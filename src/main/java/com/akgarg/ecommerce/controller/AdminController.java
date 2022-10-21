package com.akgarg.ecommerce.controller;

import com.akgarg.ecommerce.model.entity.User;
import com.akgarg.ecommerce.service.admin.AdminService;
import com.akgarg.ecommerce.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.security.Principal;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;
    private final AdminService adminService;

    @ModelAttribute
    public void checkLoginStatus(Principal principal, Model model) {
        if (principal == null) {
            model.addAttribute("isUserLoggedIn", null);
        } else {
            final User user = this.userService.getUserByEmail(principal.getName());
            model.addAttribute("isUserLoggedIn", user);
            model.addAttribute("loggedInUserRole", user.getRole());
        }
    }

    @RequestMapping("/dashboard")
    public String dashboard(HttpSession session, Model model, Principal principal) {
        return this.adminService.dashboard(session, model, principal.getName());
    }

    @RequestMapping("/all-users")
    public String showAllUsers(Model model) {
        return this.adminService.getAllUsers(model);
    }

    @RequestMapping("/edit-user/{userEmail}")
    public String adminEditUser(
            @PathVariable("userEmail") String userEmail,
            Model model
    ) {
        return this.adminService.editUser(userEmail, model);
    }

    @RequestMapping(value = "/processUpdateUserDetails", method = RequestMethod.POST)
    public String processUpdateUserDetails(
            @RequestParam("email") String email,
            @RequestParam("role") String role,
            @RequestParam("isEnabled") String isEnabled,
            @RequestParam("accountNonLocked") String accountNonLocked
    ) {
        return this.adminService.processUpdateUserDetails(
                email,
                role,
                isEnabled,
                accountNonLocked
        );
    }

    @RequestMapping("/delete-user/{email}")
    public String deleteUser(@PathVariable("email") String email) {
        return this.adminService.deleteUser(email);
    }

    @RequestMapping(value = "/edit-profile", method = RequestMethod.GET)
    public String updateProfile(Model model, Principal principal) {
        return this.adminService.updateProfile(model, principal);
    }

    @RequestMapping(value = "/process-profile-update", method = RequestMethod.POST)
    public String processUpdateProfile(
            @ModelAttribute User newAdminInformation,
            @RequestParam("image") MultipartFile image,
            Model model,
            HttpSession session,
            Principal principal
    ) {
        return this.adminService.processUpdateProfile(
                newAdminInformation,
                image,
                model,
                session,
                principal
        );
    }


    @RequestMapping(value = "/update-password", method = RequestMethod.POST)
    public String updatePassword(
            @RequestParam("old-password") String oldPassword,
            @RequestParam("new-password") String newPassword,
            @RequestParam("confirm-new-password") String confirmNewPassword,
            HttpSession session,
            Model model,
            Principal principal
    ) {
        return this.adminService.updatePassword(
                oldPassword,
                newPassword,
                confirmNewPassword,
                session,
                model,
                principal
        );
    }

    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public String logout(HttpSession session) {
        session.setAttribute("logout", "logout");
        return "redirect:/logout";
    }

}