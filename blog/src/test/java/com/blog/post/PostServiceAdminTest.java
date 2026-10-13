package com.blog.post;

import com.blog.TestcontainersConfiguration;
import com.blog.markdown.MarkdownRenderer;
import com.blog.post.categories.Category;
import com.blog.post.categories.CategoryNotFoundException;
import com.blog.post.categories.CategoryRepository;
import com.blog.post.tags.InvalidTagsException;
import com.blog.post.tags.Tag;
import com.blog.post.tags.TagRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@Import({TestcontainersConfiguration.class, PostService.class, MarkdownRenderer.class})
@ActiveProfiles("test")
class PostServiceAdminTest {

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
    void createsDraftWithSlugFromTitle() {
        Long id = postService.create(form("My First Post", "", false));

        Post post = postRepository.findById(id).orElseThrow();
        assertThat(post.getSlug()).isEqualTo("my-first-post");
        assertThat(post.isPublished()).isFalse();
        assertThat(post.getCategory().getSlug()).isEqualTo("general");
    }

    @Test
    void numbersGeneratedSlugsThatAreTaken() {
        postService.create(form("Same title", "", false));
        Long second = postService.create(form("Same title", "", false));
        Long third = postService.create(form("Same title", "", false));

        assertThat(postRepository.findById(second).orElseThrow().getSlug()).isEqualTo("same-title-2");
        assertThat(postRepository.findById(third).orElseThrow().getSlug()).isEqualTo("same-title-3");
    }

    @Test
    void usesFallbackSlugWhenTitleHasNoLetters() {
        Long id = postService.create(form("!!!", "", false));

        assertThat(postRepository.findById(id).orElseThrow().getSlug()).isEqualTo("post");
    }

    @Test
    void rejectsATypedSlugThatIsTaken() {
        postService.create(form("First", "taken", false));

        assertThatThrownBy(() -> postService.create(form("Second", "taken", false)))
                .isInstanceOf(SlugAlreadyUsedException.class);
    }

    @Test
    void publishesWhenCreatedAsPublished() {
        Long id = postService.create(form("Live", "", true));

        assertThat(postRepository.findById(id).orElseThrow().getPublishedAt()).isNotNull();
    }

    @Test
    void storesBlankSummaryAsNull() {
        PostForm form = form("Title", "", false);
        form.setSummary("   ");

        Long id = postService.create(form);

        assertThat(postRepository.findById(id).orElseThrow().getSummary()).isNull();
    }

    @Test
    void updatesFieldsAndKeepsTheOriginalPublishDate() {
        LocalDateTime originalDate = LocalDateTime.of(2026, 1, 1, 10, 0);
        Post post = new Post("Old title", "old-title", null, "old", general);
        post.publish(originalDate);
        Long id = postRepository.save(post).getId();

        postService.update(id, form("New title", "new-title", true));

        Post updated = postRepository.findById(id).orElseThrow();
        assertThat(updated.getTitle()).isEqualTo("New title");
        assertThat(updated.getSlug()).isEqualTo("new-title");
        assertThat(updated.getPublishedAt()).isEqualTo(originalDate);
    }

    @Test
    void keepingItsOwnSlugIsNotAConflict() {
        Long id = postService.create(form("Title", "my-slug", false));

        postService.update(id, form("Changed title", "my-slug", false));

        assertThat(postRepository.findById(id).orElseThrow().getTitle()).isEqualTo("Changed title");
    }

    @Test
    void rejectsChangingToAnotherPostsSlug() {
        postService.create(form("One", "one", false));
        Long two = postService.create(form("Two", "two", false));

        assertThatThrownBy(() -> postService.update(two, form("Two", "one", false)))
                .isInstanceOf(SlugAlreadyUsedException.class);
    }

    @Test
    void unpublishesAndRepublishes() {
        Long id = postService.create(form("Title", "", true));

        postService.update(id, form("Title", "title", false));
        assertThat(postRepository.findById(id).orElseThrow().isPublished()).isFalse();

        postService.update(id, form("Title", "title", true));
        assertThat(postRepository.findById(id).orElseThrow().isPublished()).isTrue();
    }

