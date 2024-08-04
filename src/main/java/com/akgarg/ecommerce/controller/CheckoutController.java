package com.akgarg.ecommerce.controller;

import com.akgarg.ecommerce.model.entity.User;
import com.akgarg.ecommerce.service.checkout.CheckoutService;
import com.akgarg.ecommerce.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class CheckoutController {

    private final CheckoutService checkoutService;
    private final UserService userService;

    @ModelAttribute
    public String checkLoginStatus(
            Principal principal, Model model
    ) {
        if (principal == null) {
            model.addAttribute("isUserLoggedIn", null);
            return "redirect:/login";
        } else {
            User user = this.userService.getUserByEmail(principal.getName());
            model.addAttribute("isUserLoggedIn", user);
            model.addAttribute("loggedInUserRole", user.getRole());

            return "redirect:/process-checkout";
        }
    }

    @RequestMapping("/checkout")
    public String checkout(
            Principal principal, Model model
    ) {
        return this.checkoutService.checkout(principal, model);
    }

    @GetMapping(value = "/checkout/isUserLoggedIn")
    public ResponseEntity<Boolean> isUserLoggedIn(Principal principal) {
        return ResponseEntity.ok(principal != null);
    }

    @GetMapping(value = "/order-confirmed/{orderId}")
    public String orderConfirmed(
            @PathVariable("orderId") String orderId, Model model, Principal principal
    ) {
        return this.checkoutService.confirmOrder(orderId, model, principal);
    }

}