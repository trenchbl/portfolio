package com.blog.controller;

import com.blog.model.BlogPost;
import com.blog.repository.BlogPostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class HomeController {
    
    private final BlogPostRepository blogPostRepository;
    
    @Autowired
    public HomeController(BlogPostRepository blogPostRepository) {
        this.blogPostRepository = blogPostRepository;
    }
    
    @GetMapping("/")
    public String home(Model model) {
        // Fetch all blog posts ordered by creation date (newest first)
        List<BlogPost> posts = blogPostRepository.findAllByOrderByCreatedAtDesc();
        model.addAttribute("posts", posts);
        return "index";
    }
    
    @GetMapping("/about")
    public String about() {
        return "about";
    }
    
    @GetMapping("/contact")
    public String contact() {
        return "contact";
    }
    
    @GetMapping("/categories")
    public String categories() {
        return "categories";
    }
    
    @GetMapping("/archive")
    public String archive(Model model) {
        // For archive page, also fetch all posts
        List<BlogPost> posts = blogPostRepository.findAllByOrderByCreatedAtDesc();
        model.addAttribute("posts", posts);
        return "archive";
    }
}