    @Test
    void deletesPosts() {
        Long id = postService.create(form("Doomed", "", false));

        postService.delete(id);

        assertThat(postRepository.existsById(id)).isFalse();
    }

    @Test
    void unknownIdsAreNotFound() {
        assertThatThrownBy(() -> postService.getForm(999L)).isInstanceOf(PostNotFoundException.class);
        assertThatThrownBy(() -> postService.update(999L, form("x", "", false))).isInstanceOf(PostNotFoundException.class);
        assertThatThrownBy(() -> postService.delete(999L)).isInstanceOf(PostNotFoundException.class);
    }

    @Test
    void unknownCategoryIsNotFound() {
        PostForm form = form("Title", "", false);
        form.setCategoryId(999L);

        assertThatThrownBy(() -> postService.create(form)).isInstanceOf(CategoryNotFoundException.class);
    }

    @Test
    void movesPostsToAnotherCategory() {
        Category spring = categoryRepository.save(new Category("Spring", "spring"));
        Long id = postService.create(form("Title", "", false));
        PostForm form = form("Title", "title", false);
        form.setCategoryId(spring.getId());

        postService.update(id, form);

        assertThat(postRepository.findById(id).orElseThrow().getCategory().getSlug()).isEqualTo("spring");
    }

    @Test
    void createsTagsAndReusesExistingOnes() {
        tagRepository.save(new Tag("Spring Boot", "spring-boot"));
        PostForm form = form("Title", "", false);
        form.setTags("spring-boot, MySQL ,  ,mysql");

        Long id = postService.create(form);

        assertThat(postRepository.findById(id).orElseThrow().getTags())
                .extracting(Tag::getName)
                .containsExactlyInAnyOrder("Spring Boot", "MySQL");
        assertThat(tagRepository.count()).isEqualTo(2);
    }

    @Test
    void rejectsInvalidTags() {
        PostForm form = form("Title", "", false);
        form.setTags("ok, !!!");

        assertThatThrownBy(() -> postService.create(form)).isInstanceOf(InvalidTagsException.class);
        assertThat(postRepository.count()).isZero();
    }

    @Test
    void removesTagsThatNoPostUsesAnyMore() {
        PostForm form = form("Title", "title", false);
        form.setTags("keep, drop");
        Long id = postService.create(form);
        PostForm other = form("Other", "", false);
        other.setTags("keep");
        Long otherId = postService.create(other);

        form.setTags("keep");
        postService.update(id, form);
        assertThat(tagRepository.findAll()).extracting(Tag::getSlug).containsExactly("keep");

        postService.delete(id);
        postService.delete(otherId);
        assertThat(tagRepository.count()).isZero();
    }

    @Test
    void adminListIncludesDrafts() {
        postService.create(form("Draft", "", false));
        postService.create(form("Published", "", true));

        assertThat(postService.findAllForAdmin(1).getContent())
                .extracting(AdminPostItem::title)
                .containsExactlyInAnyOrder("Draft", "Published");
    }

    @Test
    void previewShowsDrafts() {
        Long id = postService.create(form("Secret", "", false));

        assertThat(postService.getPreview(id).title()).isEqualTo("Secret");
    }

    @Test
    void formIsFilledFromThePost() {
        PostForm original = form("Title", "slug", true);
        original.setSummary("Summary");
        original.setTags("testing, Java");
        Long id = postService.create(original);

        PostForm loaded = postService.getForm(id);

        assertThat(loaded.getTitle()).isEqualTo("Title");
        assertThat(loaded.getSlug()).isEqualTo("slug");
        assertThat(loaded.getCategoryId()).isEqualTo(general.getId());
        assertThat(loaded.getTags()).isEqualTo("Java, testing");
        assertThat(loaded.getSummary()).isEqualTo("Summary");
        assertThat(loaded.getContent()).isEqualTo("Some content");
        assertThat(loaded.isPublished()).isTrue();
    }

    private PostForm form(String title, String slug, boolean published) {
        PostForm form = new PostForm();
        form.setTitle(title);
        form.setSlug(slug);
        form.setCategoryId(general.getId());
        form.setContent("Some content");
        form.setPublished(published);
        return form;
    }
}
