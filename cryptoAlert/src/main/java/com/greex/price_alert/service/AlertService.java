package com.greex.price_alert.service;

import java.util.List;

import org.apache.coyote.BadRequestException;

import com.greex.price_alert.entity.Alert;

public interface AlertService {
    public Alert createAlert(Alert alert);
    public Alert getAlertById(Long id);
    public List<Alert> getAllAlerts();
    public Alert updateAlertStatus(Long id, Alert.Status newStatus) throws BadRequestException;
    public void deleteAlert(Long id);
    //TODO : implement getAlertsByUserId, getAlertsByStatus, updateAlert methods
}
