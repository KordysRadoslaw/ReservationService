package com.restaurantaws.reservationservice.repositories;

import com.restaurantaws.reservationservice.models.Reservation;

/**
 * Repository interface for Reservation entity
 */
public interface ReservationRepository {

    /**
     * Get reservation by id
     * @param paramString
     * @return Reservation object retrieved from database
     */
    Reservation getReservationById(String paramString);

    /**
     * Save reservation to database
     * @param paramReservation
     */
    void saveReservation(Reservation paramReservation);
}
