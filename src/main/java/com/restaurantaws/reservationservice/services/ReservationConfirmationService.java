package com.restaurantaws.reservationservice.services;

/**
 * Service responsible for confirming reservation.
 */
public class ReservationConfirmationService {
    private final DynamoDBService dynamoDBService;

    public ReservationConfirmationService(DynamoDBService dynamoDBService) {
        this.dynamoDBService = dynamoDBService;
    }

    /**
     * Confirms reservation with given token.
     * @param token
     * @return true if reservation is confirmed, false otherwise
     */
    public boolean confirmReservation(String token) {
        if (token != null && !token.isEmpty()) {
            this.dynamoDBService.setConfirmationStatus(token, true);
            return true;
        }
        return false;
    }
}
