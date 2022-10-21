package com.akgarg.ecommerce.service.admin;

import com.akgarg.ecommerce.model.dto.Message;
import com.akgarg.ecommerce.model.entity.Category;
import com.akgarg.ecommerce.model.entity.Orders;
import com.akgarg.ecommerce.model.entity.Product;
import com.akgarg.ecommerce.model.entity.User;
import com.akgarg.ecommerce.repository.CategoryRepository;
import com.akgarg.ecommerce.repository.OrdersRepository;
import com.akgarg.ecommerce.repository.ProductRepository;
import com.akgarg.ecommerce.repository.UserRepository;
import com.akgarg.ecommerce.service.email.EmailService;
import com.akgarg.ecommerce.service.firebase.FirebaseService;
import com.akgarg.ecommerce.utils.EcommerceUtils;
import com.akgarg.ecommerce.utils.EmailMessages;
import com.akgarg.ecommerce.utils.ImageNameConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.security.Principal;
import java.util.List;

/**
 * @author Akhilesh Garg
 * @since 21-10-2022
 */
@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final OrdersRepository ordersRepository;
    private final FirebaseService firebaseService;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    @Override
    public String dashboard(final HttpSession session, final Model model, final String userEmail) {
        final List<User> users = this.userRepository.findAll();
        final List<Category> categories = this.categoryRepository.findAll();
        final List<Product> products = this.productRepository.findAll();
        final List<Orders> pendingOrders = this.ordersRepository.findOrdersByIsOrderDeliveredEquals(false);
        final List<Orders> deliveredOrders = this.ordersRepository.findOrdersByIsOrderDeliveredEquals(true);
        final User admin = this.userRepository.getUserByEmail(userEmail);

        session.setAttribute("users", users);
        session.setAttribute("categories", categories);
        session.setAttribute("products", products);
        session.setAttribute("pending", pendingOrders);
        session.setAttribute("delivered", deliveredOrders);

        model.addAttribute("currentlyLoggedInAdmin", admin);
        model.addAttribute("time", EcommerceUtils.getDayHour());
        model.addAttribute("title", admin.getName() + " - Admin dashboard");

        return "admin/dashboard";
    }

    @Override
    public String getAllUsers(final Model model) {
        List<User> users = this.userRepository.findAll();
        model.addAttribute("allUsers", users);
        return "admin/all-users";
    }

    @Override
    public String editUser(final String userEmail, final Model model) {
        User user = this.userRepository.getUserByEmail(userEmail);
        model.addAttribute("adminEditUser", user);
        return "admin/admin-edit-user";
    }

    @Override
    public String processUpdateUserDetails(
            final String email, final String role, final String isEnabled, final String accountNonLocked
    ) {
        User user = this.userRepository.getUserByEmail(email);
        user.setRole(role);
        user.setEnabled(isEnabled.equals("enabled"));
        user.setAccountNonLocked(accountNonLocked.equals("not locked"));
        this.userRepository.save(user);

        return "redirect:/admin/all-users";
    }

    @Override
    public String deleteUser(final String email) {
        try {
            User user = this.userRepository.getUserByEmail(email);
            String imageURL = user.getProfilePicture();

            if (!imageURL.equals(ImageNameConstants.DEFAULT_PROFILE_IMAGE)) {
                boolean deleteResult = this.firebaseService.delete(imageURL);
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

    @Override
    public String updateProfile(final Model model, final Principal principal) {
        User admin = this.userRepository.getUserByEmail(principal.getName());
        model.addAttribute("currentlyLoggedInAdmin", admin);
        model.addAttribute("time", EcommerceUtils.getDayHour());
        return "admin/edit-profile";
    }

    @Override
    public String processUpdateProfile(
            final User newAdminInformation,
            final MultipartFile image,
            final Model model,
            final HttpSession session,
            final Principal principal
    ) {
        try {
            User previousAdmin = this.userRepository.getUserByEmail(newAdminInformation.getEmail());

            // profile picture update logic
            String imageFileName = "null";

            if (image != null && !image.isEmpty()) {
                String originalImageFileName = image.getOriginalFilename().substring(0, image.getOriginalFilename().lastIndexOf('.'));
                String extension = image.getOriginalFilename().substring(image.getOriginalFilename().lastIndexOf('.'));
                imageFileName = EcommerceUtils.getEncodedImageFileName(newAdminInformation.getName(), originalImageFileName, extension);
            }

            if (!imageFileName.equals("null")) {
                String uploadFileName = this.firebaseService.upload(image, imageFileName);
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
        model.addAttribute("time", EcommerceUtils.getDayHour());

        return "redirect:/admin/edit-profile";
    }

    @Override
    public String updatePassword(
            final String oldPassword,
            final String newPassword,
            final String confirmNewPassword,
            final HttpSession session,
            final Model model,
            final Principal principal
    ) {
        User loggedInAdmin = this.userRepository.getUserByEmail(principal.getName());

        if (newPassword.matches(confirmNewPassword)) {
            boolean isOldPasswordMatched = this.passwordEncoder.matches(oldPassword, loggedInAdmin.getPassword());
            if (!isOldPasswordMatched) {
                session.setAttribute("updatePasswordResponse", new Message("Old password didn't matched", ""));
            } else {
                loggedInAdmin.setPassword(this.passwordEncoder.encode(newPassword));
                this.userRepository.save(loggedInAdmin);
                this.emailService.sendEmail(loggedInAdmin.getEmail(), "Password updated successfully", EmailMessages.passwordSuccessfullyChangedMessage(loggedInAdmin.getEmail(), loggedInAdmin.getName()));
                session.setAttribute("updatePasswordResponse", new Message("Password successfully updated", ""));
            }
        } else {
            session.setAttribute("updatePasswordResponse", new Message("New password and confirm password didn't matched", ""));
        }

        model.addAttribute("currentlyLoggedInAdmin", loggedInAdmin);
        model.addAttribute("time", EcommerceUtils.getDayHour());

        return "redirect:/admin/edit-profile";
    }

}
