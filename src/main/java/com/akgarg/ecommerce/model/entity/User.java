package com.akgarg.ecommerce.model.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Cascade;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
@Entity
@Table(name = "Users")
@Getter
@Setter
@ToString
@NoArgsConstructor
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

    public User(
            String email,
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
            boolean enabled
    ) {
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

    public User(
            String email,
            String name,
            String password,
            String phone,
            boolean accountNonExpired,
            boolean accountNonLocked,
            boolean credentialsNonExpired,
            boolean enabled
    ) {
        this.email = email;
        this.name = name;
        this.password = password;
        this.phone = phone;
        this.accountNonExpired = accountNonExpired;
        this.accountNonLocked = accountNonLocked;
        this.credentialsNonExpired = credentialsNonExpired;
        this.enabled = enabled;
    }

}