package com.blog.post;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

interface CategoryRepository extends JpaRepository<Category, Long> {

    Optional<Category> findBySlug(String slug);

    boolean existsBySlug(String slug);

    @Query("""
            select new com.blog.post.AdminCategoryItem(c.id, c.name, c.slug, count(p))
            from Category c left join Post p on p.category = c
            group by c.id, c.name, c.slug
            order by c.name""")
    List<AdminCategoryItem> findAllWithPostCounts();
}
