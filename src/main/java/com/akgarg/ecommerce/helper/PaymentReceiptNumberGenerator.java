package com.akgarg.ecommerce.helper;

public class PaymentReceiptNumberGenerator {

    public static String generate() {

        return "txn_" +
                Integer.parseInt(OTPHelper.generateOTP()) / 1000 +
                "_id_" +
                Integer.parseInt(OTPHelper.generateOTP()) / 1000;
    }
}
