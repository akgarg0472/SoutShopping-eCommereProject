package com.akgarg.ecommerce.controller;

import com.akgarg.ecommerce.entity.Category;
import com.akgarg.ecommerce.entity.Orders;
import com.akgarg.ecommerce.entity.Product;
import com.akgarg.ecommerce.entity.User;
import com.akgarg.ecommerce.firebase.FirebaseManager;
import com.akgarg.ecommerce.helper.EmailHelper;
import com.akgarg.ecommerce.helper.EmailMessages;
import com.akgarg.ecommerce.helper.ImageNameConstants;
import com.akgarg.ecommerce.helper.Message;
import com.akgarg.ecommerce.repository.CategoryRepository;
import com.akgarg.ecommerce.repository.OrdersRepository;
import com.akgarg.ecommerce.repository.ProductRepository;
import com.akgarg.ecommerce.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.StandardPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.security.Principal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

//@SuppressWarnings({"unused", "deprecation", "FieldCanBeLocal"})
@Controller
@RequestMapping("/admin")
public class AdminController {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final OrdersRepository ordersRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final StandardPasswordEncoder standardPasswordEncoder;
    private final FirebaseManager firebaseManager;


    @Autowired
    public AdminController(CategoryRepository categoryRepository,
                           ProductRepository productRepository,
                           UserRepository userRepository,
                           OrdersRepository ordersRepository,
                           BCryptPasswordEncoder bCryptPasswordEncoder,
                           FirebaseManager firebaseManager,
                           StandardPasswordEncoder standardPasswordEncoder) {
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.ordersRepository = ordersRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.firebaseManager = firebaseManager;
        this.standardPasswordEncoder = standardPasswordEncoder;
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


    @RequestMapping("/dashboard")
    public String dashboard(HttpSession session,
                            Model model,
                            Principal principal) {
        List<User> users = this.userRepository.findAll();
        List<Category> categories = this.categoryRepository.findAll();
        List<Product> products = this.productRepository.findAll();
        List<Orders> orders = this.ordersRepository.findOrdersByIsOrderDeliveredEquals(false);
        List<Orders> delivered = this.ordersRepository.findOrdersByIsOrderDeliveredEquals(true);
        User admin = this.userRepository.getUserByEmail(principal.getName());

        session.setAttribute("users", users);
        session.setAttribute("categories", categories);
        session.setAttribute("products", products);
        session.setAttribute("pending", orders);
        session.setAttribute("delivered", delivered);
        model.addAttribute("currentlyLoggedInAdmin", admin);
        model.addAttribute("time", getDayHour());
        model.addAttribute("title", admin.getName() + " - Admin dashboard");

        return "admin/dashboard";
    }


    @RequestMapping(value = "/add-category", method = RequestMethod.POST)
    public String addCategory(@ModelAttribute Category category,
                              @RequestParam("image") MultipartFile image,
                              HttpSession session) {
        try {
            this.categoryRepository.findById(category.getName()).get();
            session.setAttribute("addCategoryResponse", new Message("Category " + category.getName() + " already exists", "alert-danger"));
            return "redirect:/admin/dashboard";
        } catch (Exception e) {
            System.out.println(e.getClass() + " ->" + e.getMessage());
        }

        String imageFileName = "null";

        try {
            if (image != null && !image.isEmpty()) {
                String originalImageFileName = image.getOriginalFilename().substring(0, image.getOriginalFilename().lastIndexOf('.'));
                String extension = image.getOriginalFilename().substring(image.getOriginalFilename().lastIndexOf('.'));
                imageFileName = this.getEncodedImageFileName(category.getName(), originalImageFileName, extension);
            }

            if (!imageFileName.equals("null")) {
                String uploadResult = this.firebaseManager.upload(image, imageFileName);

                if (!uploadResult.equals("error")) {
                    category.setPicture(uploadResult);
                } else {
                    category.setPicture(ImageNameConstants.DEFAULT_CATEGORY_IMAGE);
                }
            } else {
                category.setPicture(ImageNameConstants.DEFAULT_CATEGORY_IMAGE);
            }

            this.categoryRepository.save(category);
            session.setAttribute("addCategoryResponse", new Message("Category " + category.getName() + " added successfully", "alert-success"));
        } catch (Exception e) {
            e.printStackTrace();
            session.setAttribute("addCategoryResponse", new Message("Error adding " + category.getName() + " category", "alert-success"));
        }
        return "redirect:/admin/dashboard";
    }


    @RequestMapping(value = "/add-product", method = RequestMethod.POST)
    public String addProduct(@ModelAttribute Product product,
                             @RequestParam("image") MultipartFile image,
                             HttpSession session) {
        // checking if product is already added or not
        try {
            Product product1 = this.productRepository.findById(product.getName()).get();
            session.setAttribute("addProductResponse", new Message("Product already exists", "alert-warning"));
            return "redirect:/admin/dashboard";
        } catch (Exception e) {
            // todo nothing
            System.out.println(e.getClass() + " ->" + e.getMessage());
        }

        String imageName = "null";

        if (image != null && !image.isEmpty()) {
            String originalImageFileName = image.getOriginalFilename().substring(0, image.getOriginalFilename().lastIndexOf('.'));
            String extension = image.getOriginalFilename().substring(image.getOriginalFilename().lastIndexOf('.'));
            imageName = this.getEncodedImageFileName(product.getName(), originalImageFileName, extension);
        }

        try {
            if (!imageName.equals("null")) {
                String uploadResult = this.firebaseManager.upload(image, imageName);

                if (!uploadResult.equals("error")) {
                    product.setPicture(uploadResult);
                } else {
                    product.setPicture(ImageNameConstants.DEFAULT_PRODUCT_IMAGE);
                }
            } else {
                product.setPicture(ImageNameConstants.DEFAULT_PRODUCT_IMAGE);
            }

            this.productRepository.save(product);
            Category category = this.categoryRepository.findById(product.getCategory()).get();
            category.getProducts().add(product);
            this.categoryRepository.save(category);
            session.setAttribute("addProductResponse", new Message("Product added successfully", "alert-success"));
        } catch (Exception e) {
            e.printStackTrace();
            session.setAttribute("addProductResponse", new Message("Product addition failed", "alert-danger"));
        }
        return "redirect:/admin/dashboard";
    }


    @RequestMapping(value = "/manage-categories", method = RequestMethod.GET)
    public String manageCategories(HttpSession session,
                                   Principal principal,
                                   Model model) {
        List<Category> categories = this.categoryRepository.findAll();
        User admin = this.userRepository.getUserByEmail(principal.getName());

        session.setAttribute("categories", categories);
        model.addAttribute("currentlyLoggedInAdmin", admin);
        model.addAttribute("time", getDayHour());
        return "admin/all-categories";
    }


    @RequestMapping(value = "/edit-category", method = RequestMethod.POST)
    public String updateCategory(@ModelAttribute Category category,
                                 @RequestParam("image") MultipartFile image,
                                 HttpSession session) {
        try {
            String imageFileName = "null";

            if (image != null && !image.isEmpty()) {
                String originalImageFileName = image.getOriginalFilename().substring(0, image.getOriginalFilename().lastIndexOf('.'));
                String extension = image.getOriginalFilename().substring(image.getOriginalFilename().lastIndexOf('.'));
                imageFileName = this.getEncodedImageFileName(category.getName(), originalImageFileName, extension);
            }

            if (!imageFileName.equals("null")) {
                String uploadResult = this.firebaseManager.upload(image, imageFileName);

                if (!uploadResult.equals("error")) {
                    category.setPicture(uploadResult);
                    this.categoryRepository.save(category);
                    session.setAttribute("editCategoryResponse", new Message("Category updated successfully", ""));
                } else {
                    session.setAttribute("addCategoryResponse", new Message("Error updating " + category.getName() + " category", "alert-success"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            session.setAttribute("addCategoryResponse", new Message("Error updating " + category.getName() + " category", "alert-success"));
        }

        return "redirect:/admin/manage-categories";
    }


    @RequestMapping(value = "/delete-category/{category}", method = RequestMethod.GET)
    public String deleteCategory(@PathVariable("category") String categoryToRemove,
                                 HttpSession session) {
        try {
            String imageUrl = this.categoryRepository.findById(categoryToRemove).get().getPicture();

            if (!imageUrl.equals(ImageNameConstants.DEFAULT_CATEGORY_IMAGE)) {
                boolean deleteResult = this.firebaseManager.delete(imageUrl);
                if (deleteResult) {
                    this.categoryRepository.deleteById(categoryToRemove);
                    this.productRepository.deleteProductByCategory(categoryToRemove);
                    session.setAttribute("deleteCategoryResponse", new Message("Category and all related products deleted", "alert-success"));
                } else {
                    session.setAttribute("deleteCategoryResponse", new Message("Category deletion failed", "alert-success"));
                }
            } else {
                this.categoryRepository.deleteById(categoryToRemove);
                this.productRepository.deleteProductByCategory(categoryToRemove);
                session.setAttribute("deleteCategoryResponse", new Message("Category and all related products deleted", "alert-success"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            session.setAttribute("deleteCategoryResponse", new Message("Category deletion failed", "alert-success"));
        }

        return "redirect:/admin/manage-categories";
    }


    @RequestMapping(value = "/delete-product/{product}", method = RequestMethod.GET)
    public String deleteProduct(@PathVariable("product") String productInfo,
                                HttpSession session) {
        String productName = productInfo.substring(0, productInfo.lastIndexOf(','));
        String productCategory = productInfo.substring(productInfo.lastIndexOf(',') + 1);

        try {
            Category category = this.categoryRepository.getOne(productCategory);
            Product product = this.productRepository.getOne(productName);
            String imageURL = product.getPicture();

            if (!imageURL.equals(ImageNameConstants.DEFAULT_PRODUCT_IMAGE)) {
                boolean deleteResult = this.firebaseManager.delete(imageURL);
                if (deleteResult) {
                    category.getProducts().remove(product);
                    this.categoryRepository.save(category);
                    this.productRepository.delete(product);
                    session.setAttribute("deleteProductResponse", new Message("Product removed successfully", "alert-success"));
                } else {
                    session.setAttribute("deleteProductResponse", new Message("Product deletion failed", "alert-danger"));
                }
            } else {
                category.getProducts().remove(product);
                this.categoryRepository.save(category);
                this.productRepository.delete(product);
                session.setAttribute("deleteProductResponse", new Message("Product removed successfully", "alert-success"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            session.setAttribute("deleteProductResponse", new Message("Product deletion failed", "alert-success"));
        }

        return "redirect:/admin/manage-products";
    }


    @RequestMapping("/all-users")
    public String showAllUsers(Model model) {
        List<User> users = this.userRepository.findAll();
        model.addAttribute("allUsers", users);
        return "admin/all-users";
    }


    @RequestMapping("/edit-user/{userEmail}")
    public String adminEditUser(@PathVariable("userEmail") String userEmail,
                                Model model) {
        User user = this.userRepository.getUserByEmail(userEmail);
        model.addAttribute("adminEditUser", user);
        return "admin/admin-edit-user";
    }


    @RequestMapping(value = "/processUpdateUserDetails", method = RequestMethod.POST)
    public String processUpdateUserDetails(@RequestParam("email") String email,
                                           @RequestParam("role") String role,
                                           @RequestParam("isEnabled") String isEnabled,
                                           @RequestParam("accountNonLocked") String accountNonLocked) {
        User user = this.userRepository.getUserByEmail(email);
        user.setRole(role);
        user.setEnabled(isEnabled.equals("enabled"));
        user.setAccountNonLocked(accountNonLocked.equals("not locked"));
        this.userRepository.save(user);

        return "redirect:/admin/all-users";
    }


    @RequestMapping("/delete-user/{email}")
    public String deleteUser(@PathVariable("email") String email) {
        try {
            User user = this.userRepository.getUserByEmail(email);
            String imageURL = user.getProfilePicture();

            if (!imageURL.equals(ImageNameConstants.DEFAULT_PROFILE_IMAGE)) {
                boolean deleteResult = this.firebaseManager.delete(imageURL);
                if (deleteResult) {
                    this.userRepository.delete(user);
                }
            } else {
                this.userRepository.delete(user);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "redirect:/admin/all-users";
    }


    @RequestMapping("/manage-products")
    public String allProducts(Model model,
                              Principal principal) {
        List<Product> products = this.productRepository.findAll();
        User admin = this.userRepository.getUserByEmail(principal.getName());

        model.addAttribute("currentlyLoggedInAdmin", admin.getName());
        model.addAttribute("allProducts", products);
        model.addAttribute("time", getDayHour());

        return "admin/manage-products";
    }


    @RequestMapping("/manage-products/search/{searchKeyword}")
    public String searchProducts(@PathVariable("searchKeyword") String searchKeyword,
                                 Model model,
                                 Principal principal) {
        List<Product> products = new ArrayList<>();
        String[] keywords = searchKeyword.split(" ");

        for (String string : keywords) {
            if (string.length() > 2) {
                List<Product> category = this.productRepository.findProductsByCategoryContainsIgnoreCase(string);
                for (Product product : category) {
                    if (!products.contains(product)) {
                        products.add(product);
                    }
                }
            }
        }

        for (String string : keywords) {
            if (string.length() > 2) {
                List<Product> name = this.productRepository.findProductsByNameContainsIgnoreCase(string);
                for (Product product : name) {
                    if (!products.contains(product)) {
                        products.add(product);
                    }
                }
            }
        }

        for (String string : keywords) {
            if (string.length() > 2) {
                List<Product> description = this.productRepository.findProductsByDescriptionContainsIgnoreCase(string);
                for (Product product : description) {
                    if (!products.contains(product)) {
                        products.add(product);
                    }
                }
            }
        }

        User admin = this.userRepository.getUserByEmail(principal.getName());
        model.addAttribute("currentlyLoggedInAdmin", admin);
        model.addAttribute("allProducts", products);
        model.addAttribute("time", getDayHour());

        return "admin/manage-products";
    }


    @RequestMapping(value = "/edit-product/{productName}", method = RequestMethod.POST)
    public String updateProduct(@PathVariable("productName") String previousProductName,
                                @ModelAttribute Product product,
                                @RequestParam("image") MultipartFile image,
                                HttpSession session) {
        try {
            Product previousProduct = this.productRepository.getOne(previousProductName);
            String imageName = "null";

            if (image != null && !image.isEmpty()) {
                String originalImageFileName = image.getOriginalFilename().substring(0, image.getOriginalFilename().lastIndexOf('.'));
                String extension = image.getOriginalFilename().substring(image.getOriginalFilename().lastIndexOf('.'));
                imageName = this.getEncodedImageFileName(product.getName(), originalImageFileName, extension);
            }

            if (!imageName.equals("null")) {
                String uploadResult = this.firebaseManager.upload(image, imageName);
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
            e.printStackTrace();
            session.setAttribute("productUpdateResponse", new Message("Product updation failed", ""));
        }

        return "redirect:/admin/manage-products";
    }


    @RequestMapping(value = "/pending-deliveries", method = RequestMethod.GET)
    public String pendingDeliveries(Model model,
                                    Principal principal) {
        User admin = this.userRepository.getUserByEmail(principal.getName());
        List<Orders> orders = this.ordersRepository.findOrdersByIsOrderDeliveredEquals(false);

        model.addAttribute("allPendingDeliveries", orders);
        model.addAttribute("currentlyLoggedInAdmin", admin);
        model.addAttribute("time", getDayHour());

        return "admin/pending-deliveries";
    }


    @RequestMapping(value = "/pending-deliveries/search/{orderDetail}", method = RequestMethod.GET)
    public String pendingDeliveriesSearch(@PathVariable("orderDetail") String orderDetail,
                                          Principal principal,
                                          Model model) {
        User admin = this.userRepository.getUserByEmail(principal.getName());
        List<Orders> orders = new ArrayList<>();

        if (orderDetail.length() < 5) {
            try {
                int orderId = Integer.parseInt(orderDetail);
                Orders pendingOrderById = this.ordersRepository.findOrdersByIdEqualsAndIsOrderDeliveredEquals(orderId, false);
                if (pendingOrderById != null) {
                    orders.add(pendingOrderById);
                }
            } catch (Exception e) {
                // todo later
            }
        } else {
            try {
                List<Orders> ordersByEmail = this.ordersRepository.findOrdersByEmailIgnoreCaseAndIsOrderDeliveredEquals(orderDetail, false);
                if (ordersByEmail != null) {
                    orders.addAll(ordersByEmail);
                }
            } catch (Exception e) {
                // todo later
            }
        }

        model.addAttribute("allPendingDeliveries", orders);
        model.addAttribute("currentlyLoggedInAdmin", admin);
        model.addAttribute("time", getDayHour());

        return "admin/pending-deliveries";
    }


    @RequestMapping(value = "/delivered-orders", method = RequestMethod.GET)
    public String deliveredOrders(Principal principal,
                                  Model model) {
        User admin = this.userRepository.getUserByEmail(principal.getName());
        List<Orders> deliveredOrders = this.ordersRepository.findOrdersByIsOrderDeliveredEquals(true);

        model.addAttribute("currentlyLoggedInAdmin", admin);
        model.addAttribute("allDeliveredOrders", deliveredOrders);
        model.addAttribute("time", getDayHour());

        return "admin/delivered-orders";
    }


    @RequestMapping(value = "/delivered-orders/search/{orderId}", method = RequestMethod.GET)
    public String searchDeliveredOrders(@PathVariable("orderId") String orderDetail, Principal principal,
                                        Model model) {
        User admin = this.userRepository.getUserByEmail(principal.getName());
        List<Orders> allDeliveredOrders = new ArrayList<>();

        if (orderDetail.length() < 5) {
            try {
                int orderId = Integer.parseInt(orderDetail);
                Orders deliveredProduct = this.ordersRepository.findOrdersByIdEqualsAndIsOrderDeliveredEquals(orderId, true);
                if (deliveredProduct != null) {
                    allDeliveredOrders.add(deliveredProduct);
                }
            } catch (Exception e) {
                // todo later
            }
        } else {
            try {
                List<Orders> deliveredProducts = this.ordersRepository.findOrdersByEmailIgnoreCaseAndIsOrderDeliveredEquals(orderDetail, true);
                if (deliveredProducts != null) {
                    allDeliveredOrders.addAll(deliveredProducts);
                }
            } catch (Exception e) {
                // todo later
            }
        }

        model.addAttribute("allDeliveredOrders", allDeliveredOrders);
        model.addAttribute("currentlyLoggedInAdmin", admin);
        model.addAttribute("time", getDayHour());

        return "admin/delivered-orders";
    }


    @RequestMapping(value = "/edit-profile", method = RequestMethod.GET)
    public String updateProfile(Model model,
                                Principal principal) {
        User admin = this.userRepository.getUserByEmail(principal.getName());
        model.addAttribute("currentlyLoggedInAdmin", admin);
        model.addAttribute("time", getDayHour());
        return "admin/edit-profile";
    }


    @RequestMapping(value = "/process-profile-update", method = RequestMethod.POST)
    public String processUpdateProfile(@ModelAttribute User newAdminInformation,
                                       @RequestParam("image") MultipartFile image,
                                       Model model,
                                       HttpSession session,
                                       Principal principal) {
        try {
            User previousAdmin = this.userRepository.getUserByEmail(newAdminInformation.getEmail());

            // profile picture update logic
            String imageFileName = "null";

            if (image != null && !image.isEmpty()) {
                String originalImageFileName = image.getOriginalFilename().substring(0, image.getOriginalFilename().lastIndexOf('.'));
                String extension = image.getOriginalFilename().substring(image.getOriginalFilename().lastIndexOf('.'));
                imageFileName = this.getEncodedImageFileName(newAdminInformation.getName(), originalImageFileName, extension);
            }

            if (!imageFileName.equals("null")) {
                String uploadFileName = this.firebaseManager.upload(image, imageFileName);
                System.out.println("admin profile update photo upload: " + uploadFileName);

                if (!uploadFileName.equals("error")) {
                    newAdminInformation.setProfilePicture(uploadFileName);
                } else {
                    newAdminInformation.setProfilePicture(previousAdmin.getProfilePicture());
                }
            } else {
                newAdminInformation.setProfilePicture(previousAdmin.getProfilePicture());
            }

            newAdminInformation.setRole(previousAdmin.getRole());
            newAdminInformation.setPassword(previousAdmin.getPassword());
            newAdminInformation.setEnabled(previousAdmin.isEnabled());
            newAdminInformation.setCredentialsNonExpired(previousAdmin.isCredentialsNonExpired());
            newAdminInformation.setAccountNonLocked(previousAdmin.isAccountNonLocked());
            newAdminInformation.setAccountNonExpired(previousAdmin.isAccountNonExpired());

            this.userRepository.save(newAdminInformation);
            session.setAttribute("updateProfileResponse", new Message("Profile successfully updated", ""));
        } catch (Exception e) {
            e.printStackTrace();
            session.setAttribute("updateProfileResponse", new Message("Profile update failed", ""));
        }

        User loggedInAdmin = this.userRepository.getUserByEmail(principal.getName());
        model.addAttribute("currentlyLoggedInAdmin", loggedInAdmin);
        model.addAttribute("time", getDayHour());

        return "redirect:/admin/edit-profile";
    }


    @RequestMapping(value = "/update-password", method = RequestMethod.POST)
    public String updatePassword(@RequestParam("old-password") String oldPassword,
                                 @RequestParam("new-password") String newPassword,
                                 @RequestParam("confirm-new-password") String confirmNewPassword,
                                 HttpSession session,
                                 Model model,
                                 Principal principal) {
        User loggedInAdmin = this.userRepository.getUserByEmail(principal.getName());

        if (newPassword.matches(confirmNewPassword)) {
            boolean isOldPasswordMatched = this.bCryptPasswordEncoder.matches(oldPassword, loggedInAdmin.getPassword());
            if (!isOldPasswordMatched) {
                session.setAttribute("updatePasswordResponse", new Message("Old password didn't matched", ""));
            } else {
                loggedInAdmin.setPassword(this.bCryptPasswordEncoder.encode(newPassword));
                this.userRepository.save(loggedInAdmin);
                EmailHelper.sendEmail(loggedInAdmin.getEmail(), "Password updated successfully", EmailMessages.passwordSuccessfullyChangedMessage(loggedInAdmin.getEmail(), loggedInAdmin.getName()));
                session.setAttribute("updatePasswordResponse", new Message("Password successfully updated", ""));
            }
        } else {
            session.setAttribute("updatePasswordResponse", new Message("New password and confirm password didn't matched", ""));
        }

        model.addAttribute("currentlyLoggedInAdmin", loggedInAdmin);
        model.addAttribute("time", getDayHour());

        return "redirect:/admin/edit-profile";
    }


    @RequestMapping("/cancel-order/{orderId}")
    public String cancelOrder(@PathVariable("orderId") String orderId,
                              Model model,
                              Principal principal,
                              HttpSession session) {
        User admin = this.userRepository.getUserByEmail(principal.getName());

        // informing user about cancellation of order
        Orders orderToDelete = this.ordersRepository.findById(Integer.parseInt(orderId)).get();

        this.ordersRepository.deleteById(Integer.parseInt(orderId));
        List<Orders> orders = this.ordersRepository.findAll();

        EmailHelper.sendEmail(orderToDelete.getEmail(), "Update regarding cancellation of order", EmailMessages.orderCancelledMessage(orderToDelete.getUsername(), orderToDelete.getProductName(), orderToDelete.getOrderDate(), orderToDelete.getOrderPrice(), orderToDelete.getProductQuantities()));

        session.setAttribute("orderCancelResponse", new Message("Order successfully cancelled", ""));
        model.addAttribute("allPendingDeliveries", orders);
        model.addAttribute("currentlyLoggedInAdmin", admin);
        model.addAttribute("time", getDayHour());

        return "redirect:/admin/pending-deliveries";
    }


    @RequestMapping("/update-order/{orderId}")
    public String updateOrderStatus(@PathVariable("orderId") String orderId,
                                    Model model,
                                    Principal principal,
                                    HttpSession session) {
        User admin = this.userRepository.getUserByEmail(principal.getName());

        try {
            Orders orderToProcess = this.ordersRepository.getOne(Integer.parseInt(orderId));
            orderToProcess.setOrderDelivered(true);
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd MMMM, uuuu");
            LocalDate localDate = LocalDate.now();
            orderToProcess.setDeliveryDate(dtf.format(localDate));
            this.ordersRepository.save(orderToProcess);
            EmailHelper.sendEmail(orderToProcess.getEmail(), "Order Delivered", EmailMessages.orderSuccessfullyDeliveredMessage(orderToProcess.getUsername(), orderToProcess.getProductName(), orderToProcess.getOrderDate(), orderToProcess.getOrderPrice(), orderToProcess.getProductQuantities(), orderToProcess.getDeliveryDate()));
            session.setAttribute("orderUpdateResponse", new Message("Order status successfully updated", ""));
        } catch (Exception e) {
            e.printStackTrace();
            session.setAttribute("orderUpdateResponse", new Message("Order status updation failed", ""));
        }

        List<Orders> orders = this.ordersRepository.findOrdersByIsOrderDeliveredEquals(false);
        model.addAttribute("allPendingDeliveries", orders);
        model.addAttribute("currentlyLoggedInAdmin", admin);
        model.addAttribute("time", getDayHour());

        return "redirect:/admin/pending-deliveries";
    }


    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public String logout(HttpSession session) {
        session.setAttribute("logout", "logout");
        return "redirect:/logout";
    }


    private String getEncodedImageFileName(String productName,
                                           String originalImageFileName,
                                           String extension) {
        return this.standardPasswordEncoder.encode(productName + originalImageFileName) + extension;
    }


    private String getDayHour() {
        int hourOfDay = new GregorianCalendar().get(Calendar.HOUR_OF_DAY);
        String time;

        if (hourOfDay >= 21 || hourOfDay < 5) {
            time = "Good Night";
        } else if (hourOfDay >= 5 && hourOfDay < 12) {
            time = "Good Morning";
        } else if (hourOfDay >= 12 && hourOfDay < 17) {
            time = "Good Afternoon";
        } else {
            time = "Good Evening";
        }

        return time;
    }
}