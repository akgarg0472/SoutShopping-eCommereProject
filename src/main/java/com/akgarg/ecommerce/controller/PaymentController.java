package com.akgarg.ecommerce.controller;

import com.akgarg.ecommerce.entity.Orders;
import com.akgarg.ecommerce.entity.PaymentRecords;
import com.akgarg.ecommerce.entity.Product;
import com.akgarg.ecommerce.entity.User;
import com.akgarg.ecommerce.helper.EmailHelper;
import com.akgarg.ecommerce.helper.EmailMessages;
import com.akgarg.ecommerce.helper.PaymentReceiptNumberGenerator;
import com.akgarg.ecommerce.repository.OrdersRepository;
import com.akgarg.ecommerce.repository.PaymentRecordsRepository;
import com.akgarg.ecommerce.repository.ProductRepository;
import com.akgarg.ecommerce.repository.UserRepository;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/payment")
public class PaymentController {

    private final UserRepository userRepository;
    private final OrdersRepository ordersRepository;
    private final ProductRepository productRepository;
    private final PaymentRecordsRepository paymentRecordsRepository;


    @Autowired
    public PaymentController(UserRepository userRepository,
                             OrdersRepository ordersRepository,
                             ProductRepository productRepository,
                             PaymentRecordsRepository paymentRecordsRepository) {
        this.userRepository = userRepository;
        this.ordersRepository = ordersRepository;
        this.productRepository = productRepository;
        this.paymentRecordsRepository = paymentRecordsRepository;
    }


    @RequestMapping(value = "/process-payment", method = RequestMethod.POST)
    public ResponseEntity<?> createOrder(@RequestParam("amount") String orderAmount,
                                         Principal principal) throws RazorpayException {
        if (principal != null) {
            double paymentAmount = Double.parseDouble(orderAmount);

            RazorpayClient client = new RazorpayClient("key_id", "private_key");
            JSONObject order = new JSONObject();
            order.put("amount", paymentAmount * 100);   // amount in paisa
            order.put("currency", "INR");
            order.put("receipt", PaymentReceiptNumberGenerator.generate());

            Order responseOrder = client.Orders.create(order);

            return ResponseEntity.ok(responseOrder.toString());
        } else {
            return ResponseEntity.ok(false);
        }
    }


    @RequestMapping(value = "/fetch-success-payment", method = RequestMethod.POST)
    public ResponseEntity<?> getSuccessPayment(@RequestParam("amount") double paymentAmount,
                                               @RequestParam("order_id") String orderId,
                                               @RequestParam("payment_id") String paymentId,
                                               @RequestParam("signature") String signature,
                                               Principal principal) {
        if (principal != null) {
            User user = this.userRepository.getUserByEmail(principal.getName());

            if (user != null) {
                List<Orders> orders = user.getCart();
                orders.forEach(order -> {
                    Product product = this.productRepository.getOne(order.getProductName());
                    if (product != null) {
                        product.setTotalQuantities(product.getTotalQuantities() - order.getProductQuantities());
                        this.productRepository.save(product);
                    }
                    order.setOrderDelivered(false);
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

                EmailHelper.sendEmail(user.getEmail(), "Order confirmed " + orderId, EmailMessages.orderSuccessfullyPlacedMessage(user.getName(), String.valueOf(new Double(paymentAmount / 100).intValue()), orderId));

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
