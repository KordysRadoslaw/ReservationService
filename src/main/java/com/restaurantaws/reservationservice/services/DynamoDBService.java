package com.restaurantaws.reservationservice.services;

import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.model.AmazonDynamoDBException;
import com.google.gson.Gson;


/**
 * DynamoDBService class is responsible for saving and retrieving data from the DynamoDB table.
 */
public class DynamoDBService {
    private final Table table;

    public DynamoDBService(Table table) {
        this.table = table;
    }

    /**
     * saveData method saves data to the DynamoDB table.
     * @param reservationId
     * @param date
     * @param firstName
     * @param lastName
     * @param numberOfGuests
     * @param email
     * @param tokenId
     * @param status
     * @return boolean value indicating whether the data was saved successfully or not.
     */
    public boolean saveData(String reservationId, String date, String firstName, String lastName, String numberOfGuests, String email, String tokenId, String status) {
        try {
            Item item = (new Item()).withPrimaryKey("reservationId", reservationId).withString("date", date).withString("firstName", firstName).withString("lastName", lastName).withString("numberOfGuests", numberOfGuests).withString("email", email).withString("tokenId", tokenId).withString("status", status);
            this.table.putItem(item);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * getReservation method retrieves data from the DynamoDB table.
     * @param reservationId
     * @return JSON string containing the data.
     */

    public String getReservation(String reservationId) {
        try {
            Item item = this.table.getItem("reservationId", reservationId);
            if (item != null) {
                Gson gson = new Gson();
                return gson.toJson(item.asMap());
            }
            return "{\"message\": \"no iformation about the token.\"}";
        } catch (Exception e) {
            return "{\"message\": \"database error.\"}";
        }
    }

    /**
     * setConfirmationStatus method sets the confirmation status of the reservation.
     * @param token
     * @param status
     * @return boolean value indicating whether the confirmation status was set successfully or not.
     */

    public boolean setConfirmationStatus(String token, boolean status) {
        try {
            Item item = this.table.getItem("tokenId", token);
            if (item != null) {
                item.withBoolean("confirmed", status);
                this.table.putItem(item);
                return true;
            }
            return false;
        } catch (AmazonDynamoDBException e) {
            return false;
        }
    }
}
