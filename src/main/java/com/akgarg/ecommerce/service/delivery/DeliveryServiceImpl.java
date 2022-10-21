package com.akgarg.ecommerce.service.delivery;

import com.akgarg.ecommerce.model.entity.Orders;
import com.akgarg.ecommerce.model.entity.User;
import com.akgarg.ecommerce.repository.OrdersRepository;
import com.akgarg.ecommerce.service.user.UserService;
import com.akgarg.ecommerce.utils.EcommerceUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Akhilesh Garg
 * @since 21-10-2022
 */
@Service
@RequiredArgsConstructor
public class DeliveryServiceImpl implements DeliveryService {

    private final UserService userService;
    private final OrdersRepository ordersRepository;

    @Override
    public String getPendingDeliveries(final Model model, final Principal principal) {
        User admin = this.userService.getUserByEmail(principal.getName());
        List<Orders> orders = this.ordersRepository.findOrdersByIsOrderDeliveredEquals(false);

        model.addAttribute("allPendingDeliveries", orders);
        model.addAttribute("currentlyLoggedInAdmin", admin);
        model.addAttribute("time", EcommerceUtils.getDayHour());

        return "admin/pending-deliveries";
    }

    @Override
    public String searchPendingDelivery(final String orderDetail, final Principal principal, final Model model) {
        User admin = this.userService.getUserByEmail(principal.getName());
        List<Orders> orders = new ArrayList<>();

        if (orderDetail.length() < 5) {
            try {
                int orderId = Integer.parseInt(orderDetail);
                Orders pendingOrderById = this.ordersRepository.findOrdersByIdEqualsAndIsOrderDeliveredEquals(orderId, false);
                if (pendingOrderById != null) {
                    orders.add(pendingOrderById);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            try {
                List<Orders> ordersByEmail = this.ordersRepository.findOrdersByEmailIgnoreCaseAndIsOrderDeliveredEquals(orderDetail, false);
                if (ordersByEmail != null) {
                    orders.addAll(ordersByEmail);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        model.addAttribute("allPendingDeliveries", orders);
        model.addAttribute("currentlyLoggedInAdmin", admin);
        model.addAttribute("time", EcommerceUtils.getDayHour());

        return "admin/pending-deliveries";
    }

}
