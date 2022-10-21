package com.akgarg.ecommerce.controller;

import com.akgarg.ecommerce.model.entity.User;
import com.akgarg.ecommerce.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.security.Principal;

@Controller
@RequestMapping("/register")
@RequiredArgsConstructor
public class RegisterController {

    private final UserService userService;

    @ModelAttribute
    public void checkLoginStatus(
            Principal principal,
            Model model
    ) {
        if (principal == null) {
            model.addAttribute("isUserLoggedIn", null);
        } else {
            final User user = this.userService.getUserByEmail(principal.getName());
            model.addAttribute("isUserLoggedIn", user);
            model.addAttribute("loggedInUserRole", user.getRole());
        }
    }

    @PostMapping(value = "/signup")
    public ResponseEntity<String> signup(
            @ModelAttribute User user,
            @RequestParam("confirm_password") String confirmPassword,
            HttpSession session
    ) {
        return this.userService.signup(user, confirmPassword, session);
    }

    @PostMapping(value = "/verify-otp")
    public ResponseEntity<String> verifyRegistrationOTP(
            @RequestParam("otp") String otp,
            HttpSession session
    ) {
        return this.userService.verifyRegistrationOTP(otp, session);
    }

}