package com.akgarg.ecommerce.controller;

import com.akgarg.ecommerce.model.entity.User;
import com.akgarg.ecommerce.service.home.HomeService;
import com.akgarg.ecommerce.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpSession;
import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final UserService userService;
    private final HomeService homeService;

    @ModelAttribute
    public void checkLoginStatus(
            Principal principal,
            Model model
    ) {
        if (principal == null) {
            model.addAttribute("isUserLoggedIn", null);
        } else {
            User user = this.userService.getUserByEmail(principal.getName());
            model.addAttribute("isUserLoggedIn", user);
            model.addAttribute("loggedInUserRole", user.getRole());
        }
    }

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String home(Model model) {
        this.homeService.home(model);
        return "home";
    }

    @RequestMapping(value = {"/login", "/login.html"}, method = RequestMethod.GET)
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

    @RequestMapping(value = {"/register", "/register.html"}, method = RequestMethod.GET)
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

    @RequestMapping(value = "/cart", method = RequestMethod.GET)
    public String cart(
            Principal principal,
            Model model
    ) {
        this.homeService.cart(principal, model);
        return "cart";
    }

    @RequestMapping(value = "/search/{searchKeywords}", method = RequestMethod.GET)
    public String searchProduct(
            @PathVariable("searchKeywords") String searchKeywords,
            Model model
    ) {
        this.homeService.searchProduct(searchKeywords, model);

        return "search";
    }

    @RequestMapping(value = "/logout-success", method = RequestMethod.GET)
    public String logoutSuccess(HttpSession session) {
        session.setAttribute("logout", "");
        return "redirect:/login";
    }

}