package com.akgarg.ecommerce.entity;

import org.hibernate.annotations.Cascade;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
@Entity
@Table(name = "Users")
public class User {

    @Id
    @Column(name = "user_email")
    private String email;


    @Column(name = "user_name")
    private String name;


    @Column(name = "user_password")
    private String password;


    @Column(name = "user_phone")
    private String phone;


    @Column(name = "user_address")
    private String address;


    @Column(name = "user_city")
    private String city;


    @Column(name = "user_state")
    private String state;


    @Column(name = "user_country")
    private String country;


    @Column(name = "user_zipcode")
    private int zipcode;


    @Column(name = "user_profile_picture")
    private String profilePicture;


    @Column(name = "user_role")
    private String role;


    @Column(name = "user_account_not_expired")
    private boolean accountNonExpired;


    @Column(name = "user_account_not_locked")
    private boolean accountNonLocked;


    @Column(name = "user_credentials_not_expired")
    private boolean credentialsNonExpired;


    @Column(name = "user_enabled")
    private boolean enabled;


    @Column(name = "user_cart")
    @OneToMany
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    private List<Orders> cart = new ArrayList<>();


    public User() {

    }


    public User(String email,
                String name,
                String password,
                String phone,
                String address,
                String city,
                String state,
                String country,
                int zipcode,
                String profilePicture,
                String role,
                boolean accountNonExpired,
                boolean accountNonLocked,
                boolean credentialsNonExpired,
                boolean enabled) {
        this.email = email;
        this.name = name;
        this.password = password;
        this.phone = phone;
        this.address = address;
        this.city = city;
        this.state = state;
        this.country = country;
        this.zipcode = zipcode;
        this.profilePicture = profilePicture;
        this.role = role;
        this.accountNonExpired = accountNonExpired;
        this.accountNonLocked = accountNonLocked;
        this.credentialsNonExpired = credentialsNonExpired;
        this.enabled = enabled;
    }


    public User(String email,
                String name,
                String password,
                String phone,
                boolean accountNonExpired,
                boolean accountNonLocked,
                boolean credentialsNonExpired,
                boolean enabled) {
        this.email = email;
        this.name = name;
        this.password = password;
        this.phone = phone;
        this.accountNonExpired = accountNonExpired;
        this.accountNonLocked = accountNonLocked;
        this.credentialsNonExpired = credentialsNonExpired;
        this.enabled = enabled;
    }


    public String getEmail() {
        return email;
    }


    public void setEmail(String email) {
        this.email = email;
    }


    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }


    public String getPassword() {
        return password;
    }


    public void setPassword(String password) {
        this.password = password;
    }


    public String getPhone() {
        return phone;
    }


    public void setPhone(String phone) {
        this.phone = phone;
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


    public String getCountry() {
        return country;
    }


    public void setCountry(String country) {
        this.country = country;
    }


    public int getZipcode() {
        return zipcode;
    }


    public void setZipcode(int zipcode) {
        this.zipcode = zipcode;
    }


    public String getProfilePicture() {
        return profilePicture;
    }


    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }


    public String getRole() {
        return role;
    }


    public void setRole(String role) {
        this.role = role;
    }


    public boolean isAccountNonExpired() {
        return accountNonExpired;
    }


    public void setAccountNonExpired(boolean accountNonExpired) {
        this.accountNonExpired = accountNonExpired;
    }


    public boolean isAccountNonLocked() {
        return accountNonLocked;
    }


    public void setAccountNonLocked(boolean accountNonLocked) {
        this.accountNonLocked = accountNonLocked;
    }


    public boolean isCredentialsNonExpired() {
        return credentialsNonExpired;
    }


    public void setCredentialsNonExpired(boolean credentialsNonExpired) {
        this.credentialsNonExpired = credentialsNonExpired;
    }


    public boolean isEnabled() {
        return enabled;
    }


    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }


    public List<Orders> getCart() {
        return cart;
    }


    public void setCart(List<Orders> cart) {
        this.cart = cart;
    }


    @Override
    public String toString() {
        return "User{" +
                "email='" + email + '\'' +
                ", name='" + name + '\'' +
                ", password='" + password + '\'' +
                ", phone='" + phone + '\'' +
                ", address='" + address + '\'' +
                ", city='" + city + '\'' +
                ", state='" + state + '\'' +
                ", country='" + country + '\'' +
                ", zipcode=" + zipcode +
                ", profilePicture='" + profilePicture + '\'' +
                ", role='" + role + '\'' +
                ", accountNonExpired=" + accountNonExpired +
                ", accountNonLocked=" + accountNonLocked +
                ", credentialsNonExpired=" + credentialsNonExpired +
                ", enabled=" + enabled +
                '}';
    }
}