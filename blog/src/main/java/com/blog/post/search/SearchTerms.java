package com.blog.post.search;

import java.util.Arrays;
import java.util.Locale;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Turns what a visitor types into a MySQL full-text query in boolean mode.
 */
public final class SearchTerms {

    /**
     * InnoDB does not index shorter words (innodb_ft_min_token_size).
     */
    public static final int MIN_WORD_LENGTH = 3;
    public static final int MAX_WORDS = 10;

    private static final Pattern NOT_A_WORD_CHARACTER = Pattern.compile("[^\\p{L}\\p{N}]+");

    private SearchTerms() {
    }

    /**
     * "Spring boot test" becomes "+spring* +boot* +test*": every word must
     * appear, as a whole word or as the start of a longer one. Everything
     * except letters and digits is dropped, so visitors cannot use MySQL's
     * search operators (+ - * " and others) to break the query.
     *
     * @return an empty string if no word is long enough to search for
     */
    public static String toBooleanQuery(String text) {
        if (text == null) {
            return "";
        }
        return Arrays.stream(NOT_A_WORD_CHARACTER.split(text.toLowerCase(Locale.ROOT)))
                .filter(word -> word.length() >= MIN_WORD_LENGTH)
                .distinct()
                .limit(MAX_WORDS)
                .map(word -> "+" + word + "*")
                .collect(Collectors.joining(" "));
    }
}