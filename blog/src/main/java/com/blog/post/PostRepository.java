package com.blog.post;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

interface PostRepository extends JpaRepository<Post, Long> {

    Page<Post> findByPublishedAtIsNotNull(Pageable pageable);

    Optional<Post> findBySlugAndPublishedAtIsNotNull(String slug);

    boolean existsBySlug(String slug);

    boolean existsBySlugAndIdNot(String slug, Long id);
}
