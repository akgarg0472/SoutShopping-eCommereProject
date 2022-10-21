package com.akgarg.ecommerce.service.password;

import com.akgarg.ecommerce.model.dto.Message;
import com.akgarg.ecommerce.model.entity.User;
import com.akgarg.ecommerce.repository.UserRepository;
import com.akgarg.ecommerce.service.email.EmailService;
import com.akgarg.ecommerce.utils.EcommerceUtils;
import com.akgarg.ecommerce.utils.EmailMessages;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;

/**
 * @author Akhilesh Garg
 * @since 21-10-2022
 */
@Service
@RequiredArgsConstructor
public class PasswordServiceImpl implements PasswordService {

    private final UserRepository userRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public ResponseEntity<String> processForgotPassword(final String email, final HttpSession session) {
        User user = this.userRepository.getUserByEmail(email);
        String otp = EcommerceUtils.generateOTP();

        if (user == null) {
            return ResponseEntity.ok("USER_NOT_FOUND");
        } else {
            boolean sendEmail = this.emailService.sendEmail(email, "Forgot Password OTP", EmailMessages.forgotPasswordOTPMessage(email, user.getName(), otp));

            if (sendEmail) {
                session.setAttribute("otp", otp);
                session.setAttribute("forgotPasswordRequestUser", user);
                return ResponseEntity.ok("SUCCESS");
            } else {
                return ResponseEntity.ok("FAILED");
            }
        }
    }

    @Override
    public ResponseEntity<String> verifyOtp(final String otp, final HttpSession session) {
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

    @Override
    public ResponseEntity<String> processNewPassword(
            final String newPassword,
            final String confirmNewPassword,
            final HttpSession session
    ) {
        User user = (User) session.getAttribute("forgotPasswordRequestUser");

        if (newPassword.equals(confirmNewPassword)) {
            try {
                user.setPassword(this.passwordEncoder.encode(newPassword));
                this.userRepository.save(user);
                session.removeAttribute("forgotPasswordRequestUser");
                session.setAttribute("forgotPasswordSuccess", new Message("Password successfully updated", ""));

                this.emailService.sendEmail(user.getEmail(), "Password Successfully updated",
                                            EmailMessages.passwordSuccessfullyChangedMessage(user.getEmail(), user.getName())
                );

                return ResponseEntity.ok("PSU");
            } catch (Exception e) {
                return ResponseEntity.ok("PASS_MIS");
            }
        } else {
            return ResponseEntity.ok("ERROR");
        }
    }

}
