package com.blog.post;

public class SlugAlreadyUsedException extends RuntimeException {

    public SlugAlreadyUsedException(String slug) {
        super("The slug '" + slug + "' is already used by another post");
    }
}
