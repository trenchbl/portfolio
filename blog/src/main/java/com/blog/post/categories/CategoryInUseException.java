package com.blog.post.categories;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * The admin page hides the delete button for categories with posts, so this
 * only happens when two admins work at the same time or a request is crafted.
 */
@ResponseStatus(HttpStatus.CONFLICT)
public class CategoryInUseException extends RuntimeException {

    public CategoryInUseException(String name) {
        super("The category '" + name + "' still has posts");
    }
}