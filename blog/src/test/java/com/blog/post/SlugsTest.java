package com.blog.post;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

class SlugsTest {

    @ParameterizedTest
    @CsvSource(delimiter = '|', value = {
            "Hello World                  | hello-world",
            "  Spaces   everywhere  !     | spaces-everywhere",
            "Café & Crème: Part 2!        | cafe-creme-part-2",
            "Über die Straße              | uber-die-strasse",
            "Smørrebrød, Łódź             | smorrebrod-lodz",
            "Spring Boot 4.1 -- released  | spring-boot-4-1-released",
            "already-a-slug               | already-a-slug",
    })
    void turnsTitlesIntoSlugs(String title, String expected) {
        assertThat(Slugs.slugify(title)).isEqualTo(expected);
    }

    @Test
    void returnsEmptyWhenNothingIsLeft() {
        assertThat(Slugs.slugify("!!! ???")).isEmpty();
    }

    @Test
    void limitsTheLengthWithoutATrailingDash() {
        String slug = Slugs.slugify("a".repeat(199) + " bbbb");

        assertThat(slug).hasSize(Slugs.MAX_LENGTH - 1).doesNotEndWith("-");
    }
}
