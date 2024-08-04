package com.akgarg.ecommerce.service.order;

import com.akgarg.ecommerce.model.dto.Message;
import com.akgarg.ecommerce.model.entity.Orders;
import com.akgarg.ecommerce.model.entity.User;
import com.akgarg.ecommerce.repository.OrdersRepository;
import com.akgarg.ecommerce.service.email.EmailService;
import com.akgarg.ecommerce.service.user.UserService;
import com.akgarg.ecommerce.utils.EcommerceUtils;
import com.akgarg.ecommerce.utils.EmailMessages;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import javax.servlet.http.HttpSession;
import java.security.Principal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author Akhilesh Garg
 * @since 21-10-2022
 */
@Service
@RequiredArgsConstructor
public class OrderService {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderService.class);

    private final OrdersRepository ordersRepository;
    private final EmailService emailService;
    private final UserService userService;

    public String getDeliveredOrders(
            final Principal principal,
            final Model model
    ) {
        return null;
    }

    public String searchDeliveredOrders(
            final String orderDetail,
            final Principal principal,
            final Model model
    ) {
        User admin = this.userService.getUserByEmail(principal.getName());
        List<Orders> allDeliveredOrders = new ArrayList<>();

        if (orderDetail.length() < 5) {
            try {
                int orderId = Integer.parseInt(orderDetail);
                Orders deliveredProduct = this.ordersRepository.findOrdersByIdEqualsAndIsOrderDeliveredEquals(orderId, true);
                if (deliveredProduct != null) {
                    allDeliveredOrders.add(deliveredProduct);
                }
            } catch (Exception e) {
                LOGGER.error("Error searching for delivered orders by id", e);
            }
        } else {
            try {
                List<Orders> deliveredProducts = this.ordersRepository.findOrdersByEmailIgnoreCaseAndIsOrderDeliveredEquals(orderDetail, true);
                if (deliveredProducts != null) {
                    allDeliveredOrders.addAll(deliveredProducts);
                }
            } catch (Exception e) {
                LOGGER.error("Error searching for delivered orders by email", e);
            }
        }

        model.addAttribute("allDeliveredOrders", allDeliveredOrders);
        model.addAttribute("currentlyLoggedInAdmin", admin);
        model.addAttribute("time", EcommerceUtils.getDayHour());

        return "admin/delivered-orders";
    }

    public String cancelOrder(
            final String orderId,
            final Model model,
            final Principal principal,
            final HttpSession session
    ) {
        User admin = this.userService.getUserByEmail(principal.getName());

        // informing user about cancellation of order
        final Optional<Orders> ordersOptional = this.ordersRepository.findById(Integer.parseInt(orderId));

        if (!ordersOptional.isPresent()) {
            LOGGER.info("order not found to cancel with id: {}", orderId);
            return "redirect:/admin/pending-deliveries";
        }

        final Orders orderToDelete = ordersOptional.get();
        this.ordersRepository.deleteById(Integer.parseInt(orderId));
        final List<Orders> orders = this.ordersRepository.findAll();

        this.emailService.sendEmail(
                orderToDelete.getEmail(),
                "Update regarding cancellation of order",
                EmailMessages.orderCancelledMessage(
                        orderToDelete.getUsername(),
                        orderToDelete.getProductName(),
                        orderToDelete.getOrderDate(),
                        orderToDelete.getOrderPrice(),
                        orderToDelete.getProductQuantities()
                )
        );

        session.setAttribute("orderCancelResponse", new Message("Order successfully cancelled", ""));
        model.addAttribute("allPendingDeliveries", orders);
        model.addAttribute("currentlyLoggedInAdmin", admin);
        model.addAttribute("time", EcommerceUtils.getDayHour());

        return "redirect:/admin/pending-deliveries";
    }

    public String updateOrderStatus(
            final String orderId,
            final Model model,
            final Principal principal,
            final HttpSession session
    ) {
        User admin = this.userService.getUserByEmail(principal.getName());

        try {
            final Optional<Orders> ordersOptional = this.ordersRepository.findById(Integer.parseInt(orderId));

            if (!ordersOptional.isPresent()) {
                throw new IllegalStateException("order not found to update by id: " + orderId);
            }

            Orders orderToProcess = ordersOptional.get();
            orderToProcess.setIsOrderDelivered(true);
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd MMMM, uuuu");
            LocalDate localDate = LocalDate.now();
            orderToProcess.setDeliveryDate(dtf.format(localDate));
            this.ordersRepository.save(orderToProcess);
            this.emailService.sendEmail(
                    orderToProcess.getEmail(),
                    "Order Delivered",
                    EmailMessages.orderSuccessfullyDeliveredMessage(
                            orderToProcess.getUsername(),
                            orderToProcess.getProductName(),
                            orderToProcess.getOrderDate(),
                            orderToProcess.getOrderPrice(),
                            orderToProcess.getProductQuantities(),
                            orderToProcess.getDeliveryDate()
                    )
            );
            session.setAttribute("orderUpdateResponse", new Message("Order status successfully updated", ""));
        } catch (Exception e) {
            LOGGER.error("error updating order status", e);
            session.setAttribute("orderUpdateResponse", new Message("Order status update failed", ""));
        }

        List<Orders> orders = this.ordersRepository.findOrdersByIsOrderDeliveredEquals(false);
        model.addAttribute("allPendingDeliveries", orders);
        model.addAttribute("currentlyLoggedInAdmin", admin);
        model.addAttribute("time", EcommerceUtils.getDayHour());

        return "redirect:/admin/pending-deliveries";
    }

}
