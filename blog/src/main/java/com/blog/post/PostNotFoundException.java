package com.blog.post;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class PostNotFoundException extends RuntimeException {

    private PostNotFoundException(String message) {
        super(message);
    }

    public static PostNotFoundException forSlug(String slug) {
        return new PostNotFoundException("No published post with slug '" + slug + "'");
    }

    public static PostNotFoundException forId(Long id) {
        return new PostNotFoundException("No post with id " + id);
    }
}
