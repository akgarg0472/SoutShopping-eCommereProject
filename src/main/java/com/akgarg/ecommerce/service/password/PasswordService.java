package com.akgarg.ecommerce.service.password;

import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpSession;

/**
 * @author Akhilesh Garg
 * @since 21-10-2022
 */
public interface PasswordService {

    ResponseEntity<String> processForgotPassword(String email, HttpSession session);

    ResponseEntity<String> verifyOtp(String otp, HttpSession session);

    ResponseEntity<String> processNewPassword(String newPassword, String confirmNewPassword, HttpSession session);

}
