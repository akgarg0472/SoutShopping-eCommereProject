package com.akgarg.ecommerce.service.delivery;

import org.springframework.ui.Model;

import java.security.Principal;

/**
 * @author Akhilesh Garg
 * @since 21-10-2022
 */
public interface DeliveryService {

    String getPendingDeliveries(Model model, Principal principal);

    String searchPendingDelivery(String orderDetail, Principal principal, Model model);


}
