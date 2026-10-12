package com.blog.web;

import com.blog.post.PostService;
import com.blog.post.PostSummary;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

@Controller
class HomeController {

    private final PostService postService;

    HomeController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping("/")
    String home(@RequestParam(defaultValue = "1") int page, Model model) {
        if (page < 1) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        Page<PostSummary> posts = postService.findPublished(page);
        if (page > 1 && page > posts.getTotalPages()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        model.addAttribute("posts", posts);
        return "home";
    }

    @GetMapping("/about")
    String about() {
        return "about";
    }
}
