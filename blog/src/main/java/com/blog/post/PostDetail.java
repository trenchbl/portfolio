package com.blog.post;

import java.time.LocalDateTime;

public record PostDetail(String title, String slug, String summary, String content, LocalDateTime publishedAt) {

    static PostDetail from(Post post) {
        return new PostDetail(post.getTitle(), post.getSlug(), post.getSummary(), post.getContent(), post.getPublishedAt());
    }
}
