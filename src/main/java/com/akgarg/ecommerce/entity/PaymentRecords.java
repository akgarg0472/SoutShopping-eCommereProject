package com.akgarg.ecommerce.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Entity
public class PaymentRecords {


    @Id
    private String paymentId;


    private String orderId;


    private String orderPrice;


    private String signature;


    private String username;


    private String userEmail;


    private String dateTime;


    public PaymentRecords() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        this.dateTime = dtf.format(now);
    }


    public PaymentRecords(String paymentId,
                          String orderId,
                          String orderPrice,
                          String signature,
                          String username,
                          String userEmail) {
        this.paymentId = paymentId;
        this.orderId = orderId;
        this.orderPrice = orderPrice;
        this.signature = signature;
        this.username = username;
        this.userEmail = userEmail;
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        this.dateTime = dtf.format(now);
    }


    public String getPaymentId() {
        return paymentId;
    }


    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }


    public String getOrderId() {
        return orderId;
    }


    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }


    public String getOrderPrice() {
        return orderPrice;
    }


    public void setOrderPrice(String orderPrice) {
        this.orderPrice = orderPrice;
    }


    public String getSignature() {
        return signature;
    }


    public void setSignature(String signature) {
        this.signature = signature;
    }


    public String getUsername() {
        return username;
    }


    public void setUsername(String username) {
        this.username = username;
    }


    public String getUserEmail() {
        return userEmail;
    }


    public void setUserEmail(String email) {
        this.userEmail = email;
    }


    public String getDateTime() {
        return dateTime;
    }


    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }


    @Override
    public String toString() {
        return "PaymentRecords{" +
                "paymentId='" + paymentId + '\'' +
                ", orderId='" + orderId + '\'' +
                ", orderPrice='" + orderPrice + '\'' +
                ", signature='" + signature + '\'' +
                ", username='" + username + '\'' +
                ", email='" + userEmail + '\'' +
                ", dateTime='" + dateTime + '\'' +
                '}';
    }
}