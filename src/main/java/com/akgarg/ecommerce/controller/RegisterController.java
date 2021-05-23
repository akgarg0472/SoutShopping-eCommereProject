package com.akgarg.ecommerce.controller;

import com.akgarg.ecommerce.entity.User;
import com.akgarg.ecommerce.helper.*;
import com.akgarg.ecommerce.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
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

@SuppressWarnings({"unused", "FieldCanBeLocal"})
@Controller
@RequestMapping("/register")
public class RegisterController {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;


    @Autowired
    public RegisterController(UserRepository userRepository,
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


    @RequestMapping(value = "/signup", method = RequestMethod.POST)
    public ResponseEntity<String> signup(@ModelAttribute User user,
                                         @RequestParam("confirm_password") String confirmPassword,
                                         HttpSession session) {
        User databaseUser = null;

        try {
            databaseUser = this.userRepository.findById(user.getEmail()).get();
            session.setAttribute("signupFormResponse", new Message("Email already registered", "alert-danger"));
            return ResponseEntity.ok("EXISTS");
        } catch (Exception e) {
            System.out.println(e.getClass() + " -> " + e.getMessage());
        }

        if (!user.getPassword().equals(confirmPassword)) {
            session.setAttribute("signupFormResponse", new Message("Password didn't match", "alert-danger"));
            return ResponseEntity.ok("PASS_MISS");
        }

        user.setPassword(this.bCryptPasswordEncoder.encode(user.getPassword()));
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

        String otp = OTPHelper.generateOTP();
        boolean confirmationOtpEmail = EmailHelper.sendEmail(user.getEmail(), "Registration confirmation OTP",
                EmailMessages.registrationOTPMessage(user.getEmail(), user.getName(), otp));

        if (confirmationOtpEmail) {
            session.setAttribute("newRegisteredUser", user);
            session.setAttribute("registrationOTP", otp);
            session.setAttribute("signupFormResponse", new Message("Registration successful", "alert-success"));
            return ResponseEntity.ok("REGISTERED");
        } else {
            return ResponseEntity.ok("ERROR_SENDING_EMAIL");
        }
    }


    @RequestMapping(value = "/verify-otp", method = RequestMethod.POST)
    public ResponseEntity<String> verifyRegistrationOTP(@RequestParam("otp") String otp,
                                                        HttpSession session) {
        Object generatedOTP = session.getAttribute("registrationOTP");

        if (generatedOTP != null) {
            if (generatedOTP.toString().equals(otp)) {
                try {
                    User newUser = (User) session.getAttribute("newRegisteredUser");
                    this.userRepository.save(newUser);
                    session.removeAttribute("newRegisteredUser");
                    session.removeAttribute("registrationOTP");
                    session.setAttribute("registrationSuccess", "");
                    EmailHelper.sendEmail(newUser.getEmail(), "Registration Successful",
                            EmailMessages.registrationSuccessMessage(newUser.getEmail(), newUser.getName()));
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
}