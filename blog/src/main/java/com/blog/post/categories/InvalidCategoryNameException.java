package com.blog.post.categories;

/**
 * The message is written for authors and is shown next to the name field.
 */
public class InvalidCategoryNameException extends RuntimeException {

    public InvalidCategoryNameException(String message) {
        super(message);
    }
}