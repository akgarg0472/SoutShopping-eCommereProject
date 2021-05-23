package com.akgarg.ecommerce.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@SuppressWarnings("unused")
@Entity
@Table(name = "Products")
public class Product {

    @Id
    @Column(name = "product_name")
    private String name;


    @Column(name = "product_description", length = 1024)
    private String description;


    @Column(name = "product_original_price")
    private int originalPrice;


    @Column(name = "product_discounted_price")
    private int discountedPrice;


    @Column(name = "product_total_quantities")
    private int totalQuantities;


    @Column(name = "product_picture")
    private String picture;


    @Column(name = "product_category")
    private String category;


    public Product() {
    }

    public Product(String name,
                   String description,
                   int originalPrice,
                   int discountedPrice,
                   int totalQuantities,
                   String picture,
                   String category) {
        this.name = name;
        this.description = description;
        this.originalPrice = originalPrice;
        this.discountedPrice = discountedPrice;
        this.totalQuantities = totalQuantities;
        this.picture = picture;
        this.category = category;
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


    public int getOriginalPrice() {
        return originalPrice;
    }


    public void setOriginalPrice(int originalPrice) {
        this.originalPrice = originalPrice;
    }


    public int getDiscountedPrice() {
        return discountedPrice;
    }


    public void setDiscountedPrice(int discountedPrice) {
        this.discountedPrice = discountedPrice;
    }


    public int getTotalQuantities() {
        return totalQuantities;
    }


    public void setTotalQuantities(int totalQuantities) {
        this.totalQuantities = totalQuantities;
    }


    public String getPicture() {
        return picture;
    }


    public void setPicture(String picture) {
        this.picture = picture;
    }


    public String getCategory() {
        return category;
    }


    public void setCategory(String category) {
        this.category = category;
    }


    @Override
    public String toString() {
        return "Product{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", originalPrice=" + originalPrice +
                ", discountedPrice=" + discountedPrice +
                ", totalQuantities=" + totalQuantities +
                ", picture='" + picture + '\'' +
                ", category='" + category + '\'' +
                '}';
    }


    @Override
    public boolean equals(Object obj) {
        Product newProduct = (Product) obj;
        return this.name.equals(newProduct.getName());
    }
}