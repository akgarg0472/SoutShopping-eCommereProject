package com.akgarg.ecommerce.controller;

import com.akgarg.ecommerce.entity.Orders;
import com.akgarg.ecommerce.entity.Product;
import com.akgarg.ecommerce.entity.User;
import com.akgarg.ecommerce.firebase.FirebaseManager;
import com.akgarg.ecommerce.helper.*;
import com.akgarg.ecommerce.repository.CategoryRepository;
import com.akgarg.ecommerce.repository.OrdersRepository;
import com.akgarg.ecommerce.repository.ProductRepository;
import com.akgarg.ecommerce.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.StandardPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.security.Principal;
import java.util.List;


@SuppressWarnings({"unused", "deprecation", "FieldCanBeLocal"})
@Controller
@RequestMapping("/user")
public class UserController {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final StandardPasswordEncoder standardPasswordEncoder;
    private final OrdersRepository ordersRepository;
    private final FirebaseManager firebaseManager;


    @Autowired
    public UserController(UserRepository userRepository, ProductRepository productRepository,
                          CategoryRepository categoryRepository,
                          BCryptPasswordEncoder bCryptPasswordEncoder,
                          StandardPasswordEncoder standardPasswordEncoder,
                          OrdersRepository ordersRepository,
                          FirebaseManager firebaseManager) {
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.standardPasswordEncoder = standardPasswordEncoder;
        this.ordersRepository = ordersRepository;
        this.firebaseManager = firebaseManager;
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


    @RequestMapping(value = "/dashboard", method = RequestMethod.GET)
    public String dashboard(Principal principal,
                            Model model) {
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

        return "user/dashboard";
    }


    @RequestMapping(value = "/process-update-profile", method = RequestMethod.POST)
    public String updateUserProfile(@ModelAttribute User user,
                                    @RequestParam("image") MultipartFile image,
                                    HttpSession session,
                                    Model model,
                                    Principal principal) {
        User loggedInUser = this.userRepository.getUserByEmail(principal.getName());

        try {
            User oldUserInfo = this.userRepository.getUserByEmail(user.getEmail());

            if (user.getEmail().equals(loggedInUser.getEmail())) {
                // profile picture update logic
                String imageFileName = "null";

                if (image != null && !image.isEmpty()) {
                    String originalImageFileName = image.getOriginalFilename().substring(0, image.getOriginalFilename().lastIndexOf('.'));
                    String extension = image.getOriginalFilename().substring(image.getOriginalFilename().lastIndexOf('.'));
                    imageFileName = this.getEncodedImageFileName(user.getName(), originalImageFileName, extension);
                }

                if (!imageFileName.equals("null")) {
                    String uploadResult = this.firebaseManager.upload(image, imageFileName);

                    if (!uploadResult.equals("error")) {
                        String oldImage = oldUserInfo.getProfilePicture();
                        if (oldImage != null && !oldImage.equals(ImageNameConstants.DEFAULT_PROFILE_IMAGE)) {
                            try {
                                this.firebaseManager.delete(oldImage);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
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
                System.out.println("1 failed");
                session.setAttribute("profileUpdateResponse", new Message("Profile update failed", ""));
            }
        } catch (Exception e) {
            e.printStackTrace();
            session.setAttribute("profileUpdateResponse", new Message("Profile update failed", ""));
        }

        model.addAttribute("currentlyLoggedInUser", loggedInUser);
        return "redirect:/user/update-profile";
    }


    @RequestMapping(value = "/my-orders", method = RequestMethod.GET)
    public String myOrders(Principal principal,
                           Model model) {
        User user = this.userRepository.getUserByEmail(principal.getName());
        List<Orders> myOrders = this.ordersRepository.findOrdersByEmailIgnoreCaseAndIsOrderDeliveredEquals(user.getEmail(), false);

        model.addAttribute("myOrders", myOrders);
        model.addAttribute("currentlyLoggedInUser", user);

        return "user/my-orders";
    }


    @RequestMapping(value = "/purchase-history", method = RequestMethod.GET)
    public String purchaseHistory(Principal principal,
                                  Model model) {
        User user = this.userRepository.getUserByEmail(principal.getName());
        List<Orders> purchaseHistory = this.ordersRepository.findOrdersByEmailIgnoreCaseAndIsOrderDeliveredEquals(user.getEmail(), true);

        model.addAttribute("purchaseHistory", purchaseHistory);
        model.addAttribute("currentlyLoggedInUser", user);

        return "user/purchase-history";
    }


    @RequestMapping(value = "/my-profile", method = RequestMethod.GET)
    public String myProfile(Principal principal,
                            Model model) {
        User user = this.userRepository.getUserByEmail(principal.getName());

        model.addAttribute("currentlyLoggedInUser", user);

        return "user/my-profile";
    }


    @RequestMapping(value = "/shipping-info", method = RequestMethod.GET)
    public String shippingDetails(Principal principal,
                                  Model model) {
        User user = this.userRepository.getUserByEmail(principal.getName());

        model.addAttribute("currentlyLoggedInUser", user);

        return "user/shipping-info";
    }


    @RequestMapping(value = "/update-profile", method = RequestMethod.GET)
    public String updateProfile(Principal principal,
                                Model model) {
        User user = this.userRepository.getUserByEmail(principal.getName());

        model.addAttribute("currentlyLoggedInUser", user);

        return "user/update-profile";
    }


    @RequestMapping(value = "/update-password", method = RequestMethod.POST)
    public String updatePassword(@RequestParam("current-password") String currentPassword,
                                 @RequestParam("new-password") String newPassword,
                                 @RequestParam("confirm-new-password") String confirmNewPassword,
                                 HttpSession session,
                                 Principal principal,
                                 Model model) {
        User user = this.userRepository.getUserByEmail(principal.getName());

        if (newPassword.length() >= 8 && confirmNewPassword.length() >= 8 && !newPassword.equals("") && !confirmNewPassword.equals("")) {
            if (!newPassword.equals(confirmNewPassword)) {
                session.setAttribute("userPasswordUpdateResponse", new Message("New passwords & confirm password mismatched", ""));
            } else if (this.bCryptPasswordEncoder.matches(currentPassword, user.getPassword())) {
                try {
                    user.setPassword(this.bCryptPasswordEncoder.encode(newPassword));
                    this.userRepository.save(user);
                    EmailHelper.sendEmail(user.getEmail(), "Password successfully updated", EmailMessages.passwordSuccessfullyChangedMessage(user.getEmail(), user.getName()));
                    session.setAttribute("userPasswordUpdateResponse", new Message("Password updated successfully", ""));
                } catch (Exception e) {
                    e.printStackTrace();
                    session.setAttribute("userPasswordUpdateResponse", new Message("Something wrong happened. Please try again later", ""));
                }
            } else if (!this.bCryptPasswordEncoder.matches(currentPassword, user.getPassword())) {
                session.setAttribute("userPasswordUpdateResponse", new Message("Current password didn't match", ""));
            } else {
                session.setAttribute("userPasswordUpdateResponse", new Message("Error processing request. Try again later", ""));
            }
        } else {
            session.setAttribute("userPasswordUpdateResponse", new Message("Password length is less than 8", ""));
        }

        model.addAttribute("currentlyLoggedInUser", user);

        return "redirect:/user/my-profile";
    }


    @RequestMapping(value = "/getUserCartItem", method = RequestMethod.GET)
    public ResponseEntity<?> getUserCartItems(Principal principal) {
        if (principal != null) {
            User user = this.userRepository.getUserByEmail(principal.getName());
            return ResponseEntity.ok(user.getCart().size());
        }

        return ResponseEntity.ok(null);
    }


    @RequestMapping(value = "/add-cart-item", method = RequestMethod.POST)
    public ResponseEntity<?> addItemInUserCart(@RequestParam("name") String productName,
                                               @RequestParam("originalPrice") String originalPrice,
                                               @RequestParam("discountedPrice") String discountedPrice,
                                               @RequestParam("totalQuantities") String totalQuantities,
                                               Principal principal) {
        if (principal != null) {
            User user = this.userRepository.getUserByEmail(principal.getName());
            Product productById = null;

            try {
                productById = this.productRepository.findById(productName).get();
            } catch (Exception e) {
                e.printStackTrace();
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
                            order.setOrderDelivered(null);
                            user.getCart().add(order);
                        }

                        this.userRepository.save(user);
                    } catch (Exception e) {
                        e.printStackTrace();
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


    @RequestMapping(value = "/delete-cart-order/{orderId}", method = RequestMethod.GET)
    public String deleteCartItem(@PathVariable("orderId") String id,
                                 Principal principal,
                                 Model model) {
        if (principal != null) {
            try {
                User user = this.userRepository.getUserByEmail(principal.getName());
                Orders orderToDelete = this.ordersRepository.getOne(Integer.parseInt(id));
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
                e.printStackTrace();
            }
        }

        return "redirect:/cart";
    }


    @RequestMapping("/buy-product-checkout")
    public String buyProduct(HttpSession session, Model model, Principal principal) {
        if (principal != null) {
            if (session.getAttribute("buyNowInfo") != null) {
                model.addAttribute("buyProductInfo", session.getAttribute("buyNowInfo"));
                session.removeAttribute("buyNowInfo");

                return "buy-product-check";
            }
            if (session.getAttribute("buyNowFailed") != null) {
                System.out.println("buyProductFailed");
                model.addAttribute("buyProductFailed", session.getAttribute("buyNowInfo"));
                session.removeAttribute("buyNowFailed");

                return "buy-product-check";
            }
        }

        return "redirect:/user/dashboard";
    }


    @RequestMapping(value = "/process-buy-product", method = RequestMethod.POST)
    public ResponseEntity<Boolean> processBuyProduct(@RequestParam("name") String productName,
                                                     @RequestParam("originalPrice") String orderPrice,
                                                     @RequestParam("discountedPrice") String discountPrice,
                                                     @RequestParam("totalQuantities") int tQuantity,
                                                     Principal principal,
                                                     HttpSession session) {
        if (principal != null) {
            User user = this.userRepository.getUserByEmail(principal.getName());

            if (user != null) {
                Product product = this.productRepository.getOne(productName);
                if (product.getTotalQuantities() > 0) {
                    product.setTotalQuantities(product.getTotalQuantities() - 1);
                    this.productRepository.save(product);

                    Orders order = new Orders();
                    order.setUsername(user.getName());
                    order.setEmail(user.getEmail());
                    order.setProductName(productName);
                    order.setProductQuantities(tQuantity);
                    order.setOrderPrice(Integer.parseInt(orderPrice));
                    order.setOrderDelivered(null);
                    user.getCart().add(order);
                    this.userRepository.save(user);

                    ShippingInfo shippingInfo = new ShippingInfo();
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


    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public String logout() {
        return "redirect:/logout";
    }


    private String getEncodedImageFileName(String productName,
                                           String originalImageFileName,
                                           String extension) {
        return this.standardPasswordEncoder.encode(productName + originalImageFileName) + extension;
    }
}