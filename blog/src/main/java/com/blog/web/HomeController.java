package com.blog.web;

import com.blog.post.PostService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
class HomeController {

    private final PostService postService;

    HomeController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping("/")
    String home(@RequestParam(defaultValue = "1") int page, Model model) {
        PageNumbers.requireValid(page);
        model.addAttribute("posts", PageNumbers.requireExisting(page, postService.findPublished(page)));
        return "home";
    }

    @GetMapping("/about")
    String about() {
        return "about";
    }
}
