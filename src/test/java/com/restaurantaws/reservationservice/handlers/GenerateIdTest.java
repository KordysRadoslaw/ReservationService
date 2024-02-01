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

        // Check if the result matches a regular expression
        //"\\d+-\\d+" means a sequence of digits, followed by a dash, followed by another sequence of digits
        assertTrue(result.matches("\\d+-\\d+"));
    }
}
