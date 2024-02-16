package com.restaurantaws.reservationservice.models;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

@DynamoDBTable(tableName = "RestaurantReservation")
public class Reservation {
    private String reservationId;

    private String firstName;

    private String lastName;

    private String numberOfGuests;

    private String email;

    private String reservationStatus;

    private boolean confirmation;

    public Reservation() {}

    public Reservation(String reservationId, String firstName, String lastName, String numberOfGuests, String email, String reservationStatus, boolean confirmation) {
        this.reservationId = reservationId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.numberOfGuests = numberOfGuests;
        this.email = email;
        this.reservationStatus = reservationStatus;
        this.confirmation = confirmation;
    }

    @DynamoDBHashKey(attributeName = "reservationId")
    public String getReservationId() {
        return this.reservationId;
    }

    public void setReservationId(String reservationId) {
        this.reservationId = reservationId;
    }

    @DynamoDBAttribute
    public String getFirstName() {
        return this.firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    @DynamoDBAttribute
    public String getLastName() {
        return this.lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @DynamoDBAttribute
    public String getNumberOfGuests() {
        return this.numberOfGuests;
    }

    public void setNumberOfGuests(String numberOfGuests) {
        this.numberOfGuests = numberOfGuests;
    }

    @DynamoDBAttribute
    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @DynamoDBAttribute
    public String getReservationStatus() {
        return this.reservationStatus;
    }

    public void setReservationStatus(String reservationStatus) {
        this.reservationStatus = reservationStatus;
    }

    @DynamoDBAttribute
    public boolean isConfirmation() {
        return this.confirmation;
    }

    public void setConfirmation(boolean confirmation) {
        this.confirmation = confirmation;
    }
}
