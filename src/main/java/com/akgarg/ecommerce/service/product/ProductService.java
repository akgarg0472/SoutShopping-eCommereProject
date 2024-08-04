package com.akgarg.ecommerce.service.product;

import com.akgarg.ecommerce.model.dto.Message;
import com.akgarg.ecommerce.model.entity.Category;
import com.akgarg.ecommerce.model.entity.Product;
import com.akgarg.ecommerce.model.entity.User;
import com.akgarg.ecommerce.model.entity.UserReview;
import com.akgarg.ecommerce.repository.CategoryRepository;
import com.akgarg.ecommerce.repository.ProductRepository;
import com.akgarg.ecommerce.repository.UserReviewRepository;
import com.akgarg.ecommerce.service.firebase.FirebaseService;
import com.akgarg.ecommerce.service.user.UserService;
import com.akgarg.ecommerce.utils.EcommerceUtils;
import com.akgarg.ecommerce.utils.ImageNameConstants;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * @author Akhilesh Garg
 * @since 21-10-2022
 */
@Service
@RequiredArgsConstructor
public class ProductService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProductService.class);

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final UserReviewRepository userReviewRepository;
    private final FirebaseService firebaseService;
    private final UserService userService;

    public String addProduct(final Product product, final MultipartFile image, final HttpSession session) {
        LOGGER.info("received request to add product: {}", product);

        try {
            final Optional<Product> productOptional = this.productRepository.findById(product.getName());

            if (productOptional.isPresent()) {
                session.setAttribute("addProductResponse", new Message("Product already exists", "alert-warning"));
                return "redirect:/admin/dashboard";
            }
        } catch (Exception e) {
            LOGGER.error("error checking if product already exists for add product", e);
        }

        String imageName = "null";

        if (image != null && !image.isEmpty()) {
            final String originalImageFileName = Objects.requireNonNull(image.getOriginalFilename()).substring(
                    0,
                    image.getOriginalFilename().lastIndexOf('.')
            );
            final String extension = image.getOriginalFilename().substring(image.getOriginalFilename().lastIndexOf('.'));
            imageName = EcommerceUtils.getEncodedImageFileName(product.getName(), originalImageFileName, extension);
        }

        try {
            if (!imageName.equals("null")) {
                String uploadResult = this.firebaseService.upload(image, imageName);

                if (!uploadResult.equals("error")) {
                    product.setPicture(uploadResult);
                } else {
                    product.setPicture(ImageNameConstants.DEFAULT_PRODUCT_IMAGE);
                }
            } else {
                product.setPicture(ImageNameConstants.DEFAULT_PRODUCT_IMAGE);
            }

            this.productRepository.save(product);
            final Optional<Category> categoryOptional = this.categoryRepository.findById(product.getCategory());

            if (categoryOptional.isPresent()) {
                final Category category = categoryOptional.get();
                category.getProducts().add(product);
                this.categoryRepository.save(category);
                session.setAttribute("addProductResponse", new Message("Product added successfully", "alert-success"));
            }
        } catch (Exception e) {
            LOGGER.error("error adding product", e);
            session.setAttribute("addProductResponse", new Message("Product addition failed", "alert-danger"));
        }
        return "redirect:/admin/dashboard";
    }

    public String deleteProduct(final String productInfo, final HttpSession session) {
        LOGGER.info("received request to delete product: {}", productInfo);

        String productName = productInfo.substring(0, productInfo.lastIndexOf(','));
        String productCategory = productInfo.substring(productInfo.lastIndexOf(',') + 1);

        try {
            Category category = this.categoryRepository.getReferenceById(productCategory);
            Product product = this.productRepository.getReferenceById(productName);
            String imageURL = product.getPicture();

            if (!imageURL.equals(ImageNameConstants.DEFAULT_PRODUCT_IMAGE)) {
                boolean deleteResult = this.firebaseService.delete(imageURL);

                if (deleteResult) {
                    category.getProducts().remove(product);
                    this.categoryRepository.save(category);
                    this.productRepository.delete(product);
                    session.setAttribute(
                            "deleteProductResponse",
                            new Message("Product removed successfully", "alert-success")
                    );
                } else {
                    session.setAttribute(
                            "deleteProductResponse",
                            new Message("Product deletion failed", "alert-danger")
                    );
                }
            } else {
                category.getProducts().remove(product);
                this.categoryRepository.save(category);
                this.productRepository.delete(product);
                session.setAttribute(
                        "deleteProductResponse",
                        new Message("Product removed successfully", "alert-success")
                );
            }
        } catch (Exception e) {
            LOGGER.error("error deleting product", e);
            session.setAttribute(
                    "deleteProductResponse",
                    new Message("Product deletion failed", "alert-success")
            );
        }

        return "redirect:/admin/manage-products";
    }

    public String getAllProducts(final Model model, final Principal principal) {
        List<Product> products = this.productRepository.findAll();
        User admin = this.userService.getUserByEmail(principal.getName());

        model.addAttribute("currentlyLoggedInAdmin", admin.getName());
        model.addAttribute("allProducts", products);
        model.addAttribute("time", EcommerceUtils.getDayHour());

        return "admin/manage-products";
    }

    public String searchProducts(final String searchKeyword, final Model model, final Principal principal) {
        final List<Product> products = new ArrayList<>();
        final String[] keywords = searchKeyword.split(" ");

        for (final String string : keywords) {
            if (string.length() > 2) {
                final List<Product> category = this.productRepository.findProductsByCategoryContainsIgnoreCase(string);
                for (final Product product : category) {
                    if (!products.contains(product)) {
                        products.add(product);
                    }
                }
            }
        }

        for (final String string : keywords) {
            if (string.length() > 2) {
                final List<Product> name = this.productRepository.findProductsByNameContainsIgnoreCase(string);
                for (final Product product : name) {
                    if (!products.contains(product)) {
                        products.add(product);
                    }
                }
            }
        }

        for (final String string : keywords) {
            if (string.length() > 2) {
                final List<Product> description = this.productRepository.findProductsByDescriptionContainsIgnoreCase(string);
                for (final Product product : description) {
                    if (!products.contains(product)) {
                        products.add(product);
                    }
                }
            }
        }

        User admin = this.userService.getUserByEmail(principal.getName());
        model.addAttribute("currentlyLoggedInAdmin", admin);
        model.addAttribute("allProducts", products);
        model.addAttribute("time", EcommerceUtils.getDayHour());

        return "admin/manage-products";
    }

    public String updateProduct(
            final String previousProductName,
            final Product product,
            final MultipartFile image,
            final HttpSession session
    ) {
        LOGGER.info("received request to update product: {}", product);

        try {
            Product previousProduct = this.productRepository.getReferenceById(previousProductName);
            String imageName = "null";

            if (image != null && !image.isEmpty()) {
                String originalImageFileName = Objects.requireNonNull(image.getOriginalFilename()).substring(0, image.getOriginalFilename().lastIndexOf('.'));
                String extension = image.getOriginalFilename().substring(image.getOriginalFilename().lastIndexOf('.'));
                imageName = EcommerceUtils.getEncodedImageFileName(product.getName(), originalImageFileName, extension);
            }

            if (!imageName.equals("null")) {
                String uploadResult = this.firebaseService.upload(image, imageName);

                if (!uploadResult.equals("error")) {
                    product.setPicture(uploadResult);
                } else {
                    product.setPicture(previousProduct.getPicture());
                }
            } else {
                product.setPicture(previousProduct.getPicture());
            }

            product.setName(previousProductName);
            this.productRepository.save(product);

            session.setAttribute("productUpdateResponse", new Message("Product successfully updated", ""));
        } catch (Exception e) {
            LOGGER.error("error updating product", e);
            session.setAttribute("productUpdateResponse", new Message("Product update failed", ""));
        }

        return "redirect:/admin/manage-products";
    }

    public String getProductsOfSpecificCategory(
            final String categoryName, final Model model, final HttpSession session
    ) {
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

    public String showProduct(
            final String productName, final Model model
    ) {
        final Optional<Product> productOptional = this.productRepository.findById(productName);

        if (!productOptional.isPresent()) {
            return null;
        }

        Product product = productOptional.get();
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
                default:
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

    public ResponseEntity<Object> addReview(
            final String productName, final String review, final int star, final Principal principal
    ) {
        if (principal != null) {
            try {
                String userEmail = principal.getName();
                User user = this.userService.getUserByEmail(userEmail);

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
