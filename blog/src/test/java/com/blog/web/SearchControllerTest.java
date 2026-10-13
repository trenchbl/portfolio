package com.blog.web;

import com.blog.post.categories.CategoryLink;
import com.blog.post.PostService;
import com.blog.post.PostSummary;
import com.blog.security.SecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(SearchController.class)
@Import(SecurityConfig.class)
class SearchControllerTest {

    private static final PostSummary POST = new PostSummary(
            1L, "Why Liquibase owns the schema", "why-liquibase-owns-the-schema", null,
            LocalDateTime.of(2026, 9, 15, 18, 30),
            new CategoryLink("Databases", "databases"), List.of());

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PostService postService;

    @Test
    void emptyQueryShowsAPrompt() throws Exception {
        given(postService.searchPublished("", 1)).willReturn(Page.empty());

        mockMvc.perform(get("/search"))
                .andExpect(status().isOk())
                .andExpect(view().name("search"))
                .andExpect(content().string(containsString("<title>Search | Blog</title>")))
                .andExpect(content().string(containsString("Type a word in the search box")))
                .andExpect(content().string(not(containsString("Results for"))));
    }

    @Test
    void showsMatchingPostsAndKeepsTheQuery() throws Exception {
        given(postService.searchPublished("liquibase", 1)).willReturn(pageOf(List.of(POST), 1, 1));

        mockMvc.perform(get("/search").param("q", "liquibase"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("<title>Search: liquibase | Blog</title>")))
                .andExpect(content().string(containsString("Results for liquibase")))
                .andExpect(content().string(containsString("href=\"/posts/why-liquibase-owns-the-schema\"")))
                .andExpect(content().string(containsString("value=\"liquibase\"")));
    }

    @Test
    void showsAnEmptyStateWhenNothingMatches() throws Exception {
        given(postService.searchPublished("xyzzy", 1)).willReturn(Page.empty());

        mockMvc.perform(get("/search").param("q", "xyzzy"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("No published posts match that search.")));
    }

    @Test
    void escapesTheQueryInTheHeading() throws Exception {
        given(postService.searchPublished("<b>x</b>", 1)).willReturn(Page.empty());

        mockMvc.perform(get("/search").param("q", "<b>x</b>"))
                .andExpect(content().string(containsString("Results for &lt;b&gt;x&lt;/b&gt;")))
                .andExpect(content().string(not(containsString("Results for <b>x</b>"))));
    }

    @Test
    void paginationKeepsTheQuery() throws Exception {
        given(postService.searchPublished("needle", 2)).willReturn(pageOf(List.of(POST), 2, 3));

        mockMvc.perform(get("/search").param("q", "needle").param("page", "2"))
                .andExpect(content().string(containsString("Page 2 of 3")))
                .andExpect(content().string(containsString("href=\"/search?q=needle\">&larr; Newer posts")))
                .andExpect(content().string(containsString("href=\"/search?page=3&amp;q=needle\"")));
    }

    @Test
    void pagesThatCannotExistAreNotFound() throws Exception {
        given(postService.searchPublished("needle", 5)).willReturn(
                new PageImpl<>(List.of(), PageRequest.of(4, PostService.PAGE_SIZE), 2L * PostService.PAGE_SIZE));

        mockMvc.perform(get("/search").param("q", "needle").param("page", "0"))
                .andExpect(status().isNotFound());
        mockMvc.perform(get("/search").param("q", "needle").param("page", "5"))
                .andExpect(status().isNotFound());
    }

    @Test
    void stripsTheQueryBeforeSearching() throws Exception {
        given(postService.searchPublished("mysql", 1)).willReturn(Page.empty());

        mockMvc.perform(get("/search").param("q", "  mysql  "))
                .andExpect(content().string(containsString("value=\"mysql\"")));

        verify(postService).searchPublished("mysql", 1);
        verify(postService, never()).searchPublished("  mysql  ", 1);
    }

    private static Page<PostSummary> pageOf(List<PostSummary> posts, int pageNumber, int totalPages) {
        long totalElements = (long) totalPages * PostService.PAGE_SIZE;
        return new PageImpl<>(posts, PageRequest.of(pageNumber - 1, PostService.PAGE_SIZE), totalElements);
    }
}
