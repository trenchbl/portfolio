package com.blog.web;

import com.blog.post.CategoryLink;
import com.blog.post.PostService;
import com.blog.post.PostSummary;
import com.blog.post.TagLink;
import com.blog.security.SecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.test.context.support.WithMockUser;
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

@WebMvcTest(HomeController.class)
@Import(SecurityConfig.class)
class HomeControllerTest {

    private static final PostSummary POST = new PostSummary(
            1L, "First post", "first-post", "A short summary", LocalDateTime.of(2026, 9, 1, 9, 0),
            new CategoryLink("Spring", "spring"), List.of(new TagLink("spring boot", "spring-boot")));

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PostService postService;

    @Test
    void homePageListsPublishedPosts() throws Exception {
        given(postService.findPublished(1)).willReturn(pageOf(List.of(POST), 1, 1));

        mockMvc.perform(get("/").header("Accept-Language", "de-DE"))
                .andExpect(status().isOk())
                .andExpect(view().name("home"))
                .andExpect(content().string(containsString("href=\"/posts/first-post\"")))
                .andExpect(content().string(containsString("A short summary")))
                .andExpect(content().string(containsString("1 September 2026")))
                .andExpect(content().string(not(containsString("Page 1 of"))));
    }

    @Test
    void postCardsLinkToTheirCategoryAndTags() throws Exception {
        given(postService.findPublished(1)).willReturn(pageOf(List.of(POST), 1, 1));

        mockMvc.perform(get("/"))
                .andExpect(content().string(containsString("href=\"/categories/spring\">Spring</a>")))
                .andExpect(content().string(containsString("href=\"/tags/spring-boot\">#spring boot</a>")));
    }

    @Test
    void homePageShowsEmptyState() throws Exception {
        given(postService.findPublished(1)).willReturn(Page.empty());

        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("No posts have been published yet.")));
    }

    @Test
    void firstPageLinksToTheNextPage() throws Exception {
        given(postService.findPublished(1)).willReturn(pageOf(List.of(POST), 1, 3));

        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Page 1 of 3")))
                .andExpect(content().string(containsString("href=\"/?page=2\"")))
                .andExpect(content().string(not(containsString("rel=\"prev\""))));
    }

    @Test
    void secondPageLinksBackToThePlainHomeUrl() throws Exception {
        given(postService.findPublished(2)).willReturn(pageOf(List.of(POST), 2, 3));

        mockMvc.perform(get("/").param("page", "2"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Page 2 of 3")))
                .andExpect(content().string(containsString("href=\"/\">&larr; Newer posts")))
                .andExpect(content().string(containsString("href=\"/?page=3\"")));
    }

    @Test
    void pageBeyondTheLastPageIsNotFound() throws Exception {
        given(postService.findPublished(4)).willReturn(pageOf(List.of(), 4, 3));

        mockMvc.perform(get("/").param("page", "4"))
                .andExpect(status().isNotFound());
    }

    @Test
    void pageZeroIsNotFound() throws Exception {
        mockMvc.perform(get("/").param("page", "0"))
                .andExpect(status().isNotFound());
    }

    @Test
    void visitorsSeeNoAdminLinks() throws Exception {
        given(postService.findPublished(1)).willReturn(Page.empty());

        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(content().string(not(containsString("href=\"/admin/posts\""))))
                .andExpect(content().string(not(containsString("Log out"))));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void adminsSeeAdminLinkAndLogout() throws Exception {
        given(postService.findPublished(1)).willReturn(Page.empty());

        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("href=\"/admin/posts\"")))
                .andExpect(content().string(containsString("action=\"/logout\"")))
                .andExpect(content().string(containsString("name=\"_csrf\"")));
    }

    @Test
    void aboutPageRenders() throws Exception {
        mockMvc.perform(get("/about"))
                .andExpect(status().isOk())
                .andExpect(view().name("about"));
    }

    private static Page<PostSummary> pageOf(List<PostSummary> posts, int pageNumber, int totalPages) {
        long totalElements = (long) totalPages * PostService.PAGE_SIZE;
        return new PageImpl<>(posts, PageRequest.of(pageNumber - 1, PostService.PAGE_SIZE), totalElements);
    }
}
