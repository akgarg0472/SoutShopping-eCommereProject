package com.akgarg.ecommerce.controller;

import com.akgarg.ecommerce.service.payment.PaymentService;
import com.razorpay.RazorpayException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
@RequestMapping("/payment")
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping(value = "/process-payment")
    public ResponseEntity<?> createOrder(
            @RequestParam("amount") String orderAmount,
            Principal principal
    ) throws RazorpayException {
        return this.paymentService.createOrder(orderAmount, principal);
    }

    @PostMapping(value = "/fetch-success-payment")
    public ResponseEntity<?> getSuccessPayment(
            @RequestParam("amount") double paymentAmount,
            @RequestParam("order_id") String orderId,
            @RequestParam("payment_id") String paymentId,
            @RequestParam("signature") String signature,
            Principal principal
    ) {
        return this.paymentService.getSuccessPayment(paymentAmount, orderId, paymentId, signature, principal);
    }

}
