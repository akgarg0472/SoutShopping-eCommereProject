package com.akgarg.ecommerce.utils;

public final class EmailMessages {

    public static String forgotPasswordOTPMessage(
            final String toEmail,
            final String name,
            final String otp
    ) {
        return "Dear " + "<strong>" + name + "</strong>" +
                "<br><br>" +
                "OTP to reset password of your account " + toEmail + " is " +
                "<strong>" + otp + "</strong>. Use this otp to reset your password." +
                "<br>" +
                "<br><br>" +
                "Thanks" +
                "<br><br>" +
                "Regards" +
                "<br>" +
                "Team SoutShopping" +
                "<br>" +
                "India";
    }

    public static String registrationOTPMessage(
            final String email,
            final String name,
            final String otp
    ) {
        return "Dear " + "<strong>" + name + "</strong>" +
                "<br><br>" +
                "Thank you for registering on SoutShopping." +
                "<br>" +
                "OTP to complete registration process of your account " + email + " is " +
                "<strong>" + otp + "</strong>. Use this otp to complete the registration of your account." +
                "<br><br>" +
                "Thanks" +
                "<br><br>" +
                "Regards" +
                "<br>" +
                "Team SoutShopping" +
                "<br>" +
                "India";
    }

    public static String registrationSuccessMessage(
            final String email,
            final String name
    ) {
        return "Dear " + "<strong>" + name + "</strong>" +
                "<br><br>" +
                "Thank you for registering with us on SoutShopping." +
                "<br>" +
                "We are pleased to inform you that your account with email id '" + email + "' is " +
                "successfully registered and verified with us. You can now continue your shopping with us." +
                "<br>" +
                "Have a happy and cheerful experience with us." +
                "<br><br>" +
                "Thanks" +
                "<br>" +
                "Regards" +
                "<br>" +
                "Team SoutShopping" +
                "<br>" +
                "India";
    }

    public static String passwordSuccessfullyChangedMessage(
            final String email,
            final String name
    ) {
        return "Dear " + "<strong>" + name + "</strong>" +
                "<br><br>" +
                "Password of your account " + email + " is " +
                "successfully changed. Use your new credentials to login on our portal." +
                "<br>" +
                "If you have not perform this action, then we recommend you to change your password immediately" +
                "<br><br>" +
                "Thanks" +
                "<br>" +
                "Regards" +
                "<br>" +
                "Team SoutShopping" +
                "<br>" +
                "India";
    }

    public static String orderSuccessfullyPlacedMessage(
            final String name,
            final String amount,
            final String orderId
    ) {
        return "Dear " + "<strong>" + name + "</strong>" +
                "<br><br>" +
                "Your order with order id <strong>" + orderId.substring(6) + "</strong> is successfully placed and we have received the order payment of &#x20b9;" + amount +
                ". Your order will be delivered to you within 6-7 business days. Thank you for shopping with us." +
                "<br><br>" +
                "Have a nice day." +
                "<br><br>" +
                "Regards" +
                "<br>" +
                "Team SoutShopping" +
                "<br>" +
                "India";
    }

    public static String orderSuccessfullyDeliveredMessage(
            final String name,
            final String productName,
            final String orderDate,
            final int orderPrice,
            final int productQuantity,
            final String deliveryDate
    ) {
        return "Dear " + "<strong>" + name + "</strong>," +
                "<br><br>" +
                "Your order is successfully delivered and received by " + name + ". " +
                "Details of your order are as follows:" +
                "<br><br>" +
                "<strong>Product name: </strong>'" + productName +
                "'<br>" +
                "<strong>Order price: </strong>&#8377;" + orderPrice +
                "<br>" +
                "<strong>Product quantity: </strong>" + productQuantity +
                "<br>" +
                "<strong>Order date: </strong>" + orderDate +
                "<br>" +
                "<strong>Delivery date: </strong>" + deliveryDate +
                "<br>" +
                "<br><br>" +
                " Thank you for shopping with us. Have a nice day." +
                "<br><br>" +
                "Regards" +
                "<br>" +
                "Team SoutShopping" +
                "<br>" +
                "India";
    }

    public static String orderCancelledMessage(
            final String name,
            final String productName,
            final String orderDate,
            final int orderPrice,
            final int productQuantity
    ) {
        return "Dear " + "<strong>" + name + "</strong>," +
                "<br>" +
                "Greetings from SoutShopping,<br><br>" +
                "We are sorry to inform you that your order with following details is cancelled due to unavailability of stock." +
                "<br><br>" +
                "<strong>Product name: </strong>'" + productName +
                "'<br>" +
                "<strong>Order price: </strong>&#8377;" + orderPrice +
                "<br>" +
                "<strong>Product quantity: </strong>" + productQuantity +
                "<br>" +
                "<strong>Order date: </strong>" + orderDate +
                "<br><br>" +
                "Your order amount of Rs. " + orderPrice + "will be refunded to you in 6-7 working days. Please contact to our team regarding the refund process." +
                "<br><br>" +
                " Thank you for choosing SoutShopping. Have a nice day. Stay safe..." +
                "<br><br>" +
                "Regards" +
                "<br>" +
                "Team SoutShopping" +
                "<br>" +
                "India";
    }

}