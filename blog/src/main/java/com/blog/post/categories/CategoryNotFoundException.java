package com.blog.post.categories;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class CategoryNotFoundException extends RuntimeException {

    private CategoryNotFoundException(String message) {
        super(message);
    }

    public static CategoryNotFoundException forSlug(String slug) {
        return new CategoryNotFoundException("No category with slug '" + slug + "'");
    }

    public static CategoryNotFoundException forId(Long id) {
        return new CategoryNotFoundException("No category with id " + id);
    }
}