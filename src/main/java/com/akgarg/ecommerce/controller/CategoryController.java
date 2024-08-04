package com.akgarg.ecommerce.controller;

import com.akgarg.ecommerce.model.entity.Category;
import com.akgarg.ecommerce.model.entity.User;
import com.akgarg.ecommerce.service.category.CategoryService;
import com.akgarg.ecommerce.service.product.ProductService;
import com.akgarg.ecommerce.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;
    private final ProductService productService;
    private final UserService userService;

    @ModelAttribute
    public void checkLoginStatus(
            final Principal principal, final Model model
    ) {
        if (principal == null) {
            model.addAttribute("isUserLoggedIn", null);
        } else {
            User user = this.userService.getUserByEmail(principal.getName());
            model.addAttribute("isUserLoggedIn", user);
            model.addAttribute("loggedInUserRole", user.getRole());
        }
    }

    /*  Admin Category APIs */
    @PostMapping(value = "/admin/add-category")
    public String addCategory(
            @ModelAttribute Category category, @RequestParam("image") MultipartFile image, HttpSession session
    ) {
        return this.categoryService.addCategory(category, image, session);
    }

    @GetMapping(value = "/admin/manage-categories")
    public String manageCategories(HttpSession session, Principal principal, Model model) {
        return this.categoryService.manageCategories(session, principal, model);
    }

    @PostMapping(value = "/admin/edit-category")
    public String updateCategory(
            @ModelAttribute Category category, @RequestParam("image") MultipartFile image, HttpSession session
    ) {
        return this.categoryService.updateCategory(category, image, session);
    }

    @GetMapping(value = "/admin/delete-category/{category}")
    public String deleteCategory(@PathVariable("category") String categoryToRemove, HttpSession session) {
        return this.categoryService.deleteCategory(categoryToRemove, session);
    }

    /* Public Category APIs */
    @RequestMapping("/category/{categoryName}")
    public String showCategoryProducts(
            @PathVariable("categoryName") String categoryName, Model model, HttpSession session
    ) {
        return this.productService.getProductsOfSpecificCategory(categoryName, model, session);
    }

}
