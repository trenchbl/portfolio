package com.blog.post.tags;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface TagRepository extends JpaRepository<Tag, Long> {

    Optional<Tag> findBySlug(String slug);

    List<Tag> findBySlugIn(Collection<String> slugs);

    /**
     * Removes tags that no post uses any more. Flushes first, so tag changes
     * made earlier in the transaction are already in the database.
     */
    @Modifying(flushAutomatically = true)
    @Query("delete from Tag t where not exists (select 1 from Post p join p.tags pt where pt = t)")
    int deleteUnused();
}