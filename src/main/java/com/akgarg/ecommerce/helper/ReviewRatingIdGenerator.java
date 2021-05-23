package com.akgarg.ecommerce.helper;

import java.util.Random;

public class ReviewRatingIdGenerator {

    public static String generateId() {
        Random random = new Random();
        StringBuilder id = new StringBuilder();

        id.append("rid_");

        for (int i = 1; i <= 4; i++) {
            id.append(random.nextInt(9));
        }

        id.append("_");

        for (int i = 1; i <= 2; i++) {
            id.append(random.nextInt(9));
        }

        return id.toString();
    }
}