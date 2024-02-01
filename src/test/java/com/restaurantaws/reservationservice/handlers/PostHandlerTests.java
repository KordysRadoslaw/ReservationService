package com.restaurantaws.reservationservice.handlers;


import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.restaurantaws.reservationservice.handlers.PostHandler;
import com.restaurantaws.reservationservice.services.DynamoDBService;
import com.restaurantaws.reservationservice.services.EmailVerification;
import com.restaurantaws.reservationservice.services.NotificationService;
import com.restaurantaws.reservationservice.services.ReservationConfirmationService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class PostHandlerTests{


    @Mock
    private AmazonDynamoDB amazonDynamoDB;

    @Mock
    private DynamoDB dynamoDB;

    @Mock
    private Table table;

    @Mock
    private APIGatewayProxyRequestEvent requestEvent;

    @Mock
    private Context context;

    @Mock
    private LambdaLogger logger;
    @Mock
    private DynamoDBService dynamoDBService;

    @Mock
    private NotificationService notificationService;

    @Mock
    private ReservationConfirmationService confirmationService;

    @Mock
    private EmailVerification emailVerification;

    @InjectMocks
    private PostHandler postHandler;

    @Mock
    private AmazonSimpleEmailService sesClient;


    @Before
    public void setUp(){
        amazonDynamoDB = mock(AmazonDynamoDB.class);
        dynamoDB = mock(DynamoDB.class);
        table = mock(Table.class);
        requestEvent = mock(APIGatewayProxyRequestEvent.class);
        context = mock(Context.class);
        logger = mock(LambdaLogger.class);
        dynamoDBService = mock(DynamoDBService.class);
        notificationService = mock(NotificationService.class);
        confirmationService = mock(ReservationConfirmationService.class);
        emailVerification = mock(EmailVerification.class);
        sesClient = mock(AmazonSimpleEmailService.class);

        when(context.getLogger()).thenReturn(logger);
        when(dynamoDB.getTable(any())).thenReturn(table);
        when(dynamoDBService.saveData(anyString(), anyString(), anyString(), anyString(), anyString(), anyString(), anyString())).thenReturn(true);


        postHandler = new PostHandler(amazonDynamoDB, dynamoDB, table, dynamoDBService, notificationService, confirmationService, emailVerification, sesClient);




    }
    @Test
    public void testHandleRequestWithValidData(){
        when(requestEvent.getBody()).thenReturn("""
                {
                    "firstName": "John",
                    "lastName": "Doe",
                    "numberOfGuests": "2",
                    "email": "john.doe@example.com"
                }
                """);

        when(dynamoDBService.saveData(any(), any(), any(), any(), any(), any(), any())).thenReturn(true);
        APIGatewayProxyResponseEvent response = postHandler.handleRequest(requestEvent, context);
        assertEquals(200, response.getStatusCode());
    }

    @Test
    public void testHandleRequestWithNullEmail(){
        when(requestEvent.getBody()).thenReturn("""
                {
                    "firstName": "John",
                    "lastName": "Doe",
                    "numberOfGuests": "2",
                    "email": null
                }
                """);

        when(dynamoDBService.saveData(any(), any(), any(), any(), any(), any(), any())).thenReturn(true);
        APIGatewayProxyResponseEvent response = postHandler.handleRequest(requestEvent, context);

        assertEquals(400, response.getStatusCode());
        assertTrue(response.getBody().contains("Missing required fields."));
    }
    @Test
    public void testHandleRequestWithNullFirstName(){
        when(requestEvent.getBody()).thenReturn("""
                {
                    "firstName": null,
                    "lastName": "Doe",
                    "numberOfGuests": "2",
                    "email": "asd@gmail.com"
                }
                """);

        when(dynamoDBService.saveData(any(), any(), any(), any(), any(), any(), any())).thenReturn(true);
        APIGatewayProxyResponseEvent response = postHandler.handleRequest(requestEvent, context);

        assertEquals(400, response.getStatusCode());
        assertTrue(response.getBody().contains("Missing required fields."));
    }
    @Test
    public void testHandleRequestWithNullNumberOfGuests(){
        when(requestEvent.getBody()).thenReturn("""
                {
                    "firstName": "John",
                    "lastName": "Doe",
                    "numberOfGuests": null,
                    "email": "asd@gmail.com"
                }
                """);

        when(dynamoDBService.saveData(any(), any(), any(), any(), any(), any(), any())).thenReturn(true);
        APIGatewayProxyResponseEvent response = postHandler.handleRequest(requestEvent, context);

        assertEquals(400, response.getStatusCode());
        assertTrue(response.getBody().contains("Missing required fields."));
    }

    @Test
    public void testConfirmationServiceTrue() {
        when(confirmationService.confirmReservation(any())).thenReturn(true);
        boolean result = confirmationService.confirmReservation("tokenId");
        assertTrue(result);
    }

    @Test
    public void testConfirmationServiceFalse() {
        when(confirmationService.confirmReservation(any())).thenReturn(false);
        boolean result = confirmationService.confirmReservation("tokenId");
        assertFalse(result);
    }

}