package com.blog.web;

import com.blog.post.PostService;
import com.blog.post.categories.CategoryLink;
import com.blog.post.tags.TagLink;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Published posts filtered by category or tag.
 */
@Controller
class PostListController {

    private static final String VIEW = "post-list";

    private final PostService postService;

    PostListController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping("/categories/{slug}")
    String category(@PathVariable String slug, @RequestParam(defaultValue = "1") int page, Model model) {
        PageNumbers.requireValid(page);
        CategoryLink category = postService.getCategory(slug);
        model.addAttribute("heading", "Posts in " + category.name());
        model.addAttribute("baseUrl", "/categories/" + category.slug());
        model.addAttribute("posts", PageNumbers.requireExisting(page, postService.findPublishedByCategory(slug, page)));
        return VIEW;
    }

    @GetMapping("/tags/{slug}")
    String tag(@PathVariable String slug, @RequestParam(defaultValue = "1") int page, Model model) {
        PageNumbers.requireValid(page);
        TagLink tag = postService.getTag(slug);
        model.addAttribute("heading", "Posts tagged #" + tag.name());
        model.addAttribute("baseUrl", "/tags/" + tag.slug());
        model.addAttribute("posts", PageNumbers.requireExisting(page, postService.findPublishedByTag(slug, page)));
        return VIEW;
    }
}
