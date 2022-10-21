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

    @RequestMapping(value = "/dashboard", method = RequestMethod.GET)
    public String dashboard(
            Principal principal,
            Model model
    ) {
        this.userService.dashboard(principal, model);

        return "user/dashboard";
    }

    @RequestMapping(value = "/process-update-profile", method = RequestMethod.POST)
    public String updateUserProfile(
            @ModelAttribute User user,
            @RequestParam("image") MultipartFile image,
            HttpSession session,
            Model model,
            Principal principal
    ) {
        this.userService.updateUserProfile(
                user,
                image,
                session,
                model,
                principal
        );

        return "redirect:/user/update-profile";
    }

    @RequestMapping(value = "/my-orders", method = RequestMethod.GET)
    public String myOrders(
            Principal principal,
            Model model
    ) {
        this.userService.userOrders(principal, model);

        return "user/my-orders";
    }

    @RequestMapping(value = "/purchase-history", method = RequestMethod.GET)
    public String purchaseHistory(
            Principal principal,
            Model model
    ) {
        this.userService.purchaseHistory(principal, model);

        return "user/purchase-history";
    }

    @RequestMapping(value = "/my-profile", method = RequestMethod.GET)
    public String myProfile(
            Principal principal,
            Model model
    ) {
        this.userService.profile(principal, model);

        return "user/my-profile";
    }

    @RequestMapping(value = "/shipping-info", method = RequestMethod.GET)
    public String shippingDetails(
            Principal principal,
            Model model
    ) {
        this.userService.shippingInfo(principal, model);

        return "user/shipping-info";
    }

    @RequestMapping(value = "/update-profile", method = RequestMethod.GET)
    public String updateProfile(
            Principal principal,
            Model model
    ) {
        this.userService.updateProfile(principal, model);

        return "user/update-profile";
    }

    @RequestMapping(value = "/update-password", method = RequestMethod.POST)
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

    @RequestMapping(value = "/getUserCartItem", method = RequestMethod.GET)
    public ResponseEntity<?> getUserCartItems(Principal principal) {
        return this.userService.userCartItems(principal);
    }

    @RequestMapping(value = "/add-cart-item", method = RequestMethod.POST)
    public ResponseEntity<?> addItemInUserCart(
            @RequestParam("name") String productName,
            @RequestParam("originalPrice") String originalPrice,
            @RequestParam("discountedPrice") String discountedPrice,
            @RequestParam("totalQuantities") String totalQuantities,
            Principal principal
    ) {
        return this.userService.addItemInUserCart(productName, originalPrice, discountedPrice, totalQuantities, principal);
    }

    @RequestMapping(value = "/delete-cart-order/{orderId}", method = RequestMethod.GET)
    public String deleteCartItem(
            @PathVariable("orderId") String id,
            Principal principal,
            Model model
    ) {
        this.userService.deleteCartItem(id, principal, model);

        return "redirect:/cart";
    }

    @RequestMapping("/buy-product-checkout")
    public String buyProduct(HttpSession session, Model model, Principal principal) {
        return this.userService.buyProduct(session, model, principal);
    }

    @RequestMapping(value = "/process-buy-product", method = RequestMethod.POST)
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

    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public String logout() {
        return "redirect:/logout";
    }

}