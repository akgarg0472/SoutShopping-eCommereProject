package com.akgarg.ecommerce.service.checkout;

import com.akgarg.ecommerce.model.dto.ShippingInfo;
import com.akgarg.ecommerce.model.entity.Orders;
import com.akgarg.ecommerce.model.entity.PaymentRecords;
import com.akgarg.ecommerce.model.entity.User;
import com.akgarg.ecommerce.repository.PaymentRecordsRepository;
import com.akgarg.ecommerce.repository.ProductRepository;
import com.akgarg.ecommerce.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.security.Principal;
import java.util.List;

/**
 * @author Akhilesh Garg
 * @since 21-10-2022
 */
@Service
@RequiredArgsConstructor
public class CheckoutServiceImpl implements CheckoutService {

    private final PaymentRecordsRepository paymentRecordsRepository;
    private final ProductRepository productRepository;
    private final UserService userService;

    @Override
    public String checkout(final Principal principal, final Model model) {
        if (principal != null) {
            User user = this.userService.getUserByEmail(principal.getName());

            if (user != null) {
                int orderPrice = 0;
                int discountedPrice = 0;
                List<Orders> cart = user.getCart();

                for (Orders orders : cart) {
                    discountedPrice += orders.getOrderPrice() * orders.getProductQuantities();
                    orderPrice += this.productRepository.findById(orders.getProductName()).get().getOriginalPrice() * orders.getProductQuantities();
                }

                ShippingInfo shippingInfo = new ShippingInfo();
                shippingInfo.setName(user.getName());
                shippingInfo.setEmail(user.getEmail());
                shippingInfo.setAddress(user.getAddress());
                shippingInfo.setCity(user.getCity());
                shippingInfo.setState(user.getState());
                shippingInfo.setZipCode(String.valueOf(user.getZipcode()));
                shippingInfo.setCountry(user.getCountry());
                shippingInfo.setOrderPrice(orderPrice);
                shippingInfo.setDiscountPrice(discountedPrice);

                model.addAttribute("shippingInfo", shippingInfo);
            }
        } else {
            model.addAttribute("checkoutNotAllowed", "");
        }

        return "checkout";
    }

    @Override
    public String confirmOrder(final String orderId, final Model model, final Principal principal) {
        if (principal != null) {
            if (orderId.startsWith("order_")) {
                PaymentRecords paymentRecord = this.paymentRecordsRepository.getByOrderId(orderId);

                if (paymentRecord == null) {
                    model.addAttribute("badRequest", "");
                } else {
                    model.addAttribute("orderPlacedPageInfo", orderId.substring(6));
                }
            } else {
                model.addAttribute("badRequest", "");
            }
        } else {
            model.addAttribute("userNotLoggedIn", "");
        }

        return "order-placed";
    }

}
