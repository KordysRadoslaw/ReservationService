package com.restaurantaws.reservationservice.handlers;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.google.gson.Gson;
import com.restaurantaws.reservationservice.services.*;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PostHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    private final DynamoDBService dynamoDBService;

    private final NotificationService notificationService;

    private final ReservationConfirmationService confirmationService;

    private final EmailVerification emailVerification;

    AmazonDynamoDB amazonDynamoDB;
    DynamoDB dynamoDB;
    Table table;

    AmazonSimpleEmailService sesClient;


    public PostHandler() {
        AmazonDynamoDB amazonDynamoDB = AmazonDynamoDBClient.builder().build();
        DynamoDB dynamoDB = new DynamoDB(amazonDynamoDB);
        Table table = dynamoDB.getTable("RestaurantReservation");


        this.dynamoDBService = new DynamoDBService(table);
        this.notificationService = new NotificationService();
        this.confirmationService = new ReservationConfirmationService(dynamoDBService);
        this.emailVerification = new EmailVerification();
    }

    public PostHandler(AmazonDynamoDB amazonDynamoDB, DynamoDB dynamoDB, Table table, DynamoDBService dynamoDBService, NotificationService notificationService, ReservationConfirmationService confirmationService, EmailVerification emailVerification, AmazonSimpleEmailService sesClient){
        this.dynamoDBService = dynamoDBService;
        this.notificationService = notificationService;
        this.confirmationService = confirmationService;
        this.emailVerification = emailVerification;
        this.amazonDynamoDB = amazonDynamoDB;
        this.dynamoDB = dynamoDB;
        this.table = table;
        this.sesClient = sesClient;
    }


    @Override
    public APIGatewayProxyResponseEvent handleRequest(final APIGatewayProxyRequestEvent apiGatewayProxyRequestEvent, final Context context) {

        LambdaLogger logger = context.getLogger();
        logger.log("Handling http post for /restaurant API endpoint");

        if("/restaurant/getReservation".equals(apiGatewayProxyRequestEvent.getPath())){
            return getReservation(apiGatewayProxyRequestEvent, context);

        }
        String requestBody = apiGatewayProxyRequestEvent.getBody();
        Gson gson = new Gson();


        Map<String, String> reservationDetails = gson.fromJson(requestBody, Map.class);
        String reservationId = GenerateId.generateUniqueId();


        if (reservationDetails != null) {
            reservationDetails.put("reservationId", reservationId);
        }

        //check confirmation email
        boolean isConfirmed = false;
        if (reservationDetails.containsKey("confirmed") && reservationDetails.get("confirmed").equalsIgnoreCase("true")) {
            isConfirmed = true;
        }

        LocalDateTime currentDateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDateTime = currentDateTime.format(formatter);

        logger.log("Request body: " + requestBody);

        Map<String, String> returnValue = new HashMap<>();
        String firstName = reservationDetails.get("firstName");
        String lastName = reservationDetails.get("lastName");
        String numberOfGuests = reservationDetails.get("numberOfGuests");
        String email = reservationDetails.get("email");
        String tokenId = UUID.randomUUID().toString();

        if (isConfirmed) {
            returnValue.put("confirmed", "true");

            // Send a confirmation email
            //at the moment it does not work
            String subject = "Reservation Confirmation";
            String confirmationLink = "Your reservation has been accepted. press this link to confirm. \n" +
                    "https://774oectd4g.execute-api.eu-west-1.amazonaws.com/restaurants/confirmation?token="+tokenId;

            notificationService.sendEmail(email, subject, confirmationLink);
        } else {
            returnValue.put("confirmed", "false");
        }

        if (firstName == null || lastName == null || numberOfGuests == null || email == null) {
            APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();
            response.setStatusCode(400);
            response.setBody("Missing required fields.");
            return response;
        }

        returnValue.put("reservationId", reservationDetails.get("reservationId"));
        returnValue.put("date", formattedDateTime);
        returnValue.put("firstName", firstName);
        returnValue.put("lastName", lastName);
        returnValue.put("numberOfGuests", numberOfGuests);
        returnValue.put("email", email);
        returnValue.put("tokenId", tokenId);

        boolean confirmationResult = confirmationService.confirmReservation(tokenId);
        returnValue.put("confirmed", String.valueOf(confirmationResult));

        try {
            dynamoDBService.saveData(reservationDetails.get("reservationId"), formattedDateTime, firstName, lastName, numberOfGuests, email, tokenId);
            emailVerification.sendVerifyEmail(email);
        } catch (Exception e) {
            APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();
            response.setBody("Error while saving data to DynamoDB: " + e.getMessage());
            response.setStatusCode(500);
            return response;
        }

        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();
        response.setStatusCode(200);
        response.setBody(gson.toJson(returnValue));

        logger.log("return value: " + returnValue);
        logger.log("return value: " + response.getBody());
        Map<String, String> responseHeaders = new HashMap<>();
        responseHeaders.put("Content-Type", "application/json");
        response.setHeaders(responseHeaders);

        return response;
    }

    public APIGatewayProxyResponseEvent getReservation(APIGatewayProxyRequestEvent apiGatewayProxyRequestEvent, Context context) {
        LambdaLogger logger = context.getLogger();
        logger.log("Handling http get for /restaurant/getReservation API endpoint");


        if("GET".equals(apiGatewayProxyRequestEvent.getHttpMethod())){
            try{
                Gson gson = new Gson();
                String couponId = apiGatewayProxyRequestEvent.getQueryStringParameters().get("reservationId");

                String reservationDetailsJson = dynamoDBService.getReservation(couponId);
                Map<String, String> reservationDetails = gson.fromJson(reservationDetailsJson, Map.class);

                String reservationId = reservationDetails.get("reservationId");
                String lastName = reservationDetails.get("lastName");
                String numberOfGuests = reservationDetails.get("numberOfGuests");
                String email = reservationDetails.get("email");

                Map<String, String> returnValue = new HashMap<>();
                returnValue.put("reservationId", reservationId);
                returnValue.put("lastName", lastName);
                returnValue.put("numberOfGuests", numberOfGuests);
                returnValue.put("email", email);


                APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();
                response.setStatusCode(200);
                response.setBody(gson.toJson(returnValue));
                Map<String, String> responseHeaders = new HashMap<>();
                responseHeaders.put("Content-Type", "application/json");
                response.setHeaders(responseHeaders);

                return response;
            }catch(Exception e){
                logger.log("blad przy pobieraniu rezerwacji " + e);
                APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();
                response.setStatusCode(500);
                response.setBody("Error while getting data from DynamoDB: " + e.getMessage());
                return response;

            }
        }

        return createErrorResponse(400, "Unsupported HTTP method");
    }

    private APIGatewayProxyResponseEvent createErrorResponse(int status, String errorMessage){
        APIGatewayProxyResponseEvent errorResponse = new APIGatewayProxyResponseEvent();
        errorResponse.setStatusCode(status);
        errorResponse.setBody(errorMessage);
        return errorResponse;
    }

}