package com.greex.price_alert.service;


import java.util.Optional;
import java.util.logging.Logger;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.greex.price_alert.service.KafkaProducerService.Notification;

@Service
public class NotificationServiceImpl implements NotificationService {

    private static final Logger logger = Logger.getLogger(NotificationService.class.getName());
    
    @Autowired
    private KafkaProducerService kafkaProducerService;

    @Override
    public Optional<String> sendNotification(String userId, String message) {
        logger.info("Sending notification to user: " + userId + ", message: " + message);
        try {
            kafkaProducerService.sendMessage(new Notification(userId, message));
        } catch (Exception e) {
            logger.severe("Unable to send notification to user: " + userId + " reason: " + e.getMessage());
            return Optional.empty();
        }
        return Optional.of("Notification sent successfully to user: " + userId);
    }
}
