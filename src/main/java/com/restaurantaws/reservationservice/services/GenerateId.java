package com.restaurantaws.reservationservice.services;

import java.security.SecureRandom;
import java.time.Instant;

public class GenerateId {

    private static final SecureRandom random = new SecureRandom();

    public static String generateUniqueId(){
        long timestamp = Instant.now().toEpochMilli();
        int randomNumber = random.nextInt(1000);
        String uniqueId = timestamp + "-" + randomNumber;
        return uniqueId;
    }

}
