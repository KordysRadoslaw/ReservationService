package com.restaurantaws.reservationservice.handlers;

import com.restaurantaws.reservationservice.services.DynamoDBService;
import com.restaurantaws.reservationservice.services.ReservationConfirmationService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

public class ReservationConfirmationServiceTest {
    @Test
    void testConfirmReservation() {
        // Prepare test data
        DynamoDBService dynamoDBService = mock(DynamoDBService.class);
        ReservationConfirmationService confirmationService = new ReservationConfirmationService(dynamoDBService);

        // Prepare test data
        String token = "token123";

        // Invoke the tested method
        boolean result = confirmationService.confirmReservation(token);

        // Check expected results
        assertTrue(result);

        // Verify if a method dependent on another service was called
        verify(dynamoDBService, times(1)).setConfirmationStatus(anyString(), anyBoolean());
    }
}
