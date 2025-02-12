package com.greex.price_alert.controller;

import java.util.List;

import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.greex.price_alert.entity.Alert;
import com.greex.price_alert.service.AlertServiceImpl;

@RestController
@RequestMapping("/api/alerts")
public class AlertController {

    @Autowired
    private AlertServiceImpl alertService;

    @PostMapping
    public ResponseEntity<Alert> createAlert(@RequestBody Alert alert) {
        Alert savedAlert = alertService.createAlert(alert);
        return ResponseEntity.ok(savedAlert);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Alert> getAlertById(@PathVariable Long id) {
        Alert alert = alertService.getAlertById(id);
        if (alert == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(alert);
    }

    @GetMapping
    public ResponseEntity<List<Alert>> getAllAlerts() {
        List<Alert> alerts = alertService.getAllAlerts();
        return ResponseEntity.ok(alerts);
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<Alert> updateAlertStatusToCancelled(@PathVariable Long id) {
        Alert existingAlert;
        try {
            existingAlert = alertService.updateAlertStatus(id, Alert.Status.CANCELLED);
        } catch (BadRequestException e) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(existingAlert);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAlert(@PathVariable Long id) {
        alertService.deleteAlert(id);
        return ResponseEntity.ok(id);
    }

    //TODO : Update Alert

    //TODO : Get Alerts for UserID

    //TODO : Get alerts by Status
}