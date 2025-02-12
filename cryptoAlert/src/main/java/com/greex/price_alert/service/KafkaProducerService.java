package com.greex.price_alert.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.logging.Logger;

@Service
public class KafkaProducerService {

    private static final String TOPIC = "notification_topic";

    private static final Logger logger = Logger.getLogger(KafkaProducerService.class.getName());

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    public void sendMessage(Notification notification) {
        try {
            String notificationMessage = objectMapper.writeValueAsString(notification);
            logger.info("Kafka Producer: " + notificationMessage);
            kafkaTemplate.send(TOPIC, notificationMessage);
        } catch (JsonProcessingException e) {
            logger.severe("Failed to serialize notification message for user: " + notification.getUserId() + " Reason: " + e.getMessage());
        }
    }

    public static class Notification {
        private String userId;
        private String message;

        public Notification(String userId, String message) {
            this.userId = userId;
            this.message = message;
        }

        public String getUserId() {
            return this.userId;
        }

        public String getMessage() {
            return this.message;
        }
    }
}


