package com.greex.price_alert.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.greex.price_alert.service.KafkaProducerService.Notification;


@SpringBootTest
public class NotificationServiceTest {

    @Autowired
    private NotificationService notificationService;

    @MockBean
    private KafkaProducerService mockKafkaProducerService;

    @Test
    public void testSendNotification_Success() {
        String userId = "test_user";
        String message = "Alert triggered!";

        // Mock KafkaProducerService behavior (no need to actually send a message)
        doNothing().when(mockKafkaProducerService).sendMessage(new Notification(userId, message));

        // Call the method under test
        Optional<String> notificationResponse = notificationService.sendNotification(userId, message);
        assertNotNull(notificationResponse);
        assertTrue(!notificationResponse.isEmpty());
        assertEquals("Notification sent successfully to user: test_user", notificationResponse.get());

    }

    @Test
    public void testSendNotification_Failure() {
        String userId = "test_user_2";
        String message = "Price alert!";

        // Mock KafkaProducerService to throw an exception
        doThrow(new RuntimeException("Simulated kafka error")).when(mockKafkaProducerService).sendMessage(new Notification(userId, message));

        // Call the method under test
        Optional<String> notificationResponse = notificationService.sendNotification(userId, message);
        assertNotNull(notificationResponse);
        assertTrue(!notificationResponse.isEmpty());

    }
}