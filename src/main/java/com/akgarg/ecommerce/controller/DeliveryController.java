package com.akgarg.ecommerce.controller;

import com.akgarg.ecommerce.service.delivery.DeliveryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.security.Principal;

/**
 * @author Akhilesh Garg
 * @since 21-10-2022
 */
@Controller
@RequiredArgsConstructor
public class DeliveryController {

    private final DeliveryService deliveryService;

    @GetMapping(value = "/admin/pending-deliveries")
    public String pendingDeliveries(Model model, Principal principal) {
        return this.deliveryService.getPendingDeliveries(model, principal);
    }

    @GetMapping(value = "/admin/pending-deliveries/search/{orderDetail}")
    public String pendingDeliveriesSearch(
            @PathVariable("orderDetail") String orderDetail, Principal principal, Model model
    ) {
        return this.deliveryService.searchPendingDelivery(orderDetail, principal, model);
    }

}
