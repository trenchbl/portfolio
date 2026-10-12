package com.blog.post;

public record CategoryLink(String name, String slug) {

    static CategoryLink from(Category category) {
        return new CategoryLink(category.getName(), category.getSlug());
    }
}
