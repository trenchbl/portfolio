package com.blog.web;

import com.blog.post.AdminCategoryItem;
import com.blog.post.CategoryInUseException;
import com.blog.post.CategoryService;
import com.blog.post.InvalidCategoryNameException;
import com.blog.security.SecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(AdminCategoryController.class)
@Import(SecurityConfig.class)
@WithMockUser(roles = "ADMIN")
class AdminCategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CategoryService categoryService;

    @Test
    void listsCategoriesAndOnlyLetsEmptyOnesBeDeleted() throws Exception {
        given(categoryService.findAll()).willReturn(List.of(
                new AdminCategoryItem(1L, "General", "general", 3),
                new AdminCategoryItem(2L, "Empty", "empty", 0)));

        mockMvc.perform(get("/admin/categories"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/categories"))
                .andExpect(content().string(containsString("href=\"/categories/general\">General</a>")))
                .andExpect(content().string(containsString("action=\"/admin/categories/2/delete\"")))
                .andExpect(content().string(not(containsString("action=\"/admin/categories/1/delete\""))));
    }

    @Test
    void createsCategoryAndRedirects() throws Exception {
        mockMvc.perform(post("/admin/categories").with(csrf()).param("name", "Spring"))
                .andExpect(redirectedUrl("/admin/categories"))
                .andExpect(flash().attribute("message", "Category created"));

        verify(categoryService).create(argThat(form -> form.getName().equals("Spring")));
    }

    @Test
    void showsValidationErrors() throws Exception {
        mockMvc.perform(post("/admin/categories").with(csrf()).param("name", " "))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasFieldErrors("category", "name"))
                .andExpect(content().string(containsString("Enter a name")));

        verify(categoryService, never()).create(any());
    }

    @Test
    void showsAnErrorForDuplicateNames() throws Exception {
        willThrow(new InvalidCategoryNameException("This category already exists")).given(categoryService).create(any());

        mockMvc.perform(post("/admin/categories").with(csrf()).param("name", "General"))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasFieldErrorCode("category", "name", "name.invalid"))
                .andExpect(content().string(containsString("This category already exists")));
    }

    @Test
    void deletesCategoryAndRedirects() throws Exception {
        mockMvc.perform(post("/admin/categories/2/delete").with(csrf()))
                .andExpect(redirectedUrl("/admin/categories"))
                .andExpect(flash().attribute("message", "Category deleted"));

        verify(categoryService).delete(2L);
    }

    @Test
    void deletingACategoryWithPostsIsAConflict() throws Exception {
        willThrow(new CategoryInUseException("General")).given(categoryService).delete(1L);

        mockMvc.perform(post("/admin/categories/1/delete").with(csrf()))
                .andExpect(status().isConflict());
    }

    @Test
    @WithAnonymousUser
    void visitorsAreSentToTheLoginPage() throws Exception {
        mockMvc.perform(get("/admin/categories"))
                .andExpect(redirectedUrl("/login"));
    }
}
