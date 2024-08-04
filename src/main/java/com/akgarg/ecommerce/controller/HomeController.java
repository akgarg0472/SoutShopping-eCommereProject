package com.akgarg.ecommerce.controller;

import com.akgarg.ecommerce.model.entity.User;
import com.akgarg.ecommerce.service.home.HomeService;
import com.akgarg.ecommerce.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;

import javax.servlet.http.HttpSession;
import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final UserService userService;
    private final HomeService homeService;

    @ModelAttribute
    public void checkLoginStatus(
            Principal principal, Model model
    ) {
        if (principal == null) {
            model.addAttribute("isUserLoggedIn", null);
        } else {
            User user = this.userService.getUserByEmail(principal.getName());
            model.addAttribute("isUserLoggedIn", user);
            model.addAttribute("loggedInUserRole", user.getRole());
        }
    }

    @GetMapping(value = "/")
    public String home(Model model) {
        this.homeService.home(model);
        return "home";
    }

    @GetMapping(value = {"/login", "/login.html"})
    public String login(Principal principal) {
        if (principal != null) {
            User user = this.userService.getUserByEmail(principal.getName());

            if (user.getRole().equalsIgnoreCase("ROLE_USER")) {
                return "redirect:/user/dashboard";
            } else if (user.getRole().equalsIgnoreCase("ROLE_ADMIN")) {
                return "redirect:/admin/dashboard";
            }
        }

        return "login";
    }

    @GetMapping(value = {"/register", "/register.html"})
    public String register(Principal principal) {
        if (principal != null) {
            User user = this.userService.getUserByEmail(principal.getName());

            if (user.getRole().equalsIgnoreCase("ROLE_USER")) {
                return "redirect:/user/dashboard";
            } else if (user.getRole().equalsIgnoreCase("ROLE_ADMIN")) {
                return "redirect:/admin/dashboard";
            }
        }

        return "register";
    }

    @GetMapping(value = "/cart")
    public String cart(
            Principal principal, Model model
    ) {
        this.homeService.cart(principal, model);
        return "cart";
    }

    @GetMapping(value = "/search/{searchKeywords}")
    public String searchProduct(
            @PathVariable("searchKeywords") String searchKeywords, Model model
    ) {
        this.homeService.searchProduct(searchKeywords, model);

        return "search";
    }

    @GetMapping(value = "/logout-success")
    public String logoutSuccess(HttpSession session) {
        session.setAttribute("logout", "");
        return "redirect:/login";
    }

}