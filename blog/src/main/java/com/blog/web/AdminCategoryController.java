package com.blog.web;

import com.blog.post.categories.CategoryForm;
import com.blog.post.categories.CategoryService;
import com.blog.post.categories.InvalidCategoryNameException;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/categories")
class AdminCategoryController {

    private static final String VIEW = "admin/categories";

    private final CategoryService categoryService;

    AdminCategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    String list(Model model) {
        model.addAttribute("category", new CategoryForm());
        return showList(model);
    }

    @PostMapping
    String create(@Valid @ModelAttribute("category") CategoryForm form, BindingResult bindingResult,
                  Model model, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return showList(model);
        }
        try {
            categoryService.create(form);
        } catch (InvalidCategoryNameException e) {
            bindingResult.rejectValue("name", "name.invalid", e.getMessage());
            return showList(model);
        }
        redirectAttributes.addFlashAttribute("message", "Category created");
        return "redirect:/admin/categories";
    }

    @PostMapping("/{id}/delete")
    String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        categoryService.delete(id);
        redirectAttributes.addFlashAttribute("message", "Category deleted");
        return "redirect:/admin/categories";
    }

    private String showList(Model model) {
        model.addAttribute("categories", categoryService.findAll());
        return VIEW;
    }
}
