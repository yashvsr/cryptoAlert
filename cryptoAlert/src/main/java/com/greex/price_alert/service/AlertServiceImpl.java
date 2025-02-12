package com.greex.price_alert.service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.apache.coyote.BadRequestException;
import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import com.greex.price_alert.entity.Alert;
import com.greex.price_alert.repository.AlertRepository;

@Service
@Validated
public class AlertServiceImpl implements AlertService{

    private static final Logger logger = Logger.getLogger(AlertServiceImpl.class.getName());

    @Autowired
    private AlertRepository alertRepository;

    @Autowired
    private BinanceServiceImpl binanceService;

    @Autowired 
    private MovingAverageServiceImpl movingAverageService;

    @Autowired
    private NotificationServiceImpl notificationService;

    @Override
    public Alert createAlert(Alert alert) {
        validateAlert(alert); //TODO : Verify that user has quota of creating alerts - cap at 50 alerts per user
        return alertRepository.save(alert);
    }

    @Override
    public Alert getAlertById(Long id) {
        return alertRepository.findById(id).orElse(null);
    }

    @Override
    public List<Alert> getAllAlerts() {
        return alertRepository.findAll();
    }

    @Override
    public Alert updateAlertStatus(Long id, Alert.Status newStatus) throws BadRequestException {
        Alert existingAlert = alertRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Alert with ID " + id + " not found"));

        if (!existingAlert.getStatus().equals(Alert.Status.PENDING)) {
            throw new BadRequestException("Alert status can only be updated from PENDING to CANCELLED");
        }

        existingAlert.setStatus(newStatus);
        return alertRepository.save(existingAlert);
    }

    @Override
    public void deleteAlert(Long id) {
        alertRepository.deleteById(id);
    }

    //TODO : Currently the scheduler to checkAlerts runs every 1sec, reduce it to 500ms and use Redis to store PENDING alerts, additionally expire alerts after a fixed timeout (like 1 day)
    @Scheduled(fixedRate = 1000)
    public void checkAlerts() {
        List<Alert> pendingAlerts = alertRepository.findAll().stream()
                .filter(alert -> Alert.Status.PENDING.equals(alert.getStatus()))
                .collect(Collectors.toList());

        for (Alert alert : pendingAlerts) {
            Optional<Double> currentPrice = fetchCurrentPrice(alert);
            logger.info("For Alert: " + alert.toString() + " currentValue: " + currentPrice);
            if (currentPrice.isPresent()) {
                Optional<String> notificationResponse = handleAlert(alert, currentPrice.get());
                if(!notificationResponse.isPresent()) {
                    logger.warning("Alert trigerred but notification was not sent for alert: " + alert.toString());
                }
            } else {
                logger.warning("Error getting current price for alert: " + alert.toString());
            }
        }
    }

    private Optional<Double> fetchCurrentPrice(Alert alert) {
        if (Alert.Basis.PRICE.equals(alert.getBasis())) {
            return binanceService.getCurrentPrice(alert.getSymbol());
        } else {
            return movingAverageService.calculateMovingAverage(alert.getSymbol(), alert.getMaLength());
        }
    }

    private Optional<String> handleAlert(Alert alert, double currentValue) {
        if ((Alert.Direction.UP.equals(alert.getDirection()) && currentValue >= alert.getValue()) ||
                (Alert.Direction.DOWN.equals(alert.getDirection()) && currentValue <= alert.getValue())) {
            alert.setStatus(Alert.Status.TRIGERRED);
            alert.setUpdatedAt(LocalDateTime.now());
            alertRepository.save(alert);
            String message = String.format("Alert triggered for symbol %s: current value is %.2f", alert.getSymbol(), currentValue);
            return notificationService.sendNotification(alert.getUserId().toString(), message);
        }
        return Optional.of("Alert not trigerred");
    }


    private void validateAlert(Alert alert) {

        if(alert.getUserId() == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }

        if (alert.getSymbol() == null || alert.getSymbol().isEmpty()) {
            throw new IllegalArgumentException("Symbol cannot be null or empty");
        }

        if (!Arrays.asList(Alert.Basis.PRICE, Alert.Basis.MOVING_AVERAGE).contains(alert.getBasis())) {
            throw new IllegalArgumentException("Invalid basis. Valid values are PRICE or MOVING_AVERAGE");
        }

        if (alert.getValue() == null) {
            throw new IllegalArgumentException("Value cannot be null");
        }

        if (!Arrays.asList(Alert.Direction.UP, Alert.Direction.DOWN).contains(alert.getDirection())) {
            throw new IllegalArgumentException("Invalid direction. Valid values are UP or DOWN");
        }
    }
}