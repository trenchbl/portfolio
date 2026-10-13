package com.blog.web;

import com.blog.post.PostService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
class SearchController {

    private final PostService postService;

    SearchController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping("/search")
    String search(@RequestParam(name = "q", defaultValue = "") String query,
                  @RequestParam(defaultValue = "1") int page, Model model) {
        PageNumbers.requireValid(page);
        String term = query.strip();
        model.addAttribute("query", term);
        model.addAttribute("posts", PageNumbers.requireExisting(page, postService.searchPublished(term, page)));
        return "search";
    }
}
