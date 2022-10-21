package com.akgarg.ecommerce.service.admin;

import com.akgarg.ecommerce.model.entity.User;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.security.Principal;

/**
 * @author Akhilesh Garg
 * @since 21-10-2022
 */
public interface AdminService {

    String dashboard(HttpSession session, Model model, String userEmail);

    String getAllUsers(Model model);

    String editUser(String userEmail, Model model);

    String processUpdateUserDetails(String email, String role, String isEnabled, String accountNonLocked);

    String deleteUser(String email);

    String updateProfile(Model model, Principal principal);

    String processUpdateProfile(
            User newAdminInformation, MultipartFile image, Model model, HttpSession session, Principal principal
    );

    String updatePassword(
            String oldPassword,
            String newPassword,
            String confirmNewPassword,
            HttpSession session,
            Model model,
            Principal principal
    );

}
