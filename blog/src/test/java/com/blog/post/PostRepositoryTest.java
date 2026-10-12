package com.blog.post;

import com.blog.TestcontainersConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@Import(TestcontainersConfiguration.class)
@ActiveProfiles("test")
class PostRepositoryTest {

    @Autowired
    private PostRepository postRepository;

    @Test
    void setsTimestampsOnSave() {
        Post saved = postRepository.saveAndFlush(new Post("Title", "title", null, "content"));

        assertThat(saved.getCreatedAt()).isNotNull();
        assertThat(saved.getUpdatedAt()).isNotNull();
    }

    @Test
    void rejectsDuplicateSlugs() {
        postRepository.saveAndFlush(new Post("First", "same-slug", null, "content"));

        assertThatThrownBy(() -> postRepository.saveAndFlush(new Post("Second", "same-slug", null, "content")))
                .isInstanceOf(DataIntegrityViolationException.class);
    }
}
