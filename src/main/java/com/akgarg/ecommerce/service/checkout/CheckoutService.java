package com.akgarg.ecommerce.service.checkout;

import org.springframework.ui.Model;

import java.security.Principal;

/**
 * @author Akhilesh Garg
 * @since 21-10-2022
 */
public interface CheckoutService {

    String checkout(Principal principal, Model model);

    String confirmOrder(String orderId, Model model, Principal principal);

}
