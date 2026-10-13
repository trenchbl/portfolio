package com.blog.post.categories;

public record CategoryLink(String name, String slug) {

    public static CategoryLink from(Category category) {
        return new CategoryLink(category.getName(), category.getSlug());
    }
}