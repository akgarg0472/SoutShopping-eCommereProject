package com.akgarg.ecommerce.service.payment;

import com.akgarg.ecommerce.model.entity.Orders;
import com.akgarg.ecommerce.model.entity.PaymentRecords;
import com.akgarg.ecommerce.model.entity.Product;
import com.akgarg.ecommerce.model.entity.User;
import com.akgarg.ecommerce.repository.PaymentRecordsRepository;
import com.akgarg.ecommerce.repository.ProductRepository;
import com.akgarg.ecommerce.repository.UserRepository;
import com.akgarg.ecommerce.service.email.EmailService;
import com.akgarg.ecommerce.utils.EcommerceUtils;
import com.akgarg.ecommerce.utils.EmailMessages;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;

/**
 * @author Akhilesh Garg
 * @since 21-10-2022
 */
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final PaymentRecordsRepository paymentRecordsRepository;
    private final EmailService emailService;

    @Value("${razorpay.key}")
    private String razorpayKey;

    @Value("${razorpay.secret}")
    private String razorpaySecret;

    @Override
    public ResponseEntity<?> createOrder(final String orderAmount, final Principal principal) throws RazorpayException {
        if (principal != null) {
            double paymentAmount = Double.parseDouble(orderAmount);

            RazorpayClient client = new RazorpayClient(razorpayKey, razorpaySecret);
            JSONObject order = new JSONObject();
            order.put("amount", paymentAmount * 100);   // amount in paisa
            order.put("currency", "INR");
            order.put("receipt", EcommerceUtils.generatePaymentReceiptNumber());

            Order responseOrder = client.orders.create(order);

            return ResponseEntity.ok(responseOrder.toString());
        } else {
            return ResponseEntity.ok(false);
        }
    }

    @Override
    public ResponseEntity<?> getSuccessPayment(
            final double paymentAmount,
            final String orderId,
            final String paymentId,
            final String signature,
            final Principal principal
    ) {
        if (principal != null) {
            User user = this.userRepository.getUserByEmail(principal.getName());

            if (user != null) {
                List<Orders> orders = user.getCart();

                orders.forEach(order -> {
                    Product product = this.productRepository.getReferenceById(order.getProductName());

                    if (product != null) {
                        product.setTotalQuantities(product.getTotalQuantities() - order.getProductQuantities());
                        this.productRepository.save(product);
                    }
                    order.setIsOrderDelivered(false);
                });

                user.getCart().clear();
                this.userRepository.save(user);

                PaymentRecords paymentRecord = new PaymentRecords();
                paymentRecord.setPaymentId(paymentId);
                paymentRecord.setOrderId(orderId);
                paymentRecord.setSignature(signature);
                paymentRecord.setOrderPrice(String.valueOf(paymentAmount / 100));
                paymentRecord.setUsername(user.getName());
                paymentRecord.setUserEmail(user.getEmail());
                this.paymentRecordsRepository.save(paymentRecord);

                this.emailService.sendEmail(user.getEmail(), "Order confirmed " + orderId, EmailMessages.orderSuccessfullyPlacedMessage(user.getName(), String.valueOf(new Double(paymentAmount / 100).intValue()), orderId));

                System.out.println(orderId);

                JSONObject object = new JSONObject();
                object.put("response", true);
                object.put("responseURL", "/order-confirmed/" + orderId);
                object.put("orderUser", user.getEmail());

                return ResponseEntity.status(HttpStatus.OK).body(object.toString());
            } else {
                return ResponseEntity.ok(false);
            }
        } else {
            return ResponseEntity.ok(false);
        }
    }

}
