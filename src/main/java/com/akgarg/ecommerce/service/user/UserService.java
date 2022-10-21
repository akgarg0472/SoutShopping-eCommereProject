package com.akgarg.ecommerce.service.user;

import com.akgarg.ecommerce.model.entity.User;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.security.Principal;

/**
 * @author Akhilesh Garg
 * @since 21-10-2022
 */
public interface UserService {

    ResponseEntity<String> signup(User user, String confirmPassword, HttpSession session);

    User getUserByEmail(String username);

    ResponseEntity<String> verifyRegistrationOTP(final String otp, final HttpSession session);

    void dashboard(Principal principal, Model model);

    void updateUserProfile(User user, MultipartFile image, HttpSession session, Model model, Principal principal);

    void userOrders(Principal principal, Model model);

    void purchaseHistory(Principal principal, Model model);

    void profile(Principal principal, Model model);

    void shippingInfo(Principal principal, Model model);

    void updateProfile(Principal principal, Model model);

    void updatePassword(String currentPassword, String newPassword, String confirmNewPassword, HttpSession session, Principal principal, Model model);

    ResponseEntity<?> userCartItems(Principal principal);

    ResponseEntity<?> addItemInUserCart(String productName, String originalPrice, String discountedPrice, String totalQuantities, Principal principal);

    void deleteCartItem(String id, Principal principal, Model model);

    String buyProduct(HttpSession session, Model model, Principal principal);

    ResponseEntity<Boolean> processBuyProduct(String productName, String orderPrice, String discountPrice, int tQuantity, Principal principal, HttpSession session);

}
