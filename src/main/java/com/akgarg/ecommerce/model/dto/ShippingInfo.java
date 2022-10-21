package com.akgarg.ecommerce.model.dto;

import lombok.Data;

@Data
public class ShippingInfo {

    private String name;
    private String email;
    private String address;
    private String city;
    private String zipCode;
    private String state;
    private String country;
    private int orderPrice;
    private int discountPrice;

}