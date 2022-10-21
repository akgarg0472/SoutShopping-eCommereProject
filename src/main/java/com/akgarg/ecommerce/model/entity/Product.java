package com.akgarg.ecommerce.model.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@SuppressWarnings("unused")
@Entity
@Table(name = "Products")
@Getter
@Setter
@ToString
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

    public Product(
            String name,
            String description,
            int originalPrice,
            int discountedPrice,
            int totalQuantities,
            String picture,
            String category
    ) {
        this.name = name;
        this.description = description;
        this.originalPrice = originalPrice;
        this.discountedPrice = discountedPrice;
        this.totalQuantities = totalQuantities;
        this.picture = picture;
        this.category = category;
    }

    @Override
    public boolean equals(Object obj) {
        Product newProduct = (Product) obj;
        return this.name.equals(newProduct.getName());
    }

}