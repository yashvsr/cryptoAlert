package com.greex.price_alert.service;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.logging.Logger;

@Service
public class KafkaConsumerService {

    @Autowired
    private ObjectMapper objectMapper;

    private static final Logger logger = Logger.getLogger(KafkaConsumerService.class.getName());

    @KafkaListener(topics = "notification_topic", groupId = "group_id")
    public void consume(String message) {
        try {
            logger.info("Consumer message: " + message);
            Map<String, String> notificationMap = objectMapper.readValue(message, Map.class);
            String userId = notificationMap.get("userId");
            String notificationMessage = notificationMap.get("message");

            // Delegate notification sending logic
            sendNotification(userId, notificationMessage);

            logger.info("Consumed notification for user: " + userId + ", message: " + notificationMessage);
        } catch (JsonMappingException e) {
            logger.severe("Failed to parse notification message: " + message + " Reason: " + e);
        } catch (Exception e) {
            logger.severe("Unexpected error consuming notification: " + message + " Reason: " +  e);
        }
    }

    private void sendNotification(String userId, String message) {
        logger.info("Consumer sent notification for user: " + userId + ", message: " + message);
    }
}
