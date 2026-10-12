package com.blog.post;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

final class TagNames {

    static final int MAX_TAGS = 10;
    static final int MAX_LENGTH = 30;

    private static final Pattern WHITESPACE = Pattern.compile("\\s+");

    private TagNames() {
    }

    /**
     * Splits "Spring Boot, testing" into tag names, keyed by slug. Blank
     * entries are ignored, and names with the same slug count once, so
     * "Spring Boot, spring-boot" is a single tag.
     *
     * @throws InvalidTagsException with a message for the author
     */
    static Map<String, String> parse(String input) {
        Map<String, String> namesBySlug = new LinkedHashMap<>();
        if (input == null) {
            return namesBySlug;
        }
        for (String part : input.split(",")) {
            String name = WHITESPACE.matcher(part.strip()).replaceAll(" ");
            if (name.isEmpty()) {
                continue;
            }
            if (name.length() > MAX_LENGTH) {
                throw new InvalidTagsException("Keep each tag to " + MAX_LENGTH + " characters or fewer: \"" + name + "\"");
            }
            String slug = Slugs.slugify(name);
            if (slug.isEmpty()) {
                throw new InvalidTagsException("Each tag needs at least one letter or digit: \"" + name + "\"");
            }
            namesBySlug.putIfAbsent(slug, name);
        }
        if (namesBySlug.size() > MAX_TAGS) {
            throw new InvalidTagsException("Use at most " + MAX_TAGS + " tags");
        }
        return namesBySlug;
    }
}
