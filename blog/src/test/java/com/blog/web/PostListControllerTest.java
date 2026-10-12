package com.blog.web;

import com.blog.post.CategoryLink;
import com.blog.post.CategoryNotFoundException;
import com.blog.post.PostService;
import com.blog.post.PostSummary;
import com.blog.post.TagLink;
import com.blog.post.TagNotFoundException;
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
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(PostListController.class)
@Import(SecurityConfig.class)
class PostListControllerTest {

    private static final CategoryLink SPRING = new CategoryLink("Spring", "spring");
    private static final TagLink MYSQL = new TagLink("mysql", "mysql");
    private static final PostSummary POST = new PostSummary(
            1L, "First post", "first-post", null, LocalDateTime.of(2026, 9, 1, 9, 0), SPRING, List.of(MYSQL));

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PostService postService;

    @Test
    void listsPostsInACategory() throws Exception {
        given(postService.getCategory("spring")).willReturn(SPRING);
        given(postService.findPublishedByCategory("spring", 1)).willReturn(pageOf(1, 1));

        mockMvc.perform(get("/categories/spring"))
                .andExpect(status().isOk())
                .andExpect(view().name("post-list"))
                .andExpect(content().string(containsString("<title>Posts in Spring | Blog</title>")))
                .andExpect(content().string(containsString("href=\"/posts/first-post\"")));
    }

    @Test
    void listsPostsWithATag() throws Exception {
        given(postService.getTag("mysql")).willReturn(MYSQL);
        given(postService.findPublishedByTag("mysql", 1)).willReturn(pageOf(1, 1));

        mockMvc.perform(get("/tags/mysql"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Posts tagged #mysql")))
                .andExpect(content().string(containsString("href=\"/posts/first-post\"")));
    }

    @Test
    void paginationStaysOnTheCategory() throws Exception {
        given(postService.getCategory("spring")).willReturn(SPRING);
        given(postService.findPublishedByCategory("spring", 2)).willReturn(pageOf(2, 3));

        mockMvc.perform(get("/categories/spring").param("page", "2"))
                .andExpect(content().string(containsString("href=\"/categories/spring\">&larr; Newer posts")))
                .andExpect(content().string(containsString("href=\"/categories/spring?page=3\"")));
    }

    @Test
    void showsAnEmptyStateForCategoriesWithoutPublishedPosts() throws Exception {
        given(postService.getCategory("spring")).willReturn(SPRING);
        given(postService.findPublishedByCategory("spring", 1)).willReturn(Page.empty());

        mockMvc.perform(get("/categories/spring"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("No posts have been published here yet.")));
    }

    @Test
    void unknownCategoriesAndTagsAreNotFound() throws Exception {
        given(postService.getCategory("nope")).willThrow(CategoryNotFoundException.forSlug("nope"));
        given(postService.getTag("nope")).willThrow(new TagNotFoundException("nope"));

        mockMvc.perform(get("/categories/nope")).andExpect(status().isNotFound());
        mockMvc.perform(get("/tags/nope")).andExpect(status().isNotFound());
    }

    @Test
    void pagesThatCannotExistAreNotFound() throws Exception {
        given(postService.getTag("mysql")).willReturn(MYSQL);
        given(postService.findPublishedByTag("mysql", 5)).willReturn(
                new PageImpl<>(List.of(), PageRequest.of(4, PostService.PAGE_SIZE), 2L * PostService.PAGE_SIZE));

        mockMvc.perform(get("/tags/mysql").param("page", "0")).andExpect(status().isNotFound());
        mockMvc.perform(get("/tags/mysql").param("page", "5")).andExpect(status().isNotFound());
    }

    private static Page<PostSummary> pageOf(int pageNumber, int totalPages) {
        long totalElements = (long) totalPages * PostService.PAGE_SIZE;
        return new PageImpl<>(List.of(POST), PageRequest.of(pageNumber - 1, PostService.PAGE_SIZE), totalElements);
    }
}
