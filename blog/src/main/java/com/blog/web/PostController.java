package com.blog.web;

import com.blog.post.PostService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
class PostController {

    private final PostService postService;

    PostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping("/posts/{slug}")
    String post(@PathVariable String slug, Model model) {
        model.addAttribute("post", postService.findPublishedBySlug(slug));
        return "post";
    }
}
