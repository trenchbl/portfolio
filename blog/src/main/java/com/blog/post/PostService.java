package com.blog.post;

import com.blog.markdown.MarkdownRenderer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class PostService {

    public static final int PAGE_SIZE = 5;
    public static final int ADMIN_PAGE_SIZE = 20;

    private static final Sort NEWEST_FIRST = Sort.by(Sort.Order.desc("publishedAt"), Sort.Order.desc("id"));
    private static final Sort RECENTLY_UPDATED_FIRST = Sort.by(Sort.Order.desc("updatedAt"), Sort.Order.desc("id"));
    private static final String FALLBACK_SLUG = "post";

    private final PostRepository postRepository;
    private final CategoryRepository categoryRepository;
    private final TagRepository tagRepository;
    private final MarkdownRenderer markdownRenderer;

    PostService(PostRepository postRepository, CategoryRepository categoryRepository, TagRepository tagRepository,
                MarkdownRenderer markdownRenderer) {
        this.postRepository = postRepository;
        this.categoryRepository = categoryRepository;
        this.tagRepository = tagRepository;
        this.markdownRenderer = markdownRenderer;
    }

    /**
     * @param pageNumber 1-based page number, as shown to visitors
     */
    public Page<PostSummary> findPublished(int pageNumber) {
        return postRepository.findByPublishedAtIsNotNull(publicPage(pageNumber)).map(PostSummary::from);
    }

    /**
     * @throws CategoryNotFoundException if there is no such category
     */
    public CategoryLink getCategory(String slug) {
        return categoryRepository.findBySlug(slug)
                .map(CategoryLink::from)
                .orElseThrow(() -> CategoryNotFoundException.forSlug(slug));
    }

    public Page<PostSummary> findPublishedByCategory(String categorySlug, int pageNumber) {
        return postRepository.findByCategory_SlugAndPublishedAtIsNotNull(categorySlug, publicPage(pageNumber))
                .map(PostSummary::from);
    }

    /**
     * @throws TagNotFoundException if there is no such tag
     */
    public TagLink getTag(String slug) {
        return tagRepository.findBySlug(slug)
                .map(TagLink::from)
                .orElseThrow(() -> new TagNotFoundException(slug));
    }

    public Page<PostSummary> findPublishedByTag(String tagSlug, int pageNumber) {
        return postRepository.findByTags_SlugAndPublishedAtIsNotNull(tagSlug, publicPage(pageNumber))
                .map(PostSummary::from);
    }

    /**
     * Published posts whose title or content contains the query. Blank
     * queries skip the database and return an empty page.
     */
    public Page<PostSummary> searchPublished(String query, int pageNumber) {
        String pattern = SearchQueries.likePattern(query);
        if (pattern.isEmpty()) {
            return Page.empty();
        }
        return postRepository.searchPublished(pattern, publicPage(pageNumber)).map(PostSummary::from);
    }

    public PostDetail findPublishedBySlug(String slug) {
        return postRepository.findBySlugAndPublishedAtIsNotNull(slug)
                .map(this::toDetail)
                .orElseThrow(() -> PostNotFoundException.forSlug(slug));
    }

    /**
     * All posts, drafts included. {@code pageNumber} is 1-based.
     */
    public Page<AdminPostItem> findAllForAdmin(int pageNumber) {
        PageRequest pageRequest = PageRequest.of(pageNumber - 1, ADMIN_PAGE_SIZE, RECENTLY_UPDATED_FIRST);
        return postRepository.findAll(pageRequest).map(AdminPostItem::from);
    }

    /**
     * For the hint next to the tags field in the admin form.
     */
    public List<String> findAllTagNames() {
        return tagRepository.findAll(Sort.by("name")).stream().map(Tag::getName).toList();
    }

    public PostForm getForm(Long id) {
        return PostForm.from(getPost(id));
    }

    /**
     * Like {@link #findPublishedBySlug}, but also shows drafts.
     */
    public PostDetail getPreview(Long id) {
        return toDetail(getPost(id));
    }

    /**
     * @throws InvalidTagsException     if the tags field cannot be used as it is
     * @throws SlugAlreadyUsedException if the typed slug belongs to another post
     */
    @Transactional
    public Long create(PostForm form) {
        Map<String, String> tagNames = TagNames.parse(form.getTags());
        Category category = getCategoryById(form.getCategoryId());
        String slug = chooseSlug(form, null);
        Post post = new Post(form.getTitle().strip(), slug, blankToNull(form.getSummary()), form.getContent(), category);
        post.replaceTags(findOrCreateTags(tagNames));
        if (form.isPublished()) {
            post.publish(LocalDateTime.now());
        }
        return postRepository.save(post).getId();
    }

    /**
     * @throws InvalidTagsException     if the tags field cannot be used as it is
     * @throws SlugAlreadyUsedException if the typed slug belongs to another post
     */
    @Transactional
    public void update(Long id, PostForm form) {
        Post post = getPost(id);
        Map<String, String> tagNames = TagNames.parse(form.getTags());
        Category category = getCategoryById(form.getCategoryId());
        String slug = chooseSlug(form, id);
        post.update(form.getTitle().strip(), slug, blankToNull(form.getSummary()), form.getContent(), category);
        post.replaceTags(findOrCreateTags(tagNames));
        if (form.isPublished() && !post.isPublished()) {
            post.publish(LocalDateTime.now());
        } else if (!form.isPublished() && post.isPublished()) {
            post.unpublish();
        }
        tagRepository.deleteUnused();
    }

    @Transactional
    public void delete(Long id) {
        postRepository.delete(getPost(id));
        tagRepository.deleteUnused();
    }

    private Post getPost(Long id) {
        return postRepository.findById(id).orElseThrow(() -> PostNotFoundException.forId(id));
    }

    private Category getCategoryById(Long id) {
        return categoryRepository.findById(id).orElseThrow(() -> CategoryNotFoundException.forId(id));
    }

    /**
     * Reuses the tags that already exist and creates the others.
     */
    private List<Tag> findOrCreateTags(Map<String, String> namesBySlug) {
        Map<String, Tag> existing = tagRepository.findBySlugIn(namesBySlug.keySet()).stream()
                .collect(Collectors.toMap(Tag::getSlug, Function.identity()));
        List<Tag> tags = new ArrayList<>();
        namesBySlug.forEach((slug, name) -> {
            Tag tag = existing.get(slug);
            tags.add(tag != null ? tag : tagRepository.save(new Tag(name, slug)));
        });
        return tags;
    }

    /**
     * A slug typed by the author must be free. A slug generated from the
     * title gets a number appended until it is free.
     */
    private String chooseSlug(PostForm form, Long postId) {
        String requested = form.getSlug() == null ? "" : form.getSlug().strip();
        if (!requested.isEmpty()) {
            if (isTaken(requested, postId)) {
                throw new SlugAlreadyUsedException(requested);
            }
            return requested;
        }

        String base = Slugs.slugify(form.getTitle());
        if (base.isEmpty()) {
            base = FALLBACK_SLUG;
        }
        String candidate = base;
        for (int number = 2; isTaken(candidate, postId); number++) {
            String suffix = "-" + number;
            candidate = base.substring(0, Math.min(base.length(), Slugs.MAX_LENGTH - suffix.length())) + suffix;
        }
        return candidate;
    }

    private boolean isTaken(String slug, Long postId) {
        return postId == null ? postRepository.existsBySlug(slug) : postRepository.existsBySlugAndIdNot(slug, postId);
    }

    private PostDetail toDetail(Post post) {
        return new PostDetail(
                post.getTitle(),
                post.getSlug(),
                post.getSummary(),
                markdownRenderer.toHtml(post.getContent()),
                post.getPublishedAt(),
                CategoryLink.from(post.getCategory()),
                TagLink.sorted(post.getTags()));
    }

    private static PageRequest publicPage(int pageNumber) {
        return PageRequest.of(pageNumber - 1, PAGE_SIZE, NEWEST_FIRST);
    }

    private static String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.strip();
    }
}
