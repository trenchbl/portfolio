package com.blog.post;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TagNamesTest {

    @Test
    void splitsOnCommasAndKeysBySlug() {
        assertThat(TagNames.parse("Spring Boot, testing"))
                .containsExactly(Map.entry("spring-boot", "Spring Boot"), Map.entry("testing", "testing"));
    }

    @Test
    void ignoresBlanksAndCollapsesWhitespace() {
        assertThat(TagNames.parse(" ,  spring    boot ,,")).containsExactly(Map.entry("spring-boot", "spring boot"));
    }

    @Test
    void keepsTheFirstNameForTheSameSlug() {
        assertThat(TagNames.parse("MySQL, mysql, my-sql")).containsOnlyKeys("mysql", "my-sql");
        assertThat(TagNames.parse("MySQL, mysql")).containsExactly(Map.entry("mysql", "MySQL"));
    }

    @Test
    void emptyInputMeansNoTags() {
        assertThat(TagNames.parse("")).isEmpty();
        assertThat(TagNames.parse(null)).isEmpty();
    }

    @Test
    void rejectsLongTags() {
        assertThatThrownBy(() -> TagNames.parse("x".repeat(TagNames.MAX_LENGTH + 1)))
                .isInstanceOf(InvalidTagsException.class)
                .hasMessageContaining(TagNames.MAX_LENGTH + " characters");
    }

    @Test
    void rejectsTagsWithoutLettersOrDigits() {
        assertThatThrownBy(() -> TagNames.parse("ok, ???"))
                .isInstanceOf(InvalidTagsException.class)
                .hasMessageContaining("\"???\"");
    }

    @Test
    void rejectsTooManyTags() {
        String elevenTags = String.join(",", "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k");

        assertThatThrownBy(() -> TagNames.parse(elevenTags))
                .isInstanceOf(InvalidTagsException.class)
                .hasMessage("Use at most " + TagNames.MAX_TAGS + " tags");
    }
}
