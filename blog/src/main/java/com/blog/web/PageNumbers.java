package com.blog.web;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

/**
 * Page numbers in URLs start at 1. Pages that cannot exist are 404s, so
 * search engines do not index endless empty pages.
 */
final class PageNumbers {

    private PageNumbers() {
    }

    static void requireValid(int pageNumber) {
        if (pageNumber < 1) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Page 1 always exists, so an empty list can show its "nothing here" message.
     */
    static <T> Page<T> requireExisting(int pageNumber, Page<T> page) {
        if (pageNumber > 1 && pageNumber > page.getTotalPages()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return page;
    }
}
