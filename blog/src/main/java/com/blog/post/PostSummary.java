package com.blog.post;

import java.time.LocalDateTime;

public record PostSummary(Long id, String title, String slug, String summary, LocalDateTime publishedAt) {

    static PostSummary from(Post post) {
        return new PostSummary(post.getId(), post.getTitle(), post.getSlug(), post.getSummary(), post.getPublishedAt());
    }
}
