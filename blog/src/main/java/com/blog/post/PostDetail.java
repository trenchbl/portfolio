package com.blog.post;

import com.blog.post.categories.CategoryLink;
import com.blog.post.tags.TagLink;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @param contentHtml the post content rendered from Markdown; safe to output unescaped
 */
public record PostDetail(String title, String slug, String summary, String contentHtml, LocalDateTime publishedAt,
                         CategoryLink category, List<TagLink> tags) {
}
