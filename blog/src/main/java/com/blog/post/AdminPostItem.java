package com.blog.post;

import java.time.LocalDateTime;

public record AdminPostItem(Long id, String title, String slug, String categoryName,
                            LocalDateTime publishedAt, LocalDateTime updatedAt) {

    public boolean published() {
        return publishedAt != null;
    }

    static AdminPostItem from(Post post) {
        return new AdminPostItem(post.getId(), post.getTitle(), post.getSlug(), post.getCategory().getName(),
                post.getPublishedAt(), post.getUpdatedAt());
    }
}
