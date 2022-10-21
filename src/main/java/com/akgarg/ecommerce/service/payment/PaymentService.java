package com.akgarg.ecommerce.service.payment;

import com.razorpay.RazorpayException;
import org.springframework.http.ResponseEntity;

import java.security.Principal;

/**
 * @author Akhilesh Garg
 * @since 21-10-2022
 */
public interface PaymentService {

    ResponseEntity<?> createOrder(String orderAmount, Principal principal) throws RazorpayException;

    ResponseEntity<?> getSuccessPayment(double paymentAmount, String orderId, String paymentId, String signature, Principal principal);

}
