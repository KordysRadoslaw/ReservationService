package com.restaurantaws.reservationservice.handlers;

import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.restaurantaws.reservationservice.services.DynamoDBService;
import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

public class DynamoDBServiceTest {

    @Test
    void testSaveData() {
        // Prepare test data
        Table table = mock(Table.class);
        DynamoDBService dynamoDBService = new DynamoDBService(table);

        // Prepare test data
        String reservationId = "123";
        String date = "2024-01-29 12:00:00";
        String firstName = "John";
        String lastName = "Doe";
        String numberOfGuests = "2";
        String email = "john.doe@example.com";
        String tokenId = "token123";

        // Invoke the tested method
        boolean result = dynamoDBService.saveData(
                reservationId, date, firstName, lastName, numberOfGuests, email, tokenId);

        // Check expected results
        assertTrue(result);

        // Verify if a method dependent on another service was called
        verify(table, times(1)).putItem(any(Item.class));
    }

    @Test
    void testGetReservation() {
        // Prepare test data
        Table table = mock(Table.class);
        DynamoDBService dynamoDBService = new DynamoDBService(table);

        // Prepare test data
        String reservationId = "123";

        // Invoke the tested method
        String result = dynamoDBService.getReservation(reservationId);

        // Check expected results
        assertNotNull(result);
        // Additional checks...

        // Verify if a method dependent on another service was called
        verify(table, times(1)).getItem(anyString(), any());
    }

    @Test
    void testSetConfirmationStatus() {
        // Prepare test data
        Table table = mock(Table.class);
        DynamoDBService dynamoDBService = new DynamoDBService(table);

        // Prepare test data
        String token = "token123";
        boolean status = true;

        // Mock DynamoDB getItem result
        Item mockedItem = mock(Item.class);
        when(table.getItem(eq("tokenId"), eq(token))).thenReturn(mockedItem);

        //invoke the tested method
        boolean result = dynamoDBService.setConfirmationStatus(token, status);

        // Check expected results
        assertTrue(result);

        // Verify if a method dependent on another service was called
        verify(table, times(1)).getItem(anyString(), any());
        verify(table, times(1)).putItem(any(Item.class));
    }
}