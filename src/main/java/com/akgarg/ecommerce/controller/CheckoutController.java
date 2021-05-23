package com.akgarg.ecommerce.controller;

import com.akgarg.ecommerce.entity.Orders;
import com.akgarg.ecommerce.entity.User;
import com.akgarg.ecommerce.helper.ShippingInfo;
import com.akgarg.ecommerce.repository.OrdersRepository;
import com.akgarg.ecommerce.repository.ProductRepository;
import com.akgarg.ecommerce.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.security.Principal;
import java.util.List;

@Controller
public class CheckoutController {

    private final UserRepository userRepository;
    private final OrdersRepository ordersRepository;
    private final ProductRepository productRepository;


    @Autowired
    public CheckoutController(UserRepository userRepository,
                              OrdersRepository ordersRepository,
                              ProductRepository productRepository) {
        this.userRepository = userRepository;
        this.ordersRepository = ordersRepository;
        this.productRepository = productRepository;
    }


    @ModelAttribute
    public String checkLoginStatus(Principal principal,
                                   Model model) {
        if (principal == null) {
            model.addAttribute("isUserLoggedIn", null);
            return "redirect:/login";
        } else {
            User user = this.userRepository.getUserByEmail(principal.getName());
            model.addAttribute("isUserLoggedIn", user);
            model.addAttribute("loggedInUserRole", user.getRole());

            return "redirect:/process-checkout";
        }
    }


    @RequestMapping("/checkout")
    public String checkout(Principal principal,
                           Model model) {
        if (principal != null) {
            User user = this.userRepository.getUserByEmail(principal.getName());

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


    @RequestMapping(value = "/order-confirmed/{orderId}", method = RequestMethod.GET)
    public String orderConfirmed(@PathVariable("orderId") String orderId,
                                 Model model,
                                 Principal principal) {
        if (principal != null) {
            if (orderId.startsWith("order_")) {
                model.addAttribute("orderPlacedPageInfo", orderId.substring(6));
            } else {
                model.addAttribute("badRequest", "");
            }
        } else {
            model.addAttribute("userNotLoggedIn", "");
        }
        return "order-placed";
    }


    @RequestMapping(value = "/checkout/isUserLoggedIn", method = RequestMethod.GET)
    public ResponseEntity<Boolean> isUserLoggedIn(Principal principal) {
        return ResponseEntity.ok(principal != null);
    }
}