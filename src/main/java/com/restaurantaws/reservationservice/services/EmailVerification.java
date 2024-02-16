package com.restaurantaws.reservationservice.services;

import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClient;
import com.amazonaws.services.simpleemail.model.SendCustomVerificationEmailRequest;
import com.amazonaws.services.simpleemail.model.VerifyEmailAddressRequest;

public class EmailVerification {
    public void sendVerifyEmail(String email) {
        AmazonSimpleEmailService sesClient = (AmazonSimpleEmailService)AmazonSimpleEmailServiceClient.builder().build();
        SendCustomVerificationEmailRequest sendRequest = (new SendCustomVerificationEmailRequest()).withEmailAddress(email).withTemplateName("template222");
        VerifyEmailAddressRequest verifyRequest = (new VerifyEmailAddressRequest()).withEmailAddress(email);
        try {
            sesClient.verifyEmailAddress(verifyRequest);
            System.out.println("Verification request sent to: " + email);
            System.out.println("Please check your email for the verification link.");
        } catch (Exception e) {
            System.err.println("Error during email verification: " + e.getMessage());
        }
    }
}
