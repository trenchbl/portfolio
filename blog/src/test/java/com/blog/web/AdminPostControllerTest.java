package com.blog.web;

import com.blog.post.AdminPostItem;
import com.blog.post.PostDetail;
import com.blog.post.PostForm;
import com.blog.post.PostNotFoundException;
import com.blog.post.PostService;
import com.blog.post.SlugAlreadyUsedException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(AdminPostController.class)
class AdminPostControllerTest {

    private static final LocalDateTime DATE = LocalDateTime.of(2026, 9, 1, 9, 0);

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PostService postService;

    @Test
    void listsAllPostsWithTheirStatus() throws Exception {
        given(postService.findAllForAdmin(1)).willReturn(new PageImpl<>(List.of(
                new AdminPostItem(1L, "A draft", "a-draft", null, DATE),
                new AdminPostItem(2L, "A live post", "a-live-post", DATE, DATE))));

        mockMvc.perform(get("/admin/posts"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/posts"))
                .andExpect(content().string(containsString("A draft")))
                .andExpect(content().string(containsString("Draft</span>")))
                .andExpect(content().string(containsString("href=\"/admin/posts/1/preview\"")))
                .andExpect(content().string(containsString("href=\"/posts/a-live-post\"")))
                .andExpect(content().string(containsString("action=\"/admin/posts/2/delete\"")));
    }

    @Test
    void showsAnEmptyFormForNewPosts() throws Exception {
        mockMvc.perform(get("/admin/posts/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/post-form"))
                .andExpect(content().string(containsString("<h1 class=\"page-title\">New post</h1>")))
                .andExpect(content().string(containsString("action=\"/admin/posts\"")));
    }

    @Test
    void createsPostAndRedirectsWithMessage() throws Exception {
        mockMvc.perform(post("/admin/posts")
                        .param("title", "Hello")
                        .param("slug", "")
                        .param("content", "Some **Markdown**")
                        .param("published", "true"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/posts"))
                .andExpect(flash().attribute("message", "Post created"));

        verify(postService).create(argThat(form ->
                form.getTitle().equals("Hello") && form.isPublished() && form.getContent().equals("Some **Markdown**")));
    }

    @Test
    void showsValidationErrorsAndKeepsTheInput() throws Exception {
        mockMvc.perform(post("/admin/posts")
                        .param("title", "")
                        .param("slug", "Not A Slug")
                        .param("content", "Content I don't want to lose"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/post-form"))
                .andExpect(model().attributeHasFieldErrors("post", "title", "slug"))
                .andExpect(content().string(containsString("Enter a title")))
                .andExpect(content().string(containsString("Use only lowercase letters")))
                .andExpect(content().string(containsString("Content I don&#39;t want to lose")));

        verify(postService, never()).create(any());
    }

    @Test
    void showsAnErrorWhenTheSlugIsTaken() throws Exception {
        willThrow(new SlugAlreadyUsedException("taken")).given(postService).create(any());

        mockMvc.perform(post("/admin/posts")
                        .param("title", "Hello")
                        .param("slug", "taken")
                        .param("content", "Content"))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasFieldErrorCode("post", "slug", "slug.taken"))
                .andExpect(content().string(containsString("Another post already uses this slug")));
    }

    @Test
    void showsTheEditFormFilledIn() throws Exception {
        PostForm form = new PostForm();
        form.setTitle("Existing title");
        form.setSlug("existing-title");
        form.setContent("Existing content");
        given(postService.getForm(7L)).willReturn(form);

        mockMvc.perform(get("/admin/posts/7/edit"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("<h1 class=\"page-title\">Edit post</h1>")))
                .andExpect(content().string(containsString("value=\"Existing title\"")))
                .andExpect(content().string(containsString("action=\"/admin/posts/7\"")));
    }

    @Test
    void updatesPostAndRedirects() throws Exception {
        mockMvc.perform(post("/admin/posts/7")
                        .param("title", "Changed")
                        .param("slug", "changed")
                        .param("content", "Content"))
                .andExpect(redirectedUrl("/admin/posts"))
                .andExpect(flash().attribute("message", "Post saved"));

        verify(postService).update(eq(7L), argThat(form -> form.getTitle().equals("Changed") && !form.isPublished()));
    }

    @Test
    void deletesPostAndRedirects() throws Exception {
        mockMvc.perform(post("/admin/posts/7/delete"))
                .andExpect(redirectedUrl("/admin/posts"))
                .andExpect(flash().attribute("message", "Post deleted"));

        verify(postService).delete(7L);
    }

    @Test
    void deletingIsNotPossibleWithGet() throws Exception {
        mockMvc.perform(get("/admin/posts/7/delete"))
                .andExpect(status().is4xxClientError());

        verify(postService, never()).delete(any());
    }

    @Test
    void previewsDrafts() throws Exception {
        given(postService.getPreview(7L)).willReturn(new PostDetail("Draft", "draft", null, "<p>Body</p>", null));

        mockMvc.perform(get("/admin/posts/7/preview"))
                .andExpect(status().isOk())
                .andExpect(view().name("post"))
                .andExpect(content().string(containsString("Preview of a draft")))
                .andExpect(content().string(containsString("<p>Body</p>")));
    }

    @Test
    void unknownPostIsNotFound() throws Exception {
        given(postService.getForm(999L)).willThrow(PostNotFoundException.forId(999L));

        mockMvc.perform(get("/admin/posts/999/edit"))
                .andExpect(status().isNotFound());
    }
}
