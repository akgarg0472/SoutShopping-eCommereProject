package com.akgarg.ecommerce.service.product;

import com.akgarg.ecommerce.model.entity.Product;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.security.Principal;

/**
 * @author Akhilesh Garg
 * @since 21-10-2022
 */
public interface ProductService {

    String addProduct(Product product, MultipartFile image, HttpSession session);

    String deleteProduct(String productInfo, HttpSession session);

    String getAllProducts(Model model, Principal principal);

    String searchProducts(String searchKeyword, Model model, Principal principal);

    String updateProduct(String previousProductName, Product product, MultipartFile image, HttpSession session);

    String getProductsOfSpecificCategory(String categoryName, Model model, HttpSession session);

    String showProduct(String categoryName, String productName, Model model);

    ResponseEntity<?> addReview(String productName, String review, int star, Principal principal);

}
