package com.blog.web;

import com.blog.post.categories.CategoryService;
import com.blog.post.tags.InvalidTagsException;
import com.blog.post.PostForm;
import com.blog.post.PostService;
import com.blog.post.SlugAlreadyUsedException;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/posts")
class AdminPostController {

    private static final String FORM_VIEW = "admin/post-form";

    private final PostService postService;
    private final CategoryService categoryService;

    AdminPostController(PostService postService, CategoryService categoryService) {
        this.postService = postService;
        this.categoryService = categoryService;
    }

    @GetMapping
    String list(@RequestParam(defaultValue = "1") int page, Model model) {
        PageNumbers.requireValid(page);
        model.addAttribute("posts", PageNumbers.requireExisting(page, postService.findAllForAdmin(page)));
        return "admin/posts";
    }

    @GetMapping("/new")
    String newPost(Model model) {
        model.addAttribute("post", new PostForm());
        return showForm(model);
    }

    @PostMapping
    String create(@Valid @ModelAttribute("post") PostForm form, BindingResult bindingResult,
                  Model model, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return showForm(model);
        }
        try {
            postService.create(form);
        } catch (SlugAlreadyUsedException e) {
            bindingResult.rejectValue("slug", "slug.taken", "Another post already uses this slug");
            return showForm(model);
        } catch (InvalidTagsException e) {
            bindingResult.rejectValue("tags", "tags.invalid", e.getMessage());
            return showForm(model);
        }
        redirectAttributes.addFlashAttribute("message", "Post created");
        return "redirect:/admin/posts";
    }

    @GetMapping("/{id}/edit")
    String edit(@PathVariable Long id, Model model) {
        model.addAttribute("post", postService.getForm(id));
        model.addAttribute("postId", id);
        return showForm(model);
    }

    @PostMapping("/{id}")
    String update(@PathVariable Long id, @Valid @ModelAttribute("post") PostForm form, BindingResult bindingResult,
                  Model model, RedirectAttributes redirectAttributes) {
        model.addAttribute("postId", id);
        if (bindingResult.hasErrors()) {
            return showForm(model);
        }
        try {
            postService.update(id, form);
        } catch (SlugAlreadyUsedException e) {
            bindingResult.rejectValue("slug", "slug.taken", "Another post already uses this slug");
            return showForm(model);
        } catch (InvalidTagsException e) {
            bindingResult.rejectValue("tags", "tags.invalid", e.getMessage());
            return showForm(model);
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

    /**
     * The form needs the choices for its category list and tag hint, also
     * when it is shown again with validation errors.
     */
    private String showForm(Model model) {
        model.addAttribute("categories", categoryService.findAll());
        model.addAttribute("existingTags", postService.findAllTagNames());
        return FORM_VIEW;
    }
}
