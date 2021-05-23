package com.akgarg.ecommerce.repository;

import com.akgarg.ecommerce.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.List;

public interface ProductRepository extends JpaRepository<Product, String> {
    // implementation will be provided by the Spring boot :)

    @Transactional
    @Modifying
    @Query("delete from Product where category=:category")
    void deleteProductByCategory(@Param("category") String category);


    List<Product> findProductsByNameContainsIgnoreCase(String name);


    List<Product> findProductsByCategoryContainsIgnoreCase(String name);


    List<Product> findProductsByDescriptionContainsIgnoreCase(String name);


    List<Product> findProductByCategory(String category);
}
