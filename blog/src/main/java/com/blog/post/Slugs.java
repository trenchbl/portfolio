package com.blog.post;

import java.text.Normalizer;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

final class Slugs {

    static final int MAX_LENGTH = 200;

    private static final Pattern DIACRITICS = Pattern.compile("\\p{M}+");
    private static final Pattern NON_ALPHANUMERIC = Pattern.compile("[^a-z0-9]+");
    private static final Pattern EDGE_DASHES = Pattern.compile("^-+|-+$");

    // Unicode normalisation splits "é" into "e" plus an accent, but these letters have no such split
    private static final Map<String, String> LETTERS_WITHOUT_DECOMPOSITION = Map.of(
            "ß", "ss",
            "æ", "ae",
            "œ", "oe",
            "ø", "o",
            "đ", "d",
            "ł", "l");

    private Slugs() {
    }

    /**
     * "Café & Crème: Part 2!" becomes "cafe-creme-part-2". Returns an empty
     * string if the text contains no letters or digits.
     */
    static String slugify(String text) {
        String lowerCase = text.toLowerCase(Locale.ROOT);
        for (Map.Entry<String, String> letter : LETTERS_WITHOUT_DECOMPOSITION.entrySet()) {
            lowerCase = lowerCase.replace(letter.getKey(), letter.getValue());
        }
        String withoutDiacritics = DIACRITICS.matcher(Normalizer.normalize(lowerCase, Normalizer.Form.NFD)).replaceAll("");
        String dashed = NON_ALPHANUMERIC.matcher(withoutDiacritics).replaceAll("-");
        String slug = EDGE_DASHES.matcher(dashed).replaceAll("");
        if (slug.length() > MAX_LENGTH) {
            slug = EDGE_DASHES.matcher(slug.substring(0, MAX_LENGTH)).replaceAll("");
        }
        return slug;
    }
}
