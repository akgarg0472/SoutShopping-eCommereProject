package com.akgarg.ecommerce.controller;

import com.akgarg.ecommerce.model.entity.User;
import com.akgarg.ecommerce.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.security.Principal;

@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

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

    @GetMapping(value = "/dashboard")
    public String dashboard(
            Principal principal, Model model
    ) {
        this.userService.dashboard(principal, model);

        return "user/dashboard";
    }

    @PostMapping(value = "/process-update-profile")
    public String updateUserProfile(
            @ModelAttribute User user,
            @RequestParam("image") MultipartFile image,
            HttpSession session,
            Model model,
            Principal principal
    ) {
        this.userService.updateUserProfile(user, image, session, model, principal);

        return "redirect:/user/update-profile";
    }

    @GetMapping(value = "/my-orders")
    public String myOrders(
            Principal principal, Model model
    ) {
        this.userService.userOrders(principal, model);

        return "user/my-orders";
    }

    @GetMapping(value = "/purchase-history")
    public String purchaseHistory(
            Principal principal, Model model
    ) {
        this.userService.purchaseHistory(principal, model);

        return "user/purchase-history";
    }

    @GetMapping(value = "/my-profile")
    public String myProfile(
            Principal principal, Model model
    ) {
        this.userService.profile(principal, model);

        return "user/my-profile";
    }

    @GetMapping(value = "/shipping-info")
    public String shippingDetails(
            Principal principal, Model model
    ) {
        this.userService.shippingInfo(principal, model);

        return "user/shipping-info";
    }

    @GetMapping(value = "/update-profile")
    public String updateProfile(
            Principal principal, Model model
    ) {
        this.userService.updateProfile(principal, model);

        return "user/update-profile";
    }

    @PostMapping(value = "/update-password")
    public String updatePassword(
            @RequestParam("current-password") String currentPassword,
            @RequestParam("new-password") String newPassword,
            @RequestParam("confirm-new-password") String confirmNewPassword,
            HttpSession session,
            Principal principal,
            Model model
    ) {
        this.userService.updatePassword(currentPassword, newPassword, confirmNewPassword, session, principal, model);

        return "redirect:/user/my-profile";
    }

    @GetMapping(value = "/getUserCartItem")
    public ResponseEntity<?> getUserCartItems(Principal principal) {
        return this.userService.userCartItems(principal);
    }

    @PostMapping(value = "/add-cart-item")
    public ResponseEntity<Boolean> addItemInUserCart(
            @RequestParam("name") String productName,
            @RequestParam("originalPrice") String originalPrice,
            @RequestParam("discountedPrice") String discountedPrice,
            @RequestParam("totalQuantities") String totalQuantities,
            Principal principal
    ) {
        return this.userService.addItemInUserCart(productName, discountedPrice, totalQuantities, principal);
    }

    @GetMapping(value = "/delete-cart-order/{orderId}")
    public String deleteCartItem(
            @PathVariable("orderId") String id, Principal principal, Model model
    ) {
        this.userService.deleteCartItem(id, principal);

        return "redirect:/cart";
    }

    @RequestMapping("/buy-product-checkout")
    public String buyProduct(HttpSession session, Model model, Principal principal) {
        return this.userService.buyProduct(session, model, principal);
    }

    @PostMapping(value = "/process-buy-product")
    public ResponseEntity<Boolean> processBuyProduct(
            @RequestParam("name") String productName,
            @RequestParam("originalPrice") String orderPrice,
            @RequestParam("discountedPrice") String discountPrice,
            @RequestParam("totalQuantities") int tQuantity,
            Principal principal,
            HttpSession session
    ) {
        return this.userService.processBuyProduct(productName, orderPrice, discountPrice, tQuantity, principal, session);
    }

    @GetMapping(value = "/logout")
    public String logout() {
        return "redirect:/logout";
    }

}