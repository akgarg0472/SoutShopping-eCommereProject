package com.akgarg.ecommerce.entity;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@SuppressWarnings("unused")
@Entity
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


    public Orders(String username,
                  String email,
                  String productName,
                  int productQuantities,
                  int orderPrice,
                  Boolean isOrderDelivered) {
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


    public int getId() {
        return id;
    }


    public void setId(int id) {
        this.id = id;
    }


    public String getUsername() {
        return username;
    }


    public void setUsername(String userName) {
        this.username = userName;
    }


    public String getEmail() {
        return email;
    }


    public void setEmail(String email) {
        this.email = email;
    }


    public String getProductName() {
        return productName;
    }


    public void setProductName(String productName) {
        this.productName = productName;
    }


    public int getProductQuantities() {
        return productQuantities;
    }


    public void setProductQuantities(int productQuantities) {
        this.productQuantities = productQuantities;
    }


    public int getOrderPrice() {
        return orderPrice;
    }


    public void setOrderPrice(int orderPrice) {
        this.orderPrice = orderPrice;
    }


    public String getOrderDate() {
        return this.orderDate;
    }


    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }


    public Boolean getOrderDelivered() {
        return isOrderDelivered;
    }


    public void setOrderDelivered(Boolean orderDelivered) {
        isOrderDelivered = orderDelivered;
    }


    public String getDeliveryDate() {
        return deliveryDate;
    }


    public void setDeliveryDate(String deliveryDate) {
        this.deliveryDate = deliveryDate;
    }


    @Override
    public String toString() {
        return "Orders{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", productName='" + productName + '\'' +
                ", productQuantities=" + productQuantities +
                ", orderPrice=" + orderPrice +
                ", isOrderDelivered=" + isOrderDelivered +
                ", orderDate='" + orderDate + '\'' +
                ", deliveryDate='" + deliveryDate + '\'' +
                '}';
    }


    @Override
    public boolean equals(Object obj) {
        return this.id == ((Orders) obj).id;
    }
}