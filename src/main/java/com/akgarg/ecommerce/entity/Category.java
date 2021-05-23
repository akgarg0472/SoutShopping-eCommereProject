package com.akgarg.ecommerce.entity;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
@Entity
@Table(name = "Categories")
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


    public Category() {
    }


    public Category(String name,
                    String description,
                    String picture,
                    List<Product> products) {
        this.name = name;
        this.description = description;
        this.picture = picture;
        this.products = products;
    }


    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }


    public String getDescription() {
        return description;
    }


    public void setDescription(String description) {
        this.description = description;
    }


    public List<Product> getProducts() {
        return products;
    }


    public void setProducts(List<Product> products) {
        this.products = products;
    }


    public String getPicture() {
        return picture;
    }


    public void setPicture(String picture) {
        this.picture = picture;
    }


    @Override
    public String toString() {
        return "Category{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", picture='" + picture + '\'' +
                '}';
    }


}
