package com.blog.post.categories;

import com.blog.TestcontainersConfiguration;
import com.blog.post.Post;
import com.blog.post.PostRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@Import({TestcontainersConfiguration.class, CategoryService.class})
@ActiveProfiles("test")
class CategoryServiceTest {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private PostRepository postRepository;

    @Test
    void createsCategoryWithSlugFromName() {
        categoryService.create(form("  Gr\u00FC\u00DFe aus K\u00F6ln "));

        Category category = categoryRepository.findBySlug("grusse-aus-koln").orElseThrow();
        assertThat(category.getName()).isEqualTo("Gr\u00FC\u00DFe aus K\u00F6ln");
    }

    @Test
    void listsCategoriesByName() {
        categoryService.create(form("Spring"));
        categoryService.create(form("Databases"));

        assertThat(categoryService.findAll()).extracting(AdminCategoryItem::name)
                .containsExactly("Databases", "General", "Spring");
    }

    @Test
    void rejectsNamesThatGiveAnExistingSlug() {
        assertThatThrownBy(() -> categoryService.create(form("GENERAL")))
                .isInstanceOf(InvalidCategoryNameException.class)
                .hasMessage("This category already exists");
    }

    @Test
    void rejectsNamesWithoutLettersOrDigits() {
        assertThatThrownBy(() -> categoryService.create(form("???")))
                .isInstanceOf(InvalidCategoryNameException.class);
    }

    @Test
    void deletesEmptyCategories() {
        categoryService.create(form("Empty"));
        Long id = categoryRepository.findBySlug("empty").orElseThrow().getId();

        categoryService.delete(id);

        assertThat(categoryRepository.existsById(id)).isFalse();
    }

    @Test
    void refusesToDeleteCategoriesWithPosts() {
        Category general = categoryRepository.findBySlug("general").orElseThrow();
        postRepository.save(new Post("Draft", "draft", null, "content", general));

        assertThatThrownBy(() -> categoryService.delete(general.getId()))
                .isInstanceOf(CategoryInUseException.class);
    }

    @Test
    void unknownIdIsNotFound() {
        assertThatThrownBy(() -> categoryService.delete(999L)).isInstanceOf(CategoryNotFoundException.class);
    }

    private static CategoryForm form(String name) {
        CategoryForm form = new CategoryForm();
        form.setName(name);
        return form;
    }
}
