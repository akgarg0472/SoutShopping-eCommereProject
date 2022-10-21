package com.akgarg.ecommerce.utils;

import java.util.*;

/**
 * @author Akhilesh Garg
 * @since 21-10-2022
 */
public final class EcommerceUtils {

    private static final Random random = new Random();
    private static final String numbers = "0123456789";

    private EcommerceUtils() {
    }

    public static String generateOTP() {
        final StringBuilder otp = new StringBuilder();

        for (int i = 1; i <= 6; i++) {
            otp.append(numbers.charAt(random.nextInt(numbers.length())));
        }

        return otp.toString();
    }

    public static String generatePaymentReceiptNumber() {
        return "txn_" + Integer.parseInt(EcommerceUtils.generateOTP()) / 1000 + "_id_" + Integer.parseInt(EcommerceUtils.generateOTP()) / 1000;
    }

    public static String generateReviewRatingId() {
        final StringBuilder id = new StringBuilder("rid_");

        for (int i = 1; i <= 4; i++) {
            id.append(random.nextInt(9));
        }

        id.append("_");

        for (int i = 1; i <= 2; i++) {
            id.append(random.nextInt(9));
        }

        return id.toString();
    }

    public static String getEncodedImageFileName(String productName, String originalImageFileName, String extension) {
        return Arrays.toString(Base64.getEncoder().encode((productName + originalImageFileName).getBytes())) + extension;
    }

    public static String getDayHour() {
        int hourOfDay = new GregorianCalendar().get(Calendar.HOUR_OF_DAY);
        String time;

        if (hourOfDay >= 21 || hourOfDay < 5) {
            time = "Good Night";
        } else if (hourOfDay >= 5 && hourOfDay < 12) {
            time = "Good Morning";
        } else if (hourOfDay >= 12 && hourOfDay < 17) {
            time = "Good Afternoon";
        } else {
            time = "Good Evening";
        }

        return time;
    }


}
