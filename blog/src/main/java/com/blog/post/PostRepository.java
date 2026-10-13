package com.blog.post;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {

    Page<Post> findByPublishedAtIsNotNull(Pageable pageable);

    Page<Post> findByCategory_SlugAndPublishedAtIsNotNull(String categorySlug, Pageable pageable);

    Page<Post> findByTags_SlugAndPublishedAtIsNotNull(String tagSlug, Pageable pageable);

    /**
     * {@code pattern} is already lower-case and escaped for LIKE. The
     * backslash is the escape character, so {@code \%} matches a percent sign.
     */
    @Query("""
            select p from Post p
            where p.publishedAt is not null
              and (lower(p.title) like :pattern escape '\\'
                   or lower(p.content) like :pattern escape '\\')
            """)
    Page<Post> searchPublished(@Param("pattern") String pattern, Pageable pageable);

    Optional<Post> findBySlugAndPublishedAtIsNotNull(String slug);

    boolean existsBySlug(String slug);

    boolean existsBySlugAndIdNot(String slug, Long id);

    boolean existsByCategory_Id(Long categoryId);
}
