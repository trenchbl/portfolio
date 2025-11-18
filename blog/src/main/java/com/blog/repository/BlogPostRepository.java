package com.blog.repository;

import com.blog.model.BlogPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BlogPostRepository extends JpaRepository<BlogPost, Long> {
    
    // Spring Data JPA automatically provides:
    // - findAll()
    // - findById(Long id)
    // - save(BlogPost post)
    // - delete(BlogPost post)
    // - count()
    
    // Custom query methods
    List<BlogPost> findByAuthor(String author);
    
    List<BlogPost> findByTitleContainingIgnoreCase(String keyword);
    
    // Find all ordered by creation date descending (newest first)
    List<BlogPost> findAllByOrderByCreatedAtDesc();
}

