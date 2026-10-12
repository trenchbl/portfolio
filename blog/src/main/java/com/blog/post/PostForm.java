package com.blog.post;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * The fields an author can edit. A mutable bean, because Spring MVC binds
 * form fields to it and Thymeleaf's th:field reads it back.
 */
public class PostForm {

    @NotBlank(message = "Enter a title")
    @Size(max = 200, message = "Keep the title under 200 characters")
    private String title = "";

    @Size(max = Slugs.MAX_LENGTH, message = "Keep the slug under 200 characters")
    @Pattern(regexp = "^$|^[a-z0-9]+(-[a-z0-9]+)*$",
            message = "Use only lowercase letters, digits, and single dashes between words")
    private String slug = "";

    @Size(max = 500, message = "Keep the summary under 500 characters")
    private String summary = "";

    @NotBlank(message = "Write some content")
    @Size(max = 1_000_000, message = "The content is too long")
    private String content = "";

    private boolean published;

    static PostForm from(Post post) {
        PostForm form = new PostForm();
        form.setTitle(post.getTitle());
        form.setSlug(post.getSlug());
        form.setSummary(post.getSummary() == null ? "" : post.getSummary());
        form.setContent(post.getContent());
        form.setPublished(post.isPublished());
        return form;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isPublished() {
        return published;
    }

    public void setPublished(boolean published) {
        this.published = published;
    }
}
