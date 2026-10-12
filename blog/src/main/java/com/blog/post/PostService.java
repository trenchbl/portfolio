package com.blog.post;

import com.blog.markdown.MarkdownRenderer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional(readOnly = true)
public class PostService {

    public static final int PAGE_SIZE = 5;
    public static final int ADMIN_PAGE_SIZE = 20;

    private static final Sort NEWEST_FIRST = Sort.by(Sort.Order.desc("publishedAt"), Sort.Order.desc("id"));
    private static final Sort RECENTLY_UPDATED_FIRST = Sort.by(Sort.Order.desc("updatedAt"), Sort.Order.desc("id"));
    private static final String FALLBACK_SLUG = "post";

    private final PostRepository postRepository;
    private final MarkdownRenderer markdownRenderer;

    PostService(PostRepository postRepository, MarkdownRenderer markdownRenderer) {
        this.postRepository = postRepository;
        this.markdownRenderer = markdownRenderer;
    }

    /**
     * @param pageNumber 1-based page number, as shown to visitors
     */
    public Page<PostSummary> findPublished(int pageNumber) {
        PageRequest pageRequest = PageRequest.of(pageNumber - 1, PAGE_SIZE, NEWEST_FIRST);
        return postRepository.findByPublishedAtIsNotNull(pageRequest).map(PostSummary::from);
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

    public PostForm getForm(Long id) {
        return PostForm.from(getPost(id));
    }

    /**
     * Like {@link #findPublishedBySlug}, but also shows drafts.
     */
    public PostDetail getPreview(Long id) {
        return toDetail(getPost(id));
    }

    @Transactional
    public Long create(PostForm form) {
        String slug = chooseSlug(form, null);
        Post post = new Post(form.getTitle().strip(), slug, blankToNull(form.getSummary()), form.getContent());
        if (form.isPublished()) {
            post.publish(LocalDateTime.now());
        }
        return postRepository.save(post).getId();
    }

    @Transactional
    public void update(Long id, PostForm form) {
        Post post = getPost(id);
        String slug = chooseSlug(form, id);
        post.update(form.getTitle().strip(), slug, blankToNull(form.getSummary()), form.getContent());
        if (form.isPublished() && !post.isPublished()) {
            post.publish(LocalDateTime.now());
        } else if (!form.isPublished() && post.isPublished()) {
            post.unpublish();
        }
    }

    @Transactional
    public void delete(Long id) {
        postRepository.delete(getPost(id));
    }

    private Post getPost(Long id) {
        return postRepository.findById(id).orElseThrow(() -> PostNotFoundException.forId(id));
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
                post.getPublishedAt());
    }

    private static String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.strip();
    }
}
