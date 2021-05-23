package com.akgarg.ecommerce.helper;

import java.util.Random;

public class OTPHelper {

    public static String generateOTP() {
        StringBuilder otp = new StringBuilder();
        Random random = new Random();
        String numbers = "0123456789";

        for (int i = 1; i <= 6; i++) {
            otp.append(numbers.charAt(random.nextInt(numbers.length())));
        }

        return otp.toString();
    }
}
