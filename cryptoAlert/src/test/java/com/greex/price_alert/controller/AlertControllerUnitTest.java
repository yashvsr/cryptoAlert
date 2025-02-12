package com.greex.price_alert.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.apache.coyote.BadRequestException;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.greex.price_alert.entity.Alert;
import com.greex.price_alert.entity.Alert.Basis;
import com.greex.price_alert.entity.Alert.Direction;
import com.greex.price_alert.entity.Alert.Status;
import com.greex.price_alert.service.AlertServiceImpl;

@SpringBootTest
public class AlertControllerUnitTest {

    @MockBean
    private AlertServiceImpl alertService;

    @Autowired
    private AlertController alertController;

    @Test
    public void testCreateAlert_success() {
        // Create a sample Alert object
        Alert newAlert = new Alert(123456789L, "BTCUSDT", Basis.PRICE, null, 69102.00, Direction.UP);

        // Mock AlertService behavior
        Alert savedAlert = new Alert(123456789L, "BTCUSDT", Basis.PRICE, null, 69102.00, Direction.UP);
        savedAlert.setStatus(Status.PENDING);
        Mockito.when(alertService.createAlert(newAlert)).thenReturn(savedAlert);

        // Call the controller method
        ResponseEntity<Alert> response = alertController.createAlert(newAlert);

        // Assertions
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(savedAlert, response.getBody());
    }

    @Test
    public void testGetAlertById_success() {
        Long alertId = 1L;
        Alert expectedAlert = new Alert(alertId, "ETHUSD", Basis.MOVING_AVERAGE, 10, 5005.00, Direction.DOWN);

        // Mock AlertService behavior
        Mockito.when(alertService.getAlertById(alertId)).thenReturn(expectedAlert);

        // Call the controller method
        ResponseEntity<Alert> response = alertController.getAlertById(alertId);

        // Assertions
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedAlert, response.getBody());
    }

    @Test
    public void testGetAlertById_notFound() {
        Long alertId = 10L;

        // Mock AlertService behavior to return null
        Mockito.when(alertService.getAlertById(alertId)).thenReturn(null);

        // Call the controller method
        ResponseEntity<Alert> response = alertController.getAlertById(alertId);

        // Assertions
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void testUpdateAlertStatusToCancelled_success() throws BadRequestException {
        Long alertId = 2L;

        // Mock AlertService behavior
        Alert existingAlert = new Alert(alertId, "LTCBTC", Basis.PRICE, 3, 5000.00, Direction.UP);
        Mockito.when(alertService.updateAlertStatus(alertId, Alert.Status.CANCELLED)).thenReturn(existingAlert);

        // Call the controller method
        ResponseEntity<Alert> response = alertController.updateAlertStatusToCancelled(alertId);

        // Assertions
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(existingAlert, response.getBody());
    }

}