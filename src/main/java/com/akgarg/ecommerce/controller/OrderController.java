package com.akgarg.ecommerce.controller;

import com.akgarg.ecommerce.service.order.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpSession;
import java.security.Principal;

/**
 * @author Akhilesh Garg
 * @since 21-10-2022
 */
@Controller
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    /* Admin order APIs */
    @RequestMapping(value = "/admin/delivered-orders", method = RequestMethod.GET)
    public String deliveredOrders(
            Principal principal,
            Model model
    ) {
        return this.orderService.getDeliveredOrders(principal, model);
    }

    @RequestMapping(value = "/admin/delivered-orders/search/{orderId}", method = RequestMethod.GET)
    public String searchDeliveredOrders(
            @PathVariable("orderId") String orderDetail,
            Principal principal,
            Model model
    ) {
        return this.orderService.searchDeliveredOrders(orderDetail, principal, model);
    }

    @RequestMapping("/admin/cancel-order/{orderId}")
    public String cancelOrder(
            @PathVariable("orderId") String orderId,
            Model model,
            Principal principal,
            HttpSession session
    ) {
        return this.orderService.cancelOrder(orderId, model, principal, session);
    }

    @RequestMapping("/admin/update-order/{orderId}")
    public String updateOrderStatus(
            @PathVariable("orderId") String orderId,
            Model model,
            Principal principal,
            HttpSession session
    ) {
        return this.orderService.updateOrderStatus(orderId, model, principal, session);
    }

}
