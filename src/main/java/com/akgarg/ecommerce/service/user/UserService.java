package com.akgarg.ecommerce.service.user;

import com.akgarg.ecommerce.model.dto.Message;
import com.akgarg.ecommerce.model.dto.ShippingInfo;
import com.akgarg.ecommerce.model.entity.Orders;
import com.akgarg.ecommerce.model.entity.Product;
import com.akgarg.ecommerce.model.entity.User;
import com.akgarg.ecommerce.repository.OrdersRepository;
import com.akgarg.ecommerce.repository.ProductRepository;
import com.akgarg.ecommerce.repository.UserRepository;
import com.akgarg.ecommerce.service.email.EmailService;
import com.akgarg.ecommerce.service.firebase.FirebaseService;
import com.akgarg.ecommerce.utils.EcommerceUtils;
import com.akgarg.ecommerce.utils.EmailMessages;
import com.akgarg.ecommerce.utils.ImageNameConstants;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.security.Principal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * @author Akhilesh Garg
 * @since 21-10-2022
 */
@Service
@RequiredArgsConstructor
public class UserService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final OrdersRepository ordersRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final FirebaseService firebaseService;
    private final ProductRepository productRepository;

    public ResponseEntity<String> signup(
            final User user,
            final String confirmPassword,
            final HttpSession session
    ) {
        LOGGER.info("received signup request: {}", user);

        try {
            final Optional<User> userOptional = this.userRepository.findById(user.getEmail());

            if (userOptional.isPresent()) {
                LOGGER.info("user already exists with email: {}", user.getEmail());
                session.setAttribute("signupFormResponse", new Message("Email already registered", "alert-danger"));
                return ResponseEntity.ok("EXISTS");
            }
        } catch (Exception e) {
            LOGGER.error("error checking if user exists for signup", e);
        }

        if (!user.getPassword().equals(confirmPassword)) {
            session.setAttribute("signupFormResponse", new Message("Password didn't match", "alert-danger"));
            return ResponseEntity.ok("PASS_MISS");
        }

        user.setPassword(this.passwordEncoder.encode(user.getPassword()));
        user.setRole("ROLE_USER");
        user.setProfilePicture(ImageNameConstants.DEFAULT_PROFILE_IMAGE);
        user.setAccountNonExpired(true);
        user.setAccountNonLocked(true);
        user.setCredentialsNonExpired(true);
        user.setEnabled(true);
        user.setAddress("");
        user.setCity("");
        user.setState("");
        user.setCountry("");
        user.setZipcode(0);

        final String otp = EcommerceUtils.generateOTP();
        final boolean confirmationOtpEmail = this.emailService.sendEmail(
                user.getEmail(),
                "Registration confirmation OTP",
                EmailMessages.registrationOTPMessage(user.getEmail(), user.getName(), otp)
        );

        if (confirmationOtpEmail) {
            session.setAttribute("newRegisteredUser", user);
            session.setAttribute("registrationOTP", otp);
            session.setAttribute("signupFormResponse", new Message("Registration successful", "alert-success"));
            return ResponseEntity.ok("REGISTERED");
        } else {
            return ResponseEntity.ok("ERROR_SENDING_EMAIL");
        }
    }

    public User getUserByEmail(final String username) {
        return this.userRepository
                .findById(username)
                .orElse(null);
    }

    public ResponseEntity<String> verifyRegistrationOTP(final String otp, final HttpSession session) {
        final Object generatedOTP = session.getAttribute("registrationOTP");

        if (generatedOTP != null) {
            if (generatedOTP.toString().equals(otp)) {
                try {
                    User newUser = (User) session.getAttribute("newRegisteredUser");
                    this.userRepository.save(newUser);
                    session.removeAttribute("newRegisteredUser");
                    session.removeAttribute("registrationOTP");
                    session.setAttribute("registrationSuccess", "");

                    this.emailService.sendEmail(
                            newUser.getEmail(),
                            "Registration Successful",
                            EmailMessages.registrationSuccessMessage(newUser.getEmail(), newUser.getName())
                    );

                    return ResponseEntity.ok("SUCCESS");
                } catch (Exception e) {
                    return ResponseEntity.ok("SERVER_ERROR");
                }
            } else {
                return ResponseEntity.ok("OTP_MISMATCHED");
            }
        } else {
            return ResponseEntity.ok("BAD_REQUEST");
        }
    }

    public void dashboard(final Principal principal, final Model model) {
        User user = this.userRepository.getUserByEmail(principal.getName());

        List<Orders> allOrders = this.ordersRepository.findOrdersByEmailIgnoreCaseAndIsOrderDeliveredEquals(user.getEmail(), true);
        allOrders.addAll(this.ordersRepository.findOrdersByEmailIgnoreCaseAndIsOrderDeliveredEquals(user.getEmail(), false));

        List<Orders> cartOrders = this.ordersRepository.findOrdersByEmailIgnoreCaseAndIsOrderDeliveredEquals(user.getEmail(), null);
        List<Orders> pendingOrders = this.ordersRepository.findOrdersByEmailIgnoreCaseAndIsOrderDeliveredEquals(user.getEmail(), false);

        model.addAttribute("currentlyLoggedInUser", user);
        model.addAttribute("allOrdersCount", allOrders.size());
        model.addAttribute("pendingOrdersCount", pendingOrders.size());
        model.addAttribute("cartOrdersCount", cartOrders.size());
        model.addAttribute("title", user.getName());
    }

    public void updateUserProfile(
            final User user,
            final MultipartFile image,
            final HttpSession session,
            final Model model,
            final Principal principal
    ) {
        LOGGER.info("received request to update user profile for user: {}", principal.getName());

        final User loggedInUser = this.userRepository.getUserByEmail(principal.getName());

        try {
            final User oldUserInfo = this.userRepository.getUserByEmail(user.getEmail());

            if (user.getEmail().equals(loggedInUser.getEmail())) {
                String imageFileName = "null";

                if (image != null && !image.isEmpty()) {
                    String originalImageFileName = Objects.requireNonNull(image.getOriginalFilename()).substring(0, image.getOriginalFilename().lastIndexOf('.'));
                    String extension = image.getOriginalFilename().substring(image.getOriginalFilename().lastIndexOf('.'));
                    imageFileName = EcommerceUtils.getEncodedImageFileName(user.getName(), originalImageFileName, extension);
                }

                if (!imageFileName.equals("null")) {
                    String uploadResult = this.firebaseService.upload(image, imageFileName);

                    if (!uploadResult.equals("error")) {
                        final String oldImage = oldUserInfo.getProfilePicture();
                        if (oldImage != null && !oldImage.equals(ImageNameConstants.DEFAULT_PROFILE_IMAGE)) {
                            this.firebaseService.delete(oldImage);
                        }
                        user.setProfilePicture(uploadResult);
                    } else {
                        user.setProfilePicture(user.getProfilePicture());
                    }
                } else {
                    user.setProfilePicture(oldUserInfo.getProfilePicture());
                }

                user.setRole(oldUserInfo.getRole());
                user.setPassword(oldUserInfo.getPassword());
                user.setEnabled(oldUserInfo.isEnabled());
                user.setCredentialsNonExpired(oldUserInfo.isCredentialsNonExpired());
                user.setAccountNonLocked(oldUserInfo.isAccountNonLocked());
                user.setAccountNonExpired(oldUserInfo.isAccountNonExpired());
                user.setCart(oldUserInfo.getCart());

                this.userRepository.save(user);

                session.setAttribute("profileUpdateResponse", new Message("Profile successfully updated", ""));
            } else {
                LOGGER.error("profile update failed for user: {}", principal.getName());
                session.setAttribute("profileUpdateResponse", new Message("Profile update failed", ""));
            }
        } catch (Exception e) {
            LOGGER.error("error updating user profile info", e);
            session.setAttribute("profileUpdateResponse", new Message("Profile update failed", ""));
        }

        model.addAttribute("currentlyLoggedInUser", loggedInUser);
    }

    public void userOrders(final Principal principal, final Model model) {
        User user = this.userRepository.getUserByEmail(principal.getName());
        List<Orders> myOrders = this.ordersRepository.findOrdersByEmailIgnoreCaseAndIsOrderDeliveredEquals(user.getEmail(), false);

        model.addAttribute("myOrders", myOrders);
        model.addAttribute("currentlyLoggedInUser", user);
    }

    public void purchaseHistory(final Principal principal, final Model model) {
        User user = this.userRepository.getUserByEmail(principal.getName());
        List<Orders> purchaseHistory = this.ordersRepository.findOrdersByEmailIgnoreCaseAndIsOrderDeliveredEquals(user.getEmail(), true);

        model.addAttribute("purchaseHistory", purchaseHistory);
        model.addAttribute("currentlyLoggedInUser", user);
    }

    public void profile(final Principal principal, final Model model) {
        User user = this.userRepository.getUserByEmail(principal.getName());
        model.addAttribute("currentlyLoggedInUser", user);
    }

    public void shippingInfo(final Principal principal, final Model model) {
        User user = this.userRepository.getUserByEmail(principal.getName());
        model.addAttribute("currentlyLoggedInUser", user);
    }

    public void updateProfile(final Principal principal, final Model model) {
        User user = this.userRepository.getUserByEmail(principal.getName());
        model.addAttribute("currentlyLoggedInUser", user);
    }

    public void updatePassword(
            final String currentPassword,
            final String newPassword,
            final String confirmNewPassword,
            final HttpSession session,
            final Principal principal,
            final Model model
    ) {
        LOGGER.info("received request to update password for user: {}", principal.getName());
        User user = this.userRepository.getUserByEmail(principal.getName());

        if (newPassword.length() >= 8 && confirmNewPassword.length() >= 8) {
            if (!newPassword.equals(confirmNewPassword)) {
                session.setAttribute("userPasswordUpdateResponse", new Message("New passwords & confirm password mismatched", ""));
            } else if (this.passwordEncoder.matches(currentPassword, user.getPassword())) {
                try {
                    user.setPassword(this.passwordEncoder.encode(newPassword));
                    this.userRepository.save(user);
                    this.emailService.sendEmail(user.getEmail(), "Password successfully updated", EmailMessages.passwordSuccessfullyChangedMessage(user.getEmail(), user.getName()));
                    session.setAttribute("userPasswordUpdateResponse", new Message("Password updated successfully", ""));
                } catch (Exception e) {
                    LOGGER.error("error updating user password", e);
                    session.setAttribute("userPasswordUpdateResponse", new Message("Something wrong happened. Please try again later", ""));
                }
            } else if (!this.passwordEncoder.matches(currentPassword, user.getPassword())) {
                session.setAttribute("userPasswordUpdateResponse", new Message("Current password didn't match", ""));
            } else {
                session.setAttribute("userPasswordUpdateResponse", new Message("Error processing request. Try again later", ""));
            }
        } else {
            session.setAttribute("userPasswordUpdateResponse", new Message("Password length is less than 8", ""));
        }

        model.addAttribute("currentlyLoggedInUser", user);
    }

    public ResponseEntity<?> userCartItems(final Principal principal) {
        if (principal != null) {
            User user = this.userRepository.getUserByEmail(principal.getName());
            return ResponseEntity.ok(user.getCart().size());
        }

        return ResponseEntity.ok(null);
    }

    public ResponseEntity<Boolean> addItemInUserCart(
            final String productName,
            final String discountedPrice,
            final String totalQuantities,
            final Principal principal
    ) {
        if (principal != null) {
            User user = this.userRepository.getUserByEmail(principal.getName());
            Product productById = null;

            try {
                final Optional<Product> productOptional = this.productRepository.findById(productName);

                if (productOptional.isPresent()) {
                    productById = productOptional.get();
                }
            } catch (Exception e) {
                LOGGER.error("error finding product by id: {}", productName);
            }

            if (productById != null && productById.getTotalQuantities() > 0) {
                if (user != null) {
                    try {
                        List<Orders> cartList = user.getCart();
                        boolean isProductAlreadyAvailableInCart = false;

                        for (Orders orders : cartList) {
                            if (orders.getProductName().equals(productName)) {
                                isProductAlreadyAvailableInCart = true;
                                orders.setProductQuantities(orders.getProductQuantities() + 1);
                                break;
                            }
                        }

                        if (!isProductAlreadyAvailableInCart) {
                            Orders order = new Orders();
                            order.setUsername(user.getName());
                            order.setEmail(user.getEmail());
                            order.setProductName(productName);
                            order.setOrderPrice(Integer.parseInt(discountedPrice));
                            order.setProductQuantities(Integer.parseInt(totalQuantities));
                            order.setIsOrderDelivered(null);
                            user.getCart().add(order);
                        }

                        this.userRepository.save(user);
                    } catch (Exception e) {
                        LOGGER.error("error adding item in user cart", e);
                        return ResponseEntity.ok(false);
                    }

                    return ResponseEntity.ok(true);
                }
            } else {
                return ResponseEntity.ok(false);
            }
        }

        return ResponseEntity.ok(false);
    }

    public void deleteCartItem(final String id, final Principal principal) {
        LOGGER.info("received request to delete cart: {}", id);

        if (principal != null) {
            try {
                User user = this.userRepository.getUserByEmail(principal.getName());
                Orders orderToDelete = this.ordersRepository.getReferenceById(Integer.parseInt(id));
                List<Orders> userOrders = user.getCart();

                for (int i = 0; i < userOrders.size(); i++) {
                    if (userOrders.get(i).getId() == orderToDelete.getId()) {
                        userOrders.remove(i);
                        this.ordersRepository.delete(orderToDelete);
                        break;
                    }
                }

                this.userRepository.save(user);
            } catch (Exception e) {
                LOGGER.error("error deleting cart item", e);
            }
        }
    }

    public String buyProduct(final HttpSession session, final Model model, final Principal principal) {
        if (principal != null) {
            if (session.getAttribute("buyNowInfo") != null) {
                model.addAttribute("buyProductInfo", session.getAttribute("buyNowInfo"));
                session.removeAttribute("buyNowInfo");

                return "buy-product-check";
            }

            if (session.getAttribute("buyNowFailed") != null) {
                model.addAttribute("buyProductFailed", session.getAttribute("buyNowInfo"));
                session.removeAttribute("buyNowFailed");

                return "buy-product-check";
            }
        }

        return "redirect:/user/dashboard";
    }

    public ResponseEntity<Boolean> processBuyProduct(
            final String productName,
            final String orderPrice,
            final String discountPrice,
            final int tQuantity,
            final Principal principal,
            final HttpSession session
    ) {
        if (principal != null) {
            User user = this.userRepository.getUserByEmail(principal.getName());

            if (user != null) {
                Product product = this.productRepository.getReferenceById(productName);
                if (product.getTotalQuantities() > 0) {
                    product.setTotalQuantities(product.getTotalQuantities() - 1);
                    this.productRepository.save(product);

                    Orders order = new Orders();
                    order.setUsername(user.getName());
                    order.setEmail(user.getEmail());
                    order.setProductName(productName);
                    order.setProductQuantities(tQuantity);
                    order.setOrderPrice(Integer.parseInt(orderPrice));
                    order.setIsOrderDelivered(null);
                    user.getCart().add(order);
                    this.userRepository.save(user);

                    final ShippingInfo shippingInfo = new ShippingInfo();
                    shippingInfo.setName(user.getName());
                    shippingInfo.setEmail(user.getEmail());
                    shippingInfo.setAddress(user.getAddress());
                    shippingInfo.setCity(user.getCity());
                    shippingInfo.setState(user.getState());
                    shippingInfo.setZipCode(String.valueOf(user.getZipcode()));
                    shippingInfo.setCountry(user.getCountry());
                    shippingInfo.setOrderPrice(Integer.parseInt(orderPrice));
                    shippingInfo.setDiscountPrice(Integer.parseInt(discountPrice));

                    session.setAttribute("buyNowInfo", shippingInfo);

                    return ResponseEntity.ok(true);
                } else {
                    session.setAttribute("buyNowFailed", "");
                }
            }
        }

        return ResponseEntity.ok(false);
    }

}
