package com.restaurantaws.reservationservice.services;

public class ReservationConfirmationService {
    private final DynamoDBService dynamoDBService;

    public ReservationConfirmationService(DynamoDBService dynamoDBService) {
        this.dynamoDBService = dynamoDBService;
    }

    public boolean confirmReservation(String token) {
        if (token != null && !token.isEmpty()) {
            this.dynamoDBService.setConfirmationStatus(token, true);
            return true;
        }
        return false;
    }
}
