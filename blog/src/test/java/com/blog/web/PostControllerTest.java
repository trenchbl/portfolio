package com.blog.web;

import com.blog.post.PostDetail;
import com.blog.post.PostNotFoundException;
import com.blog.post.PostService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
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
class PostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PostService postService;

    @Test
    void showsPublishedPost() throws Exception {
        given(postService.findPublishedBySlug("hello-world")).willReturn(new PostDetail(
                "Hello world", "hello-world", "A summary", "The full content.", LocalDateTime.of(2026, 9, 1, 9, 0)));

        mockMvc.perform(get("/posts/hello-world"))
                .andExpect(status().isOk())
                .andExpect(view().name("post"))
                .andExpect(content().string(containsString("<title>Hello world | Blog</title>")))
                .andExpect(content().string(containsString("The full content.")));
    }

    @Test
    void escapesHtmlInContent() throws Exception {
        given(postService.findPublishedBySlug("xss")).willReturn(new PostDetail(
                "XSS", "xss", null, "<script>alert(1)</script>", LocalDateTime.of(2026, 9, 1, 9, 0)));

        mockMvc.perform(get("/posts/xss"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("&lt;script&gt;alert(1)&lt;/script&gt;")));
    }

    @Test
    void unknownPostIsNotFound() throws Exception {
        given(postService.findPublishedBySlug("missing")).willThrow(new PostNotFoundException("missing"));

        mockMvc.perform(get("/posts/missing"))
                .andExpect(status().isNotFound());
    }
}
