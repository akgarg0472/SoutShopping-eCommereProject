package com.akgarg.ecommerce.controller;

import com.akgarg.ecommerce.model.entity.User;
import com.akgarg.ecommerce.service.password.PasswordService;
import com.akgarg.ecommerce.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.security.Principal;

@Controller
@RequestMapping("forgot-password")
@RequiredArgsConstructor
public class ForgotPasswordController {

    private final PasswordService passwordService;
    private final UserService userService;

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
    public String forgotPassword(Principal principal) {
        if (principal != null) {
            User user = this.userService.getUserByEmail(principal.getName());

            if (user.getRole().equals("ROLE_ADMIN")) {
                return "redirect:/admin/dashboard";
            } else if (user.getRole().equals("ROLE_USER")) {
                return "redirect:/user/dashboard";
            }
        }

        return "forgot-password";
    }

    @RequestMapping(value = "/process-forgot-password", method = RequestMethod.POST)
    public ResponseEntity<String> processForgotPassword(
            @RequestParam("email") String email,
            HttpSession session
    ) {
        return this.passwordService.processForgotPassword(email, session);
    }

    @RequestMapping(value = "/verify-otp", method = RequestMethod.POST)
    public ResponseEntity<String> verifyOTP(
            @RequestParam("otp") String otp,
            HttpSession session
    ) {
        return this.passwordService.verifyOtp(otp, session);
    }

    @RequestMapping(value = "/process", method = RequestMethod.POST)
    public ResponseEntity<String> processNewPassword(
            @RequestParam("new-password") String newPassword,
            @RequestParam("confirm-new-password") String confirmNewPassword,
            HttpSession session
    ) {
        return this.passwordService.processNewPassword(newPassword, confirmNewPassword, session);
    }

}