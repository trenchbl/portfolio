package com.blog.post.categories;

import com.blog.post.PostRepository;
import com.blog.post.Slugs;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final PostRepository postRepository;

    CategoryService(CategoryRepository categoryRepository, PostRepository postRepository) {
        this.categoryRepository = categoryRepository;
        this.postRepository = postRepository;
    }

    /**
     * All categories by name, with the number of posts in each.
     */
    public List<AdminCategoryItem> findAll() {
        return categoryRepository.findAllWithPostCounts();
    }

    /**
     * The slug is always generated from the name, so "Spring Boot" and
     * "spring boot" count as the same category.
     *
     * @throws InvalidCategoryNameException if the name cannot be used
     */
    @Transactional
    public void create(CategoryForm form) {
        String name = form.getName().strip();
        String slug = Slugs.slugify(name);
        if (slug.isEmpty()) {
            throw new InvalidCategoryNameException("Use at least one letter (a-z) or digit");
        }
        if (categoryRepository.existsBySlug(slug)) {
            throw new InvalidCategoryNameException("This category already exists");
        }
        categoryRepository.save(new Category(name, slug));
    }

    /**
     * @throws CategoryInUseException if posts still belong to the category
     */
    @Transactional
    public void delete(Long id) {
        Category category = categoryRepository.findById(id).orElseThrow(() -> CategoryNotFoundException.forId(id));
        if (postRepository.existsByCategory_Id(id)) {
            throw new CategoryInUseException(category.getName());
        }
        categoryRepository.delete(category);
    }
}