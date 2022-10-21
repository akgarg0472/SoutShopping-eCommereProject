package com.akgarg.ecommerce.controller;

import com.akgarg.ecommerce.model.entity.Product;
import com.akgarg.ecommerce.model.entity.User;
import com.akgarg.ecommerce.service.product.ProductService;
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
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
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

    /* Admin Product APIs */
    @RequestMapping(value = "/admin/add-product", method = RequestMethod.POST)
    public String addProduct(
            @ModelAttribute Product product, @RequestParam("image") MultipartFile image, HttpSession session
    ) {
        return this.productService.addProduct(product, image, session);
    }

    @RequestMapping(value = "/admin/delete-product/{product}", method = RequestMethod.GET)
    public String deleteProduct(@PathVariable("product") String productInfo, HttpSession session) {
        return this.productService.deleteProduct(productInfo, session);
    }

    @RequestMapping("/admin/manage-products")
    public String allProducts(Model model, Principal principal) {
        return this.productService.getAllProducts(model, principal);
    }

    @RequestMapping("/admin/manage-products/search/{searchKeyword}")
    public String searchProducts(
            @PathVariable("searchKeyword") String searchKeyword, Model model, Principal principal
    ) {
        return this.productService.searchProducts(searchKeyword, model, principal);
    }

    @RequestMapping(value = "/admin/edit-product/{productName}", method = RequestMethod.POST)
    public String updateProduct(
            @PathVariable("productName") String previousProductName,
            @ModelAttribute Product product,
            @RequestParam("image") MultipartFile image,
            HttpSession session
    ) {
        return this.productService.updateProduct(previousProductName, product, image, session);
    }

    /* Public Product APIs */
    @RequestMapping("/product/{category}/{product}")
    public String showProduct(
            @PathVariable("category") String categoryName,
            @PathVariable("product") String productName,
            Model model
    ) {
        return this.productService.showProduct(categoryName, productName, model);
    }

    @RequestMapping(value = "/add-review", method = RequestMethod.POST)
    public ResponseEntity<?> addReview(
            @RequestParam("pName") String productName,
            @RequestParam("review") String review,
            @RequestParam("star") int star,
            Principal principal
    ) {
        return this.productService.addReview(productName, review, star, principal);
    }

}