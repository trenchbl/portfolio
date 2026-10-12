package com.blog.web;

import com.blog.post.AdminPostItem;
import com.blog.post.PostForm;
import com.blog.post.PostService;
import com.blog.post.SlugAlreadyUsedException;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/posts")
class AdminPostController {

    private static final String FORM_VIEW = "admin/post-form";

    private final PostService postService;

    AdminPostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping
    String list(@RequestParam(defaultValue = "1") int page, Model model) {
        if (page < 1) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        Page<AdminPostItem> posts = postService.findAllForAdmin(page);
        if (page > 1 && page > posts.getTotalPages()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        model.addAttribute("posts", posts);
        return "admin/posts";
    }

    @GetMapping("/new")
    String newPost(Model model) {
        model.addAttribute("post", new PostForm());
        return FORM_VIEW;
    }

    @PostMapping
    String create(@Valid @ModelAttribute("post") PostForm form, BindingResult bindingResult,
                  RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return FORM_VIEW;
        }
        try {
            postService.create(form);
        } catch (SlugAlreadyUsedException e) {
            bindingResult.rejectValue("slug", "slug.taken", "Another post already uses this slug");
            return FORM_VIEW;
        }
        redirectAttributes.addFlashAttribute("message", "Post created");
        return "redirect:/admin/posts";
    }

    @GetMapping("/{id}/edit")
    String edit(@PathVariable Long id, Model model) {
        model.addAttribute("post", postService.getForm(id));
        model.addAttribute("postId", id);
        return FORM_VIEW;
    }

    @PostMapping("/{id}")
    String update(@PathVariable Long id, @Valid @ModelAttribute("post") PostForm form, BindingResult bindingResult,
                  Model model, RedirectAttributes redirectAttributes) {
        model.addAttribute("postId", id);
        if (bindingResult.hasErrors()) {
            return FORM_VIEW;
        }
        try {
            postService.update(id, form);
        } catch (SlugAlreadyUsedException e) {
            bindingResult.rejectValue("slug", "slug.taken", "Another post already uses this slug");
            return FORM_VIEW;
        }
        redirectAttributes.addFlashAttribute("message", "Post saved");
        return "redirect:/admin/posts";
    }

    @PostMapping("/{id}/delete")
    String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        postService.delete(id);
        redirectAttributes.addFlashAttribute("message", "Post deleted");
        return "redirect:/admin/posts";
    }

    @GetMapping("/{id}/preview")
    String preview(@PathVariable Long id, Model model) {
        model.addAttribute("post", postService.getPreview(id));
        model.addAttribute("preview", true);
        return "post";
    }
}
