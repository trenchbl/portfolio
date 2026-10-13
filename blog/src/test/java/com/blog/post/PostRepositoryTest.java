package com.blog.post;

import com.blog.TestcontainersConfiguration;
import com.blog.post.categories.AdminCategoryItem;
import com.blog.post.categories.Category;
import com.blog.post.categories.CategoryRepository;
import com.blog.post.tags.Tag;
import com.blog.post.tags.TagRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@Import(TestcontainersConfiguration.class)
@ActiveProfiles("test")
class PostRepositoryTest {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private EntityManager entityManager;

    private Category general;

    @BeforeEach
    void loadDefaultCategory() {
        general = categoryRepository.findBySlug("general").orElseThrow();
    }

    @Test
    void setsTimestampsOnSave() {
        Post saved = postRepository.saveAndFlush(new Post("Title", "title", null, "content", general));

        assertThat(saved.getCreatedAt()).isNotNull();
        assertThat(saved.getUpdatedAt()).isNotNull();
    }

    @Test
    void rejectsDuplicateSlugs() {
        postRepository.saveAndFlush(new Post("First", "same-slug", null, "content", general));

        assertThatThrownBy(() -> postRepository.saveAndFlush(new Post("Second", "same-slug", null, "content", general)))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void findsPublishedPostsByCategory() {
        Category spring = categoryRepository.save(new Category("Spring", "spring"));
        savePublished("In spring", spring, List.of());
        savePublished("In general", general, List.of());
        postRepository.save(new Post("Draft in spring", "draft-in-spring", null, "content", spring));

        assertThat(postRepository.findByCategory_SlugAndPublishedAtIsNotNull("spring", PageRequest.of(0, 10)))
                .extracting(Post::getTitle)
                .containsExactly("In spring");
    }

    @Test
    void findsPublishedPostsByTag() {
        Tag mysql = tagRepository.save(new Tag("mysql", "mysql"));
        Tag java = tagRepository.save(new Tag("java", "java"));
        savePublished("Both", general, List.of(mysql, java));
        savePublished("Only java", general, List.of(java));

        assertThat(postRepository.findByTags_SlugAndPublishedAtIsNotNull("mysql", PageRequest.of(0, 10)))
                .extracting(Post::getTitle)
                .containsExactly("Both");
        assertThat(postRepository.findByTags_SlugAndPublishedAtIsNotNull("java", PageRequest.of(0, 10)).getTotalElements())
                .isEqualTo(2);
    }

    @Test
    void deletesOnlyUnusedTags() {
        Tag used = tagRepository.save(new Tag("used", "used"));
        tagRepository.save(new Tag("unused", "unused"));
        savePublished("Post", general, List.of(used));

        assertThat(tagRepository.deleteUnused()).isEqualTo(1);
        assertThat(tagRepository.findAll()).extracting(Tag::getSlug).containsExactly("used");
    }

    @Test
    void countsPostsPerCategory() {
        Category empty = categoryRepository.save(new Category("Empty", "empty"));
        savePublished("One", general, List.of());
        postRepository.save(new Post("Draft", "draft", null, "content", general));

        assertThat(categoryRepository.findAllWithPostCounts()).containsExactly(
                new AdminCategoryItem(empty.getId(), "Empty", "empty", 0),
                new AdminCategoryItem(general.getId(), "General", "general", 2));
    }

    @Test
    void foreignKeyKeepsCategoriesWithPosts() {
        Category spring = categoryRepository.save(new Category("Spring", "spring"));
        savePublished("In spring", spring, List.of());
        postRepository.flush();

        // Plain SQL, because Hibernate refuses this delete before the database sees it
        assertThatThrownBy(() -> entityManager.createNativeQuery("DELETE FROM categories WHERE id = ?")
                .setParameter(1, spring.getId())
                .executeUpdate())
                .isInstanceOf(PersistenceException.class)
                .hasMessageContaining("fk_posts_category");
    }

    private void savePublished(String title, Category category, List<Tag> tags) {
        Post post = new Post(title, title.toLowerCase().replace(' ', '-'), null, "content", category);
        post.replaceTags(tags);
        post.publish(LocalDateTime.of(2026, 1, 1, 10, 0));
        postRepository.save(post);
    }
}
