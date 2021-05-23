package com.akgarg.ecommerce.controller;

import com.akgarg.ecommerce.entity.Product;
import com.akgarg.ecommerce.entity.User;
import com.akgarg.ecommerce.repository.CategoryRepository;
import com.akgarg.ecommerce.repository.ProductRepository;
import com.akgarg.ecommerce.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;
import java.security.Principal;
import java.util.List;

@SuppressWarnings({"unused", "FieldCanBeLocal"})
@Controller
@RequestMapping("/category")
public class CategoryController {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;


    @Autowired
    public CategoryController(ProductRepository productRepository,
                              CategoryRepository categoryRepository,
                              UserRepository userRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
    }


    @ModelAttribute
    public void checkLoginStatus(Principal principal,
                                 Model model) {
        if (principal == null) {
            model.addAttribute("isUserLoggedIn", null);
        } else {
            User user = this.userRepository.getUserByEmail(principal.getName());
            model.addAttribute("isUserLoggedIn", user);
            model.addAttribute("loggedInUserRole", user.getRole());
        }
    }


    @RequestMapping("/{categoryName}")
    public String showCategoryProducts(@PathVariable("categoryName") String categoryName,
                                       Model model,
                                       HttpSession session) {
        List<Product> products;

        if (categoryName.equals("all")) {
            products = this.productRepository.findAll();
        } else {
            products = this.productRepository.findProductByCategory(categoryName);
        }

        session.setAttribute("categories", this.categoryRepository.findAll());
        model.addAttribute("searchCategory", categoryName);
        model.addAttribute("categoryProducts", products);
        model.addAttribute("title", "SoutShopping - " + categoryName);

        return "category";
    }
}