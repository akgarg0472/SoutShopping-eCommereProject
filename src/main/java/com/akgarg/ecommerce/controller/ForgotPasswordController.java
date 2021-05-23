package com.akgarg.ecommerce.controller;

import com.akgarg.ecommerce.entity.User;
import com.akgarg.ecommerce.helper.EmailHelper;
import com.akgarg.ecommerce.helper.EmailMessages;
import com.akgarg.ecommerce.helper.Message;
import com.akgarg.ecommerce.helper.OTPHelper;
import com.akgarg.ecommerce.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.security.Principal;

@Controller
@RequestMapping("forgot-password")
public class ForgotPasswordController {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;


    @Autowired
    public ForgotPasswordController(UserRepository userRepository,
                                    BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
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


    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String forgotPassword(Principal principal) {
        if (principal != null) {
            User user = this.userRepository.getUserByEmail(principal.getName());

            if (user.getRole().equals("ROLE_ADMIN")) {
                return "redirect:/admin/dashboard";
            } else if (user.getRole().equals("ROLE_USER")) {
                return "redirect:/user/dashboard";
            }
        }
        return "forgot-password";
    }


    @RequestMapping(value = "/process-forgot-password", method = RequestMethod.POST)
    public ResponseEntity<String> processForgotPassword(@RequestParam("email") String email,
                                                        HttpSession session) {
        User user = this.userRepository.getUserByEmail(email);
        String otp = OTPHelper.generateOTP();

        if (user == null) {
            return ResponseEntity.ok("USER_NOT_FOUND");
        } else {
            boolean sendEmail = EmailHelper.sendEmail(email, "Forgot Password OTP", EmailMessages.forgotPasswordOTPMessage(email, user.getName(), otp));
            if (sendEmail) {
                session.setAttribute("otp", otp);
                session.setAttribute("forgotPasswordRequestUser", user);
                return ResponseEntity.ok("SUCCESS");
            } else {
                return ResponseEntity.ok("FAILED");
            }
        }
    }


    @RequestMapping(value = "/verify-otp", method = RequestMethod.POST)
    public ResponseEntity<String> verifyOTP(@RequestParam("otp") String otp,
                                            HttpSession session) {
        Object generatedOTP = session.getAttribute("otp");

        if (generatedOTP != null) {
            if (otp.equals(generatedOTP.toString())) {
                session.removeAttribute("otp");
                return ResponseEntity.ok("VERIFIED");
            } else {
                return ResponseEntity.ok("MISMATCHED");
            }
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("INTERNAL SERVER ERROR");
        }
    }


    @RequestMapping(value = "/process", method = RequestMethod.POST)
    public ResponseEntity<String> processNewPassword(@RequestParam("new-password") String newPassword,
                                                     @RequestParam("confirm-new-password") String confirmNewPassword,
                                                     HttpSession session) {
        User user = (User) session.getAttribute("forgotPasswordRequestUser");

        if (newPassword.equals(confirmNewPassword)) {
            try {
                user.setPassword(this.bCryptPasswordEncoder.encode(newPassword));
                this.userRepository.save(user);
                session.removeAttribute("forgotPasswordRequestUser");
                session.setAttribute("forgotPasswordSuccess", new Message("Password successfully updated", ""));
                EmailHelper.sendEmail(user.getEmail(), "Password Successfully updated",
                        EmailMessages.passwordSuccessfullyChangedMessage(user.getEmail(), user.getName()));
                return ResponseEntity.ok("PSU");
            } catch (Exception e) {
                return ResponseEntity.ok("PASS_MIS");
            }
        } else {
            return ResponseEntity.ok("ERROR");
        }
    }
}