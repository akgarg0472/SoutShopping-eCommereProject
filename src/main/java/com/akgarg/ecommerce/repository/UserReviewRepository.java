package com.akgarg.ecommerce.repository;

import com.akgarg.ecommerce.model.entity.UserReview;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserReviewRepository extends JpaRepository<UserReview, String> {

    List<UserReview> findUserReviewByProductNameContainsIgnoreCase(String productName);

}