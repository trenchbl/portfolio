package com.blog.web;

import com.blog.post.PostDetail;
import com.blog.post.PostNotFoundException;
import com.blog.post.PostService;
import com.blog.security.SecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(PostController.class)
@Import(SecurityConfig.class)
class PostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PostService postService;

    @Test
    void showsPublishedPostWithRenderedContent() throws Exception {
        given(postService.findPublishedBySlug("hello-world")).willReturn(new PostDetail(
                "Hello world", "hello-world", "A summary", "<p>The <strong>full</strong> content.</p>",
                LocalDateTime.of(2026, 9, 1, 9, 0)));

        mockMvc.perform(get("/posts/hello-world"))
                .andExpect(status().isOk())
                .andExpect(view().name("post"))
                .andExpect(content().string(containsString("<title>Hello world | Blog</title>")))
                .andExpect(content().string(containsString("<p>The <strong>full</strong> content.</p>")));
    }

    @Test
    void escapesHtmlInTitleAndSummary() throws Exception {
        given(postService.findPublishedBySlug("xss")).willReturn(new PostDetail(
                "<b>Title</b>", "xss", "<i>Summary</i>", "<p>ok</p>", LocalDateTime.of(2026, 9, 1, 9, 0)));

        mockMvc.perform(get("/posts/xss"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("&lt;b&gt;Title&lt;/b&gt;")))
                .andExpect(content().string(containsString("&lt;i&gt;Summary&lt;/i&gt;")));
    }

    @Test
    void unknownPostIsNotFound() throws Exception {
        given(postService.findPublishedBySlug("missing")).willThrow(PostNotFoundException.forSlug("missing"));

        mockMvc.perform(get("/posts/missing"))
                .andExpect(status().isNotFound());
    }
}
