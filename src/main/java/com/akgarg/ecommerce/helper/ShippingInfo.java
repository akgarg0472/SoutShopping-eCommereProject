package com.akgarg.ecommerce.helper;

@SuppressWarnings("unused")
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


    public ShippingInfo() {
    }


    public ShippingInfo(String name,
                        String email,
                        String address,
                        String city,
                        String zipCode,
                        String state,
                        String country,
                        int orderPrice,
                        int discountPrice) {
        this.name = name;
        this.email = email;
        this.address = address;
        this.city = city;
        this.zipCode = zipCode;
        this.state = state;
        this.country = country;
        this.orderPrice = orderPrice;
        this.discountPrice = discountPrice;
    }


    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }


    public String getEmail() {
        return email;
    }


    public void setEmail(String email) {
        this.email = email;
    }


    public String getAddress() {
        return address;
    }


    public void setAddress(String address) {
        this.address = address;
    }


    public String getCity() {
        return city;
    }


    public void setCity(String city) {
        this.city = city;
    }


    public String getState() {
        return state;
    }


    public void setState(String state) {
        this.state = state;
    }


    public String getZipCode() {
        return zipCode;
    }


    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }


    public String getCountry() {
        return country;
    }


    public void setCountry(String country) {
        this.country = country;
    }


    public int getOrderPrice() {
        return orderPrice;
    }


    public void setOrderPrice(int orderPrice) {
        this.orderPrice = orderPrice;
    }


    public int getDiscountPrice() {
        return discountPrice;
    }


    public void setDiscountPrice(int discountPrice) {
        this.discountPrice = discountPrice;
    }


    @Override
    public String toString() {
        return "ShippingInfo{" +
                "name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", address='" + address + '\'' +
                ", city='" + city + '\'' +
                ", zipCode='" + zipCode + '\'' +
                ", state='" + state + '\'' +
                ", country='" + country + '\'' +
                ", orderPrice=" + orderPrice +
                ", discountPrice=" + discountPrice +
                '}';
    }
}