package com.akgarg.ecommerce.controller;

import com.akgarg.ecommerce.entity.Product;
import com.akgarg.ecommerce.entity.User;
import com.akgarg.ecommerce.entity.UserReview;
import com.akgarg.ecommerce.repository.CategoryRepository;
import com.akgarg.ecommerce.repository.ProductRepository;
import com.akgarg.ecommerce.repository.UserRepository;
import com.akgarg.ecommerce.repository.UserReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@SuppressWarnings({"unused", "FieldCanBeLocal"})
@Controller
@RequestMapping("/product")
public class ProductController {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final UserReviewRepository userReviewRepository;


    @Autowired
    public ProductController(ProductRepository productRepository,
                             CategoryRepository categoryRepository,
                             UserRepository userRepository,
                             UserReviewRepository userReviewRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
        this.userReviewRepository = userReviewRepository;
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


    @RequestMapping("{category}/{product}")
    public String showProduct(@PathVariable("category") String categoryName,
                              @PathVariable("product") String productName,
                              Model model) {
        Product product = this.productRepository.findById(productName).get();
        List<UserReview> reviews = this.userReviewRepository.findUserReviewByProductNameContainsIgnoreCase(productName);

        int totalReviews = reviews.size();
        int totalStars = 0;
        int oneStarReviews = 0;
        int twoStarReviews = 0;
        int threeStarReviews = 0;
        int fourStarReviews = 0;
        int fiveStarReviews = 0;

        for (UserReview review : reviews) {
            totalStars += review.getRatingStar();

            switch (review.getRatingStar()) {
                case 1:
                    oneStarReviews++;
                    break;
                case 2:
                    twoStarReviews++;
                    break;
                case 3:
                    threeStarReviews++;
                    break;
                case 4:
                    fourStarReviews++;
                    break;
                case 5:
                    fiveStarReviews++;
                    break;
            }
        }

        model.addAttribute("productInfo", product);
        model.addAttribute("userReviews", reviews);
        model.addAttribute("title", "SoutShopping" + " - " + productName);

        if (totalReviews > 0) {
            model.addAttribute("totalReviewsCount", totalReviews);
            model.addAttribute("starsOutOf", (double) totalStars / totalReviews);
            model.addAttribute("totalReviewsCount", totalReviews);
            model.addAttribute("oneStarPercentage", (((double) oneStarReviews / totalReviews) * 100));
            model.addAttribute("twoStarPercentage", (((double) twoStarReviews / totalReviews) * 100));
            model.addAttribute("threeStarPercentage", (((double) threeStarReviews / totalReviews) * 100));
            model.addAttribute("fourStarPercentage", (((double) fourStarReviews / totalReviews) * 100));
            model.addAttribute("fiveStarPercentage", (((double) fiveStarReviews / totalReviews) * 100));
        } else {
            model.addAttribute("totalReviewsCount", 0);
            model.addAttribute("starsOutOf", 0);
            model.addAttribute("totalReviewsCount", 0);
            model.addAttribute("oneStarPercentage", 0);
            model.addAttribute("twoStarPercentage", 0);
            model.addAttribute("threeStarPercentage", 0);
            model.addAttribute("fourStarPercentage", 0);
            model.addAttribute("fiveStarPercentage", 0);
        }

        return "product";
    }


    @RequestMapping(value = "/add-review", method = RequestMethod.POST)
    public ResponseEntity<?> addReview(@RequestParam("pName") String productName,
                                       @RequestParam("review") String review,
                                       @RequestParam("star") int star,
                                       Principal principal) {
        if (principal != null) {
            try {
                String userEmail = principal.getName();
                User user = this.userRepository.getUserByEmail(userEmail);

                UserReview userReview = new UserReview();
                userReview.setUserName(user != null ? user.getName() : "Anonymous");
                userReview.setReviewMessage(review);
                userReview.setReviewId(userEmail.substring(0, userEmail.indexOf('@')) + (productName.length() > 75 ? productName.substring(0, 76) : productName));
                userReview.setRatingStar(star);
                userReview.setProductName(productName);
                this.userReviewRepository.save(userReview);

                return ResponseEntity.ok(true);
            } catch (Exception e) {
                return ResponseEntity.ok(false);
            }
        }

        return ResponseEntity.ok(false);
    }
}