package com.blog.post;

import com.blog.TestcontainersConfiguration;
import com.blog.markdown.MarkdownRenderer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@Import({TestcontainersConfiguration.class, PostService.class, MarkdownRenderer.class})
@ActiveProfiles("test")
class PostServiceTest {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private PostService postService;

    private Category general;

    @BeforeEach
    void loadDefaultCategory() {
        general = categoryRepository.findBySlug("general").orElseThrow();
    }

    @Test
    void listsOnlyPublishedPostsNewestFirst() {
        savePublished("Older", LocalDateTime.of(2026, 1, 1, 10, 0));
        savePublished("Newer", LocalDateTime.of(2026, 2, 1, 10, 0));
        postRepository.save(new Post("Draft", "draft", null, "content", general));

        assertThat(postService.findPublished(1).getContent())
                .extracting(PostSummary::title)
                .containsExactly("Newer", "Older");
    }

    @Test
    void splitsPostsIntoPages() {
        int total = PostService.PAGE_SIZE + 2;
        for (int i = 1; i <= total; i++) {
            savePublished("Post " + i, LocalDateTime.of(2026, 1, i, 10, 0));
        }

        Page<PostSummary> firstPage = postService.findPublished(1);
        Page<PostSummary> secondPage = postService.findPublished(2);

        assertThat(firstPage.getTotalPages()).isEqualTo(2);
        assertThat(firstPage.getContent()).hasSize(PostService.PAGE_SIZE);
        assertThat(firstPage.getContent().getFirst().title()).isEqualTo("Post " + total);
        assertThat(secondPage.getContent()).extracting(PostSummary::title).containsExactly("Post 2", "Post 1");
    }

    @Test
    void summariesIncludeCategoryAndSortedTags() {
        Post post = savePublished("Tagged", LocalDateTime.of(2026, 1, 1, 10, 0));
        post.replaceTags(List.of(tagRepository.save(new Tag("testing", "testing")),
                tagRepository.save(new Tag("Java", "java"))));

        PostSummary summary = postService.findPublished(1).getContent().getFirst();

        assertThat(summary.category()).isEqualTo(new CategoryLink("General", "general"));
        assertThat(summary.tags()).extracting(TagLink::name).containsExactly("Java", "testing");
    }

    @Test
    void findsPublishedPostBySlug() {
        savePublished("Hello world", LocalDateTime.of(2026, 1, 1, 10, 0));

        assertThat(postService.findPublishedBySlug("hello-world").title()).isEqualTo("Hello world");
    }

    @Test
    void rendersContentAsSafeHtml() {
        Post post = new Post("Markdown", "markdown", null, "Some **bold** text <script>alert(1)</script>", general);
        post.publish(LocalDateTime.of(2026, 1, 1, 10, 0));
        postRepository.save(post);

        assertThat(postService.findPublishedBySlug("markdown").contentHtml())
                .contains("<strong>bold</strong>")
                .contains("&lt;script&gt;")
                .doesNotContain("<script>");
    }

    @Test
    void doesNotFindDraftsBySlug() {
        postRepository.save(new Post("Secret draft", "secret-draft", null, "content", general));

        assertThatThrownBy(() -> postService.findPublishedBySlug("secret-draft"))
                .isInstanceOf(PostNotFoundException.class);
    }

    @Test
    void doesNotFindUnknownSlugs() {
        assertThatThrownBy(() -> postService.findPublishedBySlug("does-not-exist"))
                .isInstanceOf(PostNotFoundException.class);
    }

    @Test
    void listsPublishedPostsByCategory() {
        Category spring = categoryRepository.save(new Category("Spring", "spring"));
        Post inSpring = savePublished("In spring", LocalDateTime.of(2026, 1, 1, 10, 0));
        inSpring.update(inSpring.getTitle(), inSpring.getSlug(), null, "content", spring);
        savePublished("In general", LocalDateTime.of(2026, 1, 2, 10, 0));

        assertThat(postService.getCategory("spring")).isEqualTo(new CategoryLink("Spring", "spring"));
        assertThat(postService.findPublishedByCategory("spring", 1).getContent())
                .extracting(PostSummary::title)
                .containsExactly("In spring");
    }

    @Test
    void listsPublishedPostsByTag() {
        Tag mysql = tagRepository.save(new Tag("mysql", "mysql"));
        savePublished("Tagged", LocalDateTime.of(2026, 1, 1, 10, 0)).replaceTags(List.of(mysql));
        savePublished("Not tagged", LocalDateTime.of(2026, 1, 2, 10, 0));

        assertThat(postService.getTag("mysql")).isEqualTo(new TagLink("mysql", "mysql"));
        assertThat(postService.findPublishedByTag("mysql", 1).getContent())
                .extracting(PostSummary::title)
                .containsExactly("Tagged");
    }

    @Test
    void unknownCategoriesAndTagsAreNotFound() {
        assertThatThrownBy(() -> postService.getCategory("nope")).isInstanceOf(CategoryNotFoundException.class);
        assertThatThrownBy(() -> postService.getTag("nope")).isInstanceOf(TagNotFoundException.class);
    }

    private Post savePublished(String title, LocalDateTime publishedAt) {
        Post post = new Post(title, title.toLowerCase().replace(' ', '-'), null, "content", general);
        post.publish(publishedAt);
        return postRepository.save(post);
    }
}
