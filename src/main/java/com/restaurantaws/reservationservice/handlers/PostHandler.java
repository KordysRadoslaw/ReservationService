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
import com.restaurantaws.reservationservice.models.Reservation;
import com.restaurantaws.reservationservice.repositories.ReservationRepository;
import com.restaurantaws.reservationservice.repositories.ReservationRepositoryImpl;
import com.restaurantaws.reservationservice.services.DynamoDBService;
import com.restaurantaws.reservationservice.services.EmailVerification;
import com.restaurantaws.reservationservice.services.GenerateId;
import com.restaurantaws.reservationservice.services.NotificationService;
import com.restaurantaws.reservationservice.services.ReservationConfirmationService;
import java.util.HashMap;
import java.util.Map;

/**
 * PostHandler class implements RequestHandler interface and handles the POST requests for the /restaurant API endpoint.
 */
public class PostHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    private final DynamoDBService dynamoDBService;

    private final NotificationService notificationService;

    private final ReservationConfirmationService confirmationService;

    private final EmailVerification emailVerification;

    private final ReservationRepository reservationRepository;

    AmazonDynamoDB amazonDynamoDB;

    DynamoDB dynamoDB;

    Table table;

    AmazonSimpleEmailService sesClient;

    public PostHandler() {
        AmazonDynamoDB amazonDynamoDB = (AmazonDynamoDB)AmazonDynamoDBClient.builder().build();
        DynamoDB dynamoDB = new DynamoDB(amazonDynamoDB);
        Table table = dynamoDB.getTable("RestaurantReservation");
        this.dynamoDBService = new DynamoDBService(table);
        this.notificationService = new NotificationService();
        this.confirmationService = new ReservationConfirmationService(this.dynamoDBService);
        this.emailVerification = new EmailVerification();
        this.reservationRepository = (ReservationRepository)new ReservationRepositoryImpl(dynamoDB);
    }

    public PostHandler(AmazonDynamoDB amazonDynamoDB, DynamoDB dynamoDB, Table table, DynamoDBService dynamoDBService, NotificationService notificationService, ReservationConfirmationService confirmationService, EmailVerification emailVerification, AmazonSimpleEmailService sesClient, ReservationRepository reservationRepository) {
        this.dynamoDBService = dynamoDBService;
        this.notificationService = notificationService;
        this.confirmationService = confirmationService;
        this.emailVerification = emailVerification;
        this.amazonDynamoDB = amazonDynamoDB;
        this.dynamoDB = dynamoDB;
        this.table = table;
        this.sesClient = sesClient;
        this.reservationRepository = reservationRepository;
    }

    /**
     * handleRequest method handles the POST requests for the /restaurant API endpoint.
     *
     * @param apiGatewayProxyRequestEvent The APIGatewayProxyRequestEvent object containing the request details.
     * @param context The Context object containing the Lambda runtime details.
     * @return The APIGatewayProxyResponseEvent object containing the response details.
     */
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent apiGatewayProxyRequestEvent, Context context) {
        LambdaLogger logger = context.getLogger();
        logger.log("Handling http post for /restaurant handleRequest API endpoint");
        if ("/restaurant/getReservation".equals(apiGatewayProxyRequestEvent.getPath()))
            return getReservation(apiGatewayProxyRequestEvent, context);
        String requestBody = apiGatewayProxyRequestEvent.getBody();
        Gson gson = new Gson();
        Reservation reservation = (Reservation)gson.fromJson(requestBody, Reservation.class);
        reservation.setReservationStatus("PENDING");
        reservation.setConfirmation(false);
        reservation.setReservationId(GenerateId.generateUniqueId());
        if (reservation.getFirstName() == null || reservation.getLastName() == null || reservation.getNumberOfGuests() == null || reservation.getEmail() == null)
            return createErrorResponse(400, "Missing required fields.");
        try {
            this.reservationRepository.saveReservation(reservation);
            this.emailVerification.sendVerifyEmail(reservation.getEmail());
        } catch (Exception e) {
            logger.log("Error while saving reservation: " + e);
            return createErrorResponse(500, "Error while saving reservation: " + e);
        }
        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();
        response.setStatusCode(Integer.valueOf(200));
        response.setBody(gson.toJson(reservation));
        Map<String, String> responseHeaders = new HashMap<>();
        responseHeaders.put("Content-Type", "application/json");
        response.setHeaders(responseHeaders);
        logger.log("return value: " + response.getBody());
        return response;
    }

    /**
     * getReservation method handles the GET requests for the /restaurant/getReservation API endpoint.
     *
     * @param apiGatewayProxyRequestEvent The APIGatewayProxyRequestEvent object containing the request details.
     * @param context The Context object containing the Lambda runtime details.
     * @return The APIGatewayProxyResponseEvent object containing the response details.
     */
    private APIGatewayProxyResponseEvent getReservation(APIGatewayProxyRequestEvent apiGatewayProxyRequestEvent, Context context) {
        LambdaLogger logger = context.getLogger();
        logger.log("Handling http get for /restaurant/getReservation API endpoint");
        if ("GET".equals(apiGatewayProxyRequestEvent.getHttpMethod()))
            try {
                Gson gson = new Gson();
                String reservationId = (String)apiGatewayProxyRequestEvent.getQueryStringParameters().get("reservationId");
                Reservation reservation = this.reservationRepository.getReservationById(reservationId);
                APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();
                response.setStatusCode(Integer.valueOf(200));
                response.setBody(gson.toJson(reservation));
                Map<String, String> responseHeaders = new HashMap<>();
                responseHeaders.put("Content-Type", "application/json");
                response.setHeaders(responseHeaders);
                return response;
            } catch (Exception e) {
                logger.log("Error while getting reservation details: " + e);
                return createErrorResponse(400, "Error while getting reservation details");
            }
        throw new RuntimeException("Unsupported HTTP method");
    }

    /**
     * createErrorResponse method creates an APIGatewayProxyResponseEvent object with the specified error status and message.
     *
     * @param reservationStatus The error status code.
     * @param errorMessage The error message.
     * @return The APIGatewayProxyResponseEvent object with the specified error status and message.
     */
    private APIGatewayProxyResponseEvent createErrorResponse(int reservationStatus, String errorMessage) {
        APIGatewayProxyResponseEvent errorResponse = new APIGatewayProxyResponseEvent();
        errorResponse.setStatusCode(Integer.valueOf(reservationStatus));
        errorResponse.setBody(errorMessage);
        return errorResponse;
    }
}
