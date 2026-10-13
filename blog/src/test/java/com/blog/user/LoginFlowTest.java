package com.blog.user;

import com.blog.TestcontainersConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * The whole chain on a real database: the first admin is created from
 * configuration, and logging in checks the password against its hash.
 */
@SpringBootTest(properties = {
        "blog.admin.username=" + LoginFlowTest.USERNAME,
        "blog.admin.password=" + LoginFlowTest.PASSWORD
})
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
@ActiveProfiles("test")
class LoginFlowTest {

    static final String USERNAME = "editor";
    static final String PASSWORD = "correct-horse-battery-staple";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserAccountRepository userAccountRepository;

    @Test
    void createsTheConfiguredAdminWithAHashedPassword() {
        UserAccount account = userAccountRepository.findByUsername(USERNAME).orElseThrow();

        assertThat(account.getRole()).isEqualTo(Role.ADMIN);
        assertThat(account.getPasswordHash()).startsWith("{bcrypt}").doesNotContain(PASSWORD);
    }

    @Test
    void loginPageIsPublic() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("name=\"_csrf\"")));
    }

    @Test
    void adminPagesRequireLogin() throws Exception {
        mockMvc.perform(get("/admin/posts"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    void wrongPasswordIsRejected() throws Exception {
        mockMvc.perform(formLogin().user(USERNAME).password("wrong-password"))
                .andExpect(redirectedUrl("/login?error"))
                .andExpect(unauthenticated());
    }

    @Test
    void unknownUserIsRejected() throws Exception {
        mockMvc.perform(formLogin().user("nobody").password(PASSWORD))
                .andExpect(redirectedUrl("/login?error"))
                .andExpect(unauthenticated());
    }

    @Test
    void loginGivesAccessToTheAdminPagesUntilLogout() throws Exception {
        MvcResult login = mockMvc.perform(formLogin().user(USERNAME).password(PASSWORD))
                .andExpect(redirectedUrl("/admin/posts"))
                .andExpect(authenticated().withUsername(USERNAME).withRoles("ADMIN"))
                .andReturn();
        MockHttpSession session = (MockHttpSession) login.getRequest().getSession(false);

        mockMvc.perform(get("/admin/posts").session(session))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Log out")));

        mockMvc.perform(post("/logout").with(csrf()).session(session))
                .andExpect(redirectedUrl("/login?logout"))
                .andExpect(unauthenticated());

        mockMvc.perform(get("/admin/posts").session(session))
                .andExpect(redirectedUrl("/login"));
    }
}
