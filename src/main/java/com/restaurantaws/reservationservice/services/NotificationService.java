package com.restaurantaws.reservationservice.services;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClient;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClientBuilder;
import com.amazonaws.services.simpleemail.model.*;

public class NotificationService {

    private final AmazonSimpleEmailService sesClient;

    public NotificationService() {
        this.sesClient = AmazonSimpleEmailServiceClientBuilder.standard().build();
    }
    //for testing
    public NotificationService(AmazonSimpleEmailService sesClient) {
        this.sesClient = sesClient;
    }

    //for testing
    public NotificationService(AWSCredentials awsCredentials, AmazonSimpleEmailService sesClient) {
        this.sesClient = AmazonSimpleEmailServiceClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
                .withRegion(Regions.EU_WEST_1)
                .build();
    }

    public void sendEmail(String recipientEmail, String subject, String messageBody) {

        String accessKey = System.getenv("AWS_ACCESS_KEY_ID");
        String secretKey = System.getenv("AWS_SECRET_ACCESS_KEY");

        BasicAWSCredentials awsCredentials = new BasicAWSCredentials(accessKey, secretKey);
        AmazonSimpleEmailService client = AmazonSimpleEmailServiceClient.builder()
                .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
                .withRegion(Regions.EU_WEST_1)
                .build();

        // create email
        Destination destination = new Destination().withToAddresses(recipientEmail);
        Content subjectContent = new Content().withData(subject);
        Body body = new Body().withText(new Content().withData(messageBody));
        Message message = new Message().withSubject(subjectContent).withBody(body);

        // create email request
        SendEmailRequest request = new SendEmailRequest()
                .withDestination(destination)
                .withMessage(message)
                .withSource("kordys.radoslaw@gmail.com");

        // send email using the provided client
        SendEmailResult result = client.sendEmail(request);

        // print email sent confirmation
        System.out.println("Email sent! Id: " + result.getMessageId());
    }


}
