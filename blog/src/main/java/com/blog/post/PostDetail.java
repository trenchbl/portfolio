package com.blog.post;

import java.time.LocalDateTime;

/**
 * @param contentHtml the post content rendered from Markdown; safe to output unescaped
 */
public record PostDetail(String title, String slug, String summary, String contentHtml, LocalDateTime publishedAt) {
}
