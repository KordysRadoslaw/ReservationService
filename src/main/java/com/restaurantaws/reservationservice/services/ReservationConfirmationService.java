package com.restaurantaws.reservationservice.services;

import com.restaurantaws.reservationservice.services.DynamoDBService;

public class ReservationConfirmationService {

    private final DynamoDBService dynamoDBService;

    public ReservationConfirmationService(DynamoDBService dynamoDBService) {
        this.dynamoDBService = dynamoDBService;
    }

    public boolean confirmReservation(String token){


        if(token != null && !token.isEmpty()){
            dynamoDBService.setConfirmationStatus(token, true);
            return true;
        }else{
            return false;
        }
    }


}
