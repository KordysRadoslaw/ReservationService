package com.restaurantaws.reservationservice.handlers;

import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.model.SendEmailRequest;
import com.restaurantaws.reservationservice.services.NotificationService;
import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

public class NotificationServiceTest {
    @Test
    void testSendEmail() {
        // Prepare test data
        //AmazonSimpleEmailService sesClient = mock(AmazonSimpleEmailService.class);
        //NotificationService notificationService = new NotificationService(sesClient);

        // Prepare test data
        String recipientEmail = "john.doe@example.com";
        String subject = "Subject";
        String messageBody = "Body";

        // Invoke the tested method
        //boolean result = notificationService.sendEmail(recipientEmail, subject, messageBody);

        // Check expected results
        //assertTrue(result);

        // Verify if a method dependent on another service was called
        //verify(sesClient, times(1)).sendEmail(any(SendEmailRequest.class));
    }
}
