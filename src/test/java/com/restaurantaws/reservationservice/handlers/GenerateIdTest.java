package com.restaurantaws.reservationservice.handlers;

import com.restaurantaws.reservationservice.services.GenerateId;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class GenerateIdTest {

    @Test
    void testGenerateUniqueId() {
        // Invoke the tested method
        String result = GenerateId.generateUniqueId();

        // Check expected results
        assertNotNull(result);
        assertTrue(result.matches("\\d+-\\d+"));
    }
}
