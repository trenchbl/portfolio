package com.blog.user;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Creates the first admin account on startup when there are no users yet.
 * After that, the configured credentials are ignored.
 */
@Component
@EnableConfigurationProperties(InitialAdminProperties.class)
class InitialAdminCreator implements ApplicationRunner {

    static final int MIN_PASSWORD_LENGTH = 12;

    private static final Logger log = LoggerFactory.getLogger(InitialAdminCreator.class);

    private final UserAccountRepository userAccountRepository;
    private final PasswordEncoder passwordEncoder;
    private final InitialAdminProperties properties;

    InitialAdminCreator(UserAccountRepository userAccountRepository, PasswordEncoder passwordEncoder,
                        InitialAdminProperties properties) {
        this.userAccountRepository = userAccountRepository;
        this.passwordEncoder = passwordEncoder;
        this.properties = properties;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (userAccountRepository.count() > 0) {
            return;
        }
        if (!properties.isConfigured()) {
            log.warn("There are no user accounts, so nobody can log in. Set blog.admin.username and "
                    + "blog.admin.password (or BLOG_ADMIN_USERNAME and BLOG_ADMIN_PASSWORD) and restart.");
            return;
        }
        if (properties.password().length() < MIN_PASSWORD_LENGTH) {
            throw new IllegalStateException(
                    "blog.admin.password must be at least " + MIN_PASSWORD_LENGTH + " characters long");
        }
        String username = properties.username().strip();
        userAccountRepository.save(new UserAccount(username, passwordEncoder.encode(properties.password()), Role.ADMIN));
        log.info("Created the first admin account '{}'. You can remove blog.admin.password from the configuration now.",
                username);
    }
}
