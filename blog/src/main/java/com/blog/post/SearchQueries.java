package com.blog.post;

import java.util.Locale;

/**
 * Turns a visitor's search into a LIKE pattern. {@code %} and {@code _}
 * are escaped, so they are ordinary characters and cannot match every post.
 */
final class SearchQueries {

    static final int MAX_LENGTH = 100;

    private SearchQueries() {
    }

    /**
     * {@code "MySQL_8"} becomes {@code "%mysql\\_8%"}. Blank input becomes
     * empty, so the service can skip the database.
     */
    static String likePattern(String query) {
        String term = normalize(query);
        if (term.isEmpty()) {
            return "";
        }
        String escaped = term.replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_");
        return "%" + escaped + "%";
    }

    static String normalize(String query) {
        if (query == null) {
            return "";
        }
        String term = query.strip().toLowerCase(Locale.ROOT);
        if (term.length() > MAX_LENGTH) {
            return term.substring(0, MAX_LENGTH);
        }
        return term;
    }
}
