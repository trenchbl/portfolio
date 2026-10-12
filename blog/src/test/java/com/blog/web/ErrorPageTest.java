package com.blog.web;

import com.blog.TestcontainersConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * MockMvc does not forward errors to Spring Boot's error page, so this test
 * runs the real server to check what visitors actually see.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestcontainersConfiguration.class)
@ActiveProfiles("test")
class ErrorPageTest {

    @Value("${local.server.port}")
    private int port;

    private final HttpClient httpClient = HttpClient.newHttpClient();

    @Test
    void unknownPostShowsTheNotFoundPage() throws Exception {
        HttpResponse<String> response = get("/posts/does-not-exist");

        assertThat(response.statusCode()).isEqualTo(404);
        assertThat(response.body()).contains("Page not found").contains("site-header");
    }

    @Test
    void unknownUrlShowsTheNotFoundPage() throws Exception {
        HttpResponse<String> response = get("/no-such-page");

        assertThat(response.statusCode()).isEqualTo(404);
        assertThat(response.body()).contains("Page not found");
    }

    private HttpResponse<String> get(String path) throws Exception {
        HttpRequest request = HttpRequest.newBuilder(URI.create("http://localhost:" + port + path))
                .header("Accept", "text/html")
                .build();
        return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }
}
