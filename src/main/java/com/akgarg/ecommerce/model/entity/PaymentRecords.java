package com.akgarg.ecommerce.model.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Entity
@Getter
@Setter
@ToString
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

}