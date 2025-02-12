package com.greex.price_alert.service;

import java.util.Optional;

public interface NotificationService {
    Optional<String> sendNotification(String userId, String message);
}
