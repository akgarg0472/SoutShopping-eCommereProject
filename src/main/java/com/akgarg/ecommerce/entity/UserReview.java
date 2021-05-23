package com.akgarg.ecommerce.entity;

import com.akgarg.ecommerce.helper.ReviewRatingIdGenerator;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class UserReview {

    @Id
    private String reviewId;


    private String productName;


    private String userName;


    private int ratingStar;

    @Column(length = 1024)
    private String reviewMessage;


    public UserReview() {
        this.reviewId = ReviewRatingIdGenerator.generateId();
    }


    public UserReview(String productName,
                      String userName,
                      int ratingStar,
                      String reviewMessage) {
        this.productName = productName;
        this.userName = userName;
        this.ratingStar = ratingStar;
        this.reviewMessage = reviewMessage;
    }


    public String getReviewId() {
        return reviewId;
    }


    public void setReviewId(String reviewId) {
        this.reviewId = reviewId;
    }


    public String getUserName() {
        return userName;
    }


    public void setUserName(String userName) {
        this.userName = userName;
    }


    public int getRatingStar() {
        return ratingStar;
    }


    public void setRatingStar(int ratingStar) {
        this.ratingStar = ratingStar;
    }


    public String getReviewMessage() {
        return reviewMessage;
    }


    public void setReviewMessage(String reviewMessage) {
        this.reviewMessage = reviewMessage;
    }


    public String getProductName() {
        return productName;
    }


    public void setProductName(String productName) {
        this.productName = productName;
    }


    @Override
    public String toString() {
        return "UserReview{" +
                "reviewId='" + reviewId + '\'' +
                ", productName='" + productName + '\'' +
                ", userName='" + userName + '\'' +
                ", ratingStar=" + ratingStar +
                ", reviewMessage='" + reviewMessage + '\'' +
                '}';
    }
}