package com.blog.user;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

class InitialAdminCreatorTest {

    private static final String PASSWORD = "a-long-enough-password";

    private final UserAccountRepository repository = mock(UserAccountRepository.class);
    private final PasswordEncoder passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();

    @Test
    void createsTheAdminWithAHashedPasswordWhenThereAreNoUsers() {
        given(repository.count()).willReturn(0L);

        creator(" admin ", PASSWORD).run(null);

        verify(repository).save(argThat(account ->
                account.getUsername().equals("admin")
                        && account.getRole() == Role.ADMIN
                        && account.getPasswordHash().startsWith("{bcrypt}")
                        && passwordEncoder.matches(PASSWORD, account.getPasswordHash())));
    }

    @Test
    void doesNothingWhenUsersExist() {
        given(repository.count()).willReturn(1L);

        creator("admin", PASSWORD).run(null);

        verify(repository, never()).save(any());
    }

    @Test
    void doesNothingWhenNotConfigured() {
        given(repository.count()).willReturn(0L);

        creator(null, null).run(null);
        creator("admin", "  ").run(null);

        verify(repository, never()).save(any());
    }

    @Test
    void refusesShortPasswords() {
        given(repository.count()).willReturn(0L);

        assertThatThrownBy(() -> creator("admin", "short").run(null))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("at least " + InitialAdminCreator.MIN_PASSWORD_LENGTH);
        verify(repository, never()).save(any());
    }

    @Test
    void passwordHashesAreSalted() {
        assertThat(passwordEncoder.encode(PASSWORD)).isNotEqualTo(passwordEncoder.encode(PASSWORD));
    }

    private InitialAdminCreator creator(String username, String password) {
        return new InitialAdminCreator(repository, passwordEncoder, new InitialAdminProperties(username, password));
    }
}
