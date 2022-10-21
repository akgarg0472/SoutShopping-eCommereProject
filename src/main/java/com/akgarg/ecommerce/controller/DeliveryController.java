package com.akgarg.ecommerce.controller;

import com.akgarg.ecommerce.service.delivery.DeliveryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.security.Principal;

/**
 * @author Akhilesh Garg
 * @since 21-10-2022
 */
@Controller
@RequiredArgsConstructor
public class DeliveryController {

    private final DeliveryService deliveryService;

    @RequestMapping(value = "/admin/pending-deliveries", method = RequestMethod.GET)
    public String pendingDeliveries(Model model, Principal principal) {
        return this.deliveryService.getPendingDeliveries(model, principal);
    }

    @RequestMapping(value = "/admin/pending-deliveries/search/{orderDetail}", method = RequestMethod.GET)
    public String pendingDeliveriesSearch(
            @PathVariable("orderDetail") String orderDetail, Principal principal, Model model
    ) {
        return this.deliveryService.searchPendingDelivery(orderDetail, principal, model);
    }

}
