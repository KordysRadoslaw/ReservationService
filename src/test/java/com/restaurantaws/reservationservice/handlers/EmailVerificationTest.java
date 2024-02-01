package com.restaurantaws.reservationservice.handlers;

import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.model.VerifyEmailAddressRequest;
import com.restaurantaws.reservationservice.services.EmailVerification;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

public class EmailVerificationTest {
    @Test
    void testSendVerifyEmail() {
        // Prepare test data
        AmazonSimpleEmailService sesClient = mock(AmazonSimpleEmailService.class);
        //EmailVerification emailVerification = new EmailVerification(sesClient);

        // Prepare test data
        String email = "john.doe@example.com";

        // Invoke the tested method
        //boolean result = emailVerification.sendVerifyEmail(email);

        // Check expected results
        //assertTrue(result);

        // Verify if a method dependent on another service was called
        //verify(sesClient, times(1)).verifyEmailAddress(any(VerifyEmailAddressRequest.class));
    }
}
