package com.restaurantaws.reservationservice.repositories;

import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.restaurantaws.reservationservice.models.Reservation;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ReservationRepositoryImpl implements ReservationRepository {
    private final Table reservationTable;

    public ReservationRepositoryImpl(DynamoDB dynamoDB) {
        this.reservationTable = dynamoDB.getTable("RestaurantReservation");
    }

    public Reservation getReservationById(String reservationId) {
        try {
            Item item = this.reservationTable.getItem("reservationId", reservationId);
            if (item != null)
                return createReservationFromItem(item);

            return null;
        } catch (Exception e) {
            throw new RuntimeException("Error getting reservation by id: " + e);
        }
    }

    public void saveReservation(Reservation reservation) {
        LocalDateTime currentDateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDateTime = currentDateTime.format(formatter);
        try {
            Item item = (new Item()).withPrimaryKey("reservationId", reservation.getReservationId()).withString("firstName", reservation.getFirstName()).withString("lastName", reservation.getLastName()).withString("numberOfGuests", reservation.getNumberOfGuests()).withString("email", reservation.getEmail()).withString("reservationStatus", reservation.getReservationStatus()).withBoolean("confirmationToken", reservation.isConfirmation()).withString("createdDate", formattedDateTime);
            this.reservationTable.putItem(item);
        } catch (Exception e) {
            throw new RuntimeException("Error saving reservation: " + e);
        }
    }

    private Reservation createReservationFromItem(Item item) {
        Reservation reservation = new Reservation();
        reservation.setReservationId(item.getString("reservationId"));
        reservation.setFirstName(item.getString("firstName"));
        reservation.setLastName(item.getString("lastName"));
        reservation.setNumberOfGuests(item.getString("numberOfGuests"));
        reservation.setEmail(item.getString("email"));
        reservation.setReservationStatus(item.getString("reservationStatus"));
        reservation.setConfirmation(item.getBoolean("confirmationToken"));
        return reservation;
    }
}
