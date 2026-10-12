package com.blog.post;

import com.blog.TestcontainersConfiguration;
import com.blog.markdown.MarkdownRenderer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@Import({TestcontainersConfiguration.class, PostService.class, MarkdownRenderer.class})
@ActiveProfiles("test")
class PostServiceTest {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private PostService postService;

    @Test
    void listsOnlyPublishedPostsNewestFirst() {
        savePublished("Older", LocalDateTime.of(2026, 1, 1, 10, 0));
        savePublished("Newer", LocalDateTime.of(2026, 2, 1, 10, 0));
        postRepository.save(new Post("Draft", "draft", null, "content"));

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
    void findsPublishedPostBySlug() {
        savePublished("Hello world", LocalDateTime.of(2026, 1, 1, 10, 0));

        assertThat(postService.findPublishedBySlug("hello-world").title()).isEqualTo("Hello world");
    }

    @Test
    void rendersContentAsSafeHtml() {
        Post post = new Post("Markdown", "markdown", null, "Some **bold** text <script>alert(1)</script>");
        post.publish(LocalDateTime.of(2026, 1, 1, 10, 0));
        postRepository.save(post);

        assertThat(postService.findPublishedBySlug("markdown").contentHtml())
                .contains("<strong>bold</strong>")
                .contains("&lt;script&gt;")
                .doesNotContain("<script>");
    }

    @Test
    void doesNotFindDraftsBySlug() {
        postRepository.save(new Post("Secret draft", "secret-draft", null, "content"));

        assertThatThrownBy(() -> postService.findPublishedBySlug("secret-draft"))
                .isInstanceOf(PostNotFoundException.class);
    }

    @Test
    void doesNotFindUnknownSlugs() {
        assertThatThrownBy(() -> postService.findPublishedBySlug("does-not-exist"))
                .isInstanceOf(PostNotFoundException.class);
    }

    private void savePublished(String title, LocalDateTime publishedAt) {
        Post post = new Post(title, title.toLowerCase().replace(' ', '-'), null, "content");
        post.publish(publishedAt);
        postRepository.save(post);
    }
}
