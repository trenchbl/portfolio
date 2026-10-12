package com.blog.post;

import java.time.LocalDateTime;
import java.util.List;

public record PostSummary(Long id, String title, String slug, String summary, LocalDateTime publishedAt,
                          CategoryLink category, List<TagLink> tags) {

    static PostSummary from(Post post) {
        return new PostSummary(post.getId(), post.getTitle(), post.getSlug(), post.getSummary(), post.getPublishedAt(),
                CategoryLink.from(post.getCategory()), TagLink.sorted(post.getTags()));
    }
}
