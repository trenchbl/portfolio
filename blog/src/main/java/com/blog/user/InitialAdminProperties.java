package com.blog.user;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Credentials for the first admin account, read from blog.admin.username and
 * blog.admin.password (or the BLOG_ADMIN_USERNAME and BLOG_ADMIN_PASSWORD
 * environment variables). Only used while the users table is empty.
 */
@ConfigurationProperties("blog.admin")
record InitialAdminProperties(String username, String password) {

    boolean isConfigured() {
        return username != null && !username.isBlank() && password != null && !password.isBlank();
    }
}
