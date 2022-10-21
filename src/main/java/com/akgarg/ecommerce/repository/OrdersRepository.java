package com.akgarg.ecommerce.repository;

import com.akgarg.ecommerce.model.entity.Orders;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrdersRepository extends JpaRepository<Orders, Integer> {

    Orders findOrdersByIdEqualsAndIsOrderDeliveredEquals(
            int id,
            boolean delivered
    );

    List<Orders> findOrdersByIsOrderDeliveredEquals(boolean delivered);

    List<Orders> findOrdersByEmailIgnoreCaseAndIsOrderDeliveredEquals(
            String email,
            Boolean delivered
    );

}