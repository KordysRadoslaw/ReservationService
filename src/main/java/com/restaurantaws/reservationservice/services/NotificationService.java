package com.restaurantaws.reservationservice.services;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClientBuilder;
import com.amazonaws.services.simpleemail.model.Body;
import com.amazonaws.services.simpleemail.model.Content;
import com.amazonaws.services.simpleemail.model.Destination;
import com.amazonaws.services.simpleemail.model.Message;
import com.amazonaws.services.simpleemail.model.SendEmailRequest;
import com.amazonaws.services.simpleemail.model.SendEmailResult;
import com.restaurantaws.reservationservice.models.Notification;

/**
 * NotificationService class.
 */
public class NotificationService {
    private final AmazonSimpleEmailService sesClient;

    public NotificationService() {
        this.sesClient = (AmazonSimpleEmailService)AmazonSimpleEmailServiceClientBuilder.standard().build();
    }

    public NotificationService(AmazonSimpleEmailService sesClient) {
        this.sesClient = sesClient;
    }

    public NotificationService(AWSCredentials awsCredentials) {
        this

                .sesClient = (AmazonSimpleEmailService)((AmazonSimpleEmailServiceClientBuilder)((AmazonSimpleEmailServiceClientBuilder)AmazonSimpleEmailServiceClientBuilder.standard().withCredentials((AWSCredentialsProvider)new AWSStaticCredentialsProvider(awsCredentials))).withRegion(Regions.EU_WEST_1)).build();
    }

    /**
     * Send notification.
     * @param notification Notification object.
     */

    public void sendNotification(Notification notification) {
        String recipientEmail = notification.getRecipientEmail();
        String subject = notification.getSubject();
        String messageBody = notification.getMessageBody();
        try {
            sendEmail(recipientEmail, subject, messageBody);
        } catch (Exception e) {
            throw new RuntimeException("Error during email sending: " + e.getMessage());
        }
    }

    /**
     * Send email.
     * @param recipientEmail
     * @param subject
     * @param messageBody
     */
    public void sendEmail(String recipientEmail, String subject, String messageBody) {
        Destination destination = (new Destination()).withToAddresses(new String[] { recipientEmail });
        Content subjectContent = (new Content()).withData(subject);
        Body body = (new Body()).withText((new Content()).withData(messageBody));
        Message message = (new Message()).withSubject(subjectContent).withBody(body);
        SendEmailRequest request = (new SendEmailRequest()).withDestination(destination).withMessage(message).withSource("kordys.radoslaw@gmail.com");
        SendEmailResult result = this.sesClient.sendEmail(request);
        System.out.println("Email sent! Id: " + result.getMessageId());
    }
}
