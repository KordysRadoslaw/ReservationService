package com.restaurantaws.reservationservice.repositories;

import com.restaurantaws.reservationservice.models.Reservation;

public interface ReservationRepository {
    Reservation getReservationById(String paramString);

    void saveReservation(Reservation paramReservation);
}
