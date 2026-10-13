package com.blog.post.tags;

/**
 * The message is written for authors and is shown next to the tags field.
 */
public class InvalidTagsException extends RuntimeException {

    public InvalidTagsException(String message) {
        super(message);
    }
}