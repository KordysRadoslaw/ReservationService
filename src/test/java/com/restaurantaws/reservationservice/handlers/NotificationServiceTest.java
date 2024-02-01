package com.restaurantaws.reservationservice.handlers;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClientBuilder;
import com.amazonaws.services.simpleemail.model.SendEmailRequest;
import com.restaurantaws.reservationservice.services.NotificationService;
import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

public class NotificationServiceTest {
    @Test
    void testSendEmail() {
//        // Prepare test data
//        AWSCredentials awsCredentials = new BasicAWSCredentials("fakeAccessKey", "fakeSecretKey");
//        AmazonSimpleEmailService sesClient = mock(AmazonSimpleEmailService.class);
//        NotificationService notificationService = new NotificationService(awsCredentials, sesClient);
//
//        // Prepare test data
//        String recipientEmail = "john.doe@example.com";
//        String subject = "Subject";
//        String messageBody = "Body";
//
//        // Invoke the tested method
//        boolean result;
//        try {
//            notificationService.sendEmail(recipientEmail, subject, messageBody);
//            result = true;
//        } catch (AssertionError e) {
//            result = false;
//        }
//
//        // Check expected results
//        assertTrue(result);
//
//        // Verify if a method dependent on another service was called
//        verify(sesClient, times(1)).sendEmail(any(SendEmailRequest.class));
   }
}