package com.blog.web;

import com.blog.post.CategoryLink;
import com.blog.post.PostDetail;
import com.blog.post.PostNotFoundException;
import com.blog.post.PostService;
import com.blog.post.TagLink;
import com.blog.security.SecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(PostController.class)
@Import(SecurityConfig.class)
class PostControllerTest {

    private static final LocalDateTime DATE = LocalDateTime.of(2026, 9, 1, 9, 0);
    private static final CategoryLink GENERAL = new CategoryLink("General", "general");

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PostService postService;

    @Test
    void showsPublishedPostWithRenderedContent() throws Exception {
        given(postService.findPublishedBySlug("hello-world")).willReturn(new PostDetail(
                "Hello world", "hello-world", "A summary", "<p>The <strong>full</strong> content.</p>",
                DATE, GENERAL, List.of()));

        mockMvc.perform(get("/posts/hello-world"))
                .andExpect(status().isOk())
                .andExpect(view().name("post"))
                .andExpect(content().string(containsString("<title>Hello world | Blog</title>")))
                .andExpect(content().string(containsString("<p>The <strong>full</strong> content.</p>")))
                .andExpect(content().string(not(containsString("class=\"tag-list\""))));
    }

    @Test
    void linksToCategoryAndTags() throws Exception {
        given(postService.findPublishedBySlug("tagged")).willReturn(new PostDetail(
                "Tagged", "tagged", null, "<p>ok</p>", DATE, new CategoryLink("Testing", "testing"),
                List.of(new TagLink("mysql", "mysql"), new TagLink("testcontainers", "testcontainers"))));

        mockMvc.perform(get("/posts/tagged"))
                .andExpect(content().string(containsString("href=\"/categories/testing\">Testing</a>")))
                .andExpect(content().string(containsString("href=\"/tags/mysql\">#mysql</a>")))
                .andExpect(content().string(containsString("href=\"/tags/testcontainers\">#testcontainers</a>")));
    }

    @Test
    void escapesHtmlInTitleAndSummary() throws Exception {
        given(postService.findPublishedBySlug("xss")).willReturn(new PostDetail(
                "<b>Title</b>", "xss", "<i>Summary</i>", "<p>ok</p>", DATE, GENERAL,
                List.of(new TagLink("<script>", "script"))));

        mockMvc.perform(get("/posts/xss"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("&lt;b&gt;Title&lt;/b&gt;")))
                .andExpect(content().string(containsString("&lt;i&gt;Summary&lt;/i&gt;")))
                .andExpect(content().string(containsString("#&lt;script&gt;")));
    }

    @Test
    void unknownPostIsNotFound() throws Exception {
        given(postService.findPublishedBySlug("missing")).willThrow(PostNotFoundException.forSlug("missing"));

        mockMvc.perform(get("/posts/missing"))
                .andExpect(status().isNotFound());
    }
}
