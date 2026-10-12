package com.blog.post;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class PostService {

    public static final int PAGE_SIZE = 5;

    private static final Sort NEWEST_FIRST = Sort.by(Sort.Order.desc("publishedAt"), Sort.Order.desc("id"));

    private final PostRepository postRepository;

    PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    /**
     * @param pageNumber 1-based page number, as shown to visitors
     */
    public Page<PostSummary> findPublished(int pageNumber) {
        PageRequest pageRequest = PageRequest.of(pageNumber - 1, PAGE_SIZE, NEWEST_FIRST);
        return postRepository.findByPublishedAtIsNotNull(pageRequest).map(PostSummary::from);
    }

    public PostDetail findPublishedBySlug(String slug) {
        return postRepository.findBySlugAndPublishedAtIsNotNull(slug)
                .map(PostDetail::from)
                .orElseThrow(() -> new PostNotFoundException(slug));
    }
}
