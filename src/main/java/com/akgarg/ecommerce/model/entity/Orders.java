package com.akgarg.ecommerce.model.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@SuppressWarnings("unused")
@Entity
@Getter
@Setter
@ToString
public class Orders {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @Column(name = "user_name")
    private String username;

    @Column(name = "user_email")
    private String email;

    @Column(name = "product_name")
    private String productName;

    @Column(name = "product_quantities")
    private int productQuantities;

    @Column(name = "order_price")
    private int orderPrice;

    @Column(name = "product_delivered")
    private Boolean isOrderDelivered;

    @Column(name = "order_date")
    private String orderDate;

    private String deliveryDate;

    public Orders() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd MMMM, uuuu");
        LocalDate localDate = LocalDate.now();
        this.orderDate = dtf.format(localDate);
    }

    public Orders(
            String username,
            String email,
            String productName,
            int productQuantities,
            int orderPrice,
            Boolean isOrderDelivered
    ) {
        this.username = username;
        this.email = email;
        this.productName = productName;
        this.productQuantities = productQuantities;
        this.orderPrice = orderPrice;
        this.isOrderDelivered = isOrderDelivered;
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd MMMM, uuuu");
        LocalDate localDate = LocalDate.now();
        this.orderDate = dtf.format(localDate);
    }

    @Override
    public boolean equals(Object obj) {
        return this.id == ((Orders) obj).id;
    }

}