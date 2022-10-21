package com.akgarg.ecommerce.service.order;

import org.springframework.ui.Model;

import javax.servlet.http.HttpSession;
import java.security.Principal;

/**
 * @author Akhilesh Garg
 * @since 21-10-2022
 */
public interface OrderService {

    String getDeliveredOrders(Principal principal, Model model);

    String searchDeliveredOrders(String orderDetail, Principal principal, Model model);

    String cancelOrder(String orderId, Model model, Principal principal, HttpSession session);

    String updateOrderStatus(String orderId, Model model, Principal principal, HttpSession session);

}
