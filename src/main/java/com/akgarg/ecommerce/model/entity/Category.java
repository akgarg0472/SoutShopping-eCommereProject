package com.akgarg.ecommerce.model.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
@Entity
@Table(name = "Categories")
@Data
public class Category {

    @Id
    @Column(name = "category_name")
    private String name;

    @Column(name = "category_description")
    private String description;

    @Column(name = "category_picture")
    private String picture;

    @OneToMany
    private List<Product> products = new ArrayList<>();

}
