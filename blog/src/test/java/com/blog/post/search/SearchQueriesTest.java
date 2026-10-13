package com.blog.post.search;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SearchQueriesTest {

    @Test
    void wrapsTheTermInPercentSignsAndLowerCasesIt() {
        assertThat(SearchQueries.likePattern("  MySQL ")).isEqualTo("%mysql%");
    }

    @Test
    void escapesLikeWildcardsAndBackslashes() {
        assertThat(SearchQueries.likePattern("100%_done\\")).isEqualTo("%100\\%\\_done\\\\%");
    }

    @Test
    void blankInputIsEmpty() {
        assertThat(SearchQueries.likePattern("   ")).isEmpty();
        assertThat(SearchQueries.likePattern(null)).isEmpty();
        assertThat(SearchQueries.normalize(null)).isEmpty();
    }

    @Test
    void longQueriesAreCutToTheLimit() {
        String tooLong = "a".repeat(SearchQueries.MAX_LENGTH + 20);

        assertThat(SearchQueries.normalize(tooLong)).hasSize(SearchQueries.MAX_LENGTH);
        assertThat(SearchQueries.likePattern(tooLong))
                .isEqualTo("%" + "a".repeat(SearchQueries.MAX_LENGTH) + "%");
    }
}
