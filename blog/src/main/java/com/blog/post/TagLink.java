package com.blog.post;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;

public record TagLink(String name, String slug) {

    static TagLink from(Tag tag) {
        return new TagLink(tag.getName(), tag.getSlug());
    }

    /**
     * Tags are stored in a Set, which has no order, so they are sorted here
     * to show them the same way every time.
     */
    static List<TagLink> sorted(Collection<Tag> tags) {
        return tags.stream()
                .map(TagLink::from)
                .sorted(Comparator.comparing(TagLink::name, String.CASE_INSENSITIVE_ORDER))
                .toList();
    }
}
