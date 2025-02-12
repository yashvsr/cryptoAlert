package com.greex.price_alert.service;

import com.greex.price_alert.entity.Alert;
import com.greex.price_alert.repository.AlertRepository;
import org.apache.coyote.BadRequestException;
import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AlertServiceTest {

    @Mock
    private AlertRepository alertRepository;

    @Mock
    private BinanceServiceImpl binanceService;

    @Mock
    private MovingAverageServiceImpl movingAverageService;

    @Mock
    private NotificationServiceImpl notificationService;

    @InjectMocks
    private AlertServiceImpl alertService;

    private Alert sampleAlert;

    @BeforeEach
    public void setUp() {
        sampleAlert = new Alert();
        sampleAlert.setId(1L);
        sampleAlert.setUserId(1L);
        sampleAlert.setSymbol("BTC");
        sampleAlert.setBasis(Alert.Basis.PRICE);
        sampleAlert.setValue(50000.0);
        sampleAlert.setDirection(Alert.Direction.UP);
        sampleAlert.setStatus(Alert.Status.PENDING);
    }

    @Test
    public void testCreateAlert() {
        when(alertRepository.save(any(Alert.class))).thenReturn(sampleAlert);

        Alert createdAlert = alertService.createAlert(sampleAlert);

        assertNotNull(createdAlert);
        assertEquals(sampleAlert, createdAlert);
        verify(alertRepository, times(1)).save(sampleAlert);
    }

    @Test
    public void testGetAlertById() {
        when(alertRepository.findById(1L)).thenReturn(Optional.of(sampleAlert));

        Alert foundAlert = alertService.getAlertById(1L);

        assertNotNull(foundAlert);
        assertEquals(sampleAlert, foundAlert);
        verify(alertRepository, times(1)).findById(1L);
    }

    @Test
    public void testGetAllAlerts() {
        List<Alert> alerts = Arrays.asList(sampleAlert);
        when(alertRepository.findAll()).thenReturn(alerts);

        List<Alert> foundAlerts = alertService.getAllAlerts();

        assertNotNull(foundAlerts);
        assertEquals(alerts, foundAlerts);
        verify(alertRepository, times(1)).findAll();
    }

    @Test
    public void testUpdateAlertStatus() throws BadRequestException {
        when(alertRepository.findById(1L)).thenReturn(Optional.of(sampleAlert));
        when(alertRepository.save(any(Alert.class))).thenReturn(sampleAlert);

        sampleAlert.setStatus(Alert.Status.PENDING);
        Alert updatedAlert = alertService.updateAlertStatus(1L, Alert.Status.CANCELLED);

        assertNotNull(updatedAlert);
        assertEquals(Alert.Status.CANCELLED, updatedAlert.getStatus());
        verify(alertRepository, times(1)).findById(1L);
        verify(alertRepository, times(1)).save(sampleAlert);
    }

    @Test
    public void testUpdateAlertStatusThrowsExceptionWhenAlertNotFound() {
        when(alertRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> alertService.updateAlertStatus(1L, Alert.Status.CANCELLED));
    }

    @Test
    public void testUpdateAlertStatusThrowsExceptionWhenStatusNotPending() {
        sampleAlert.setStatus(Alert.Status.TRIGERRED);
        when(alertRepository.findById(1L)).thenReturn(Optional.of(sampleAlert));

        assertThrows(BadRequestException.class, () -> alertService.updateAlertStatus(1L, Alert.Status.CANCELLED));
    }

    @Test
    public void testDeleteAlert() {
        doNothing().when(alertRepository).deleteById(1L);

        alertService.deleteAlert(1L);

        verify(alertRepository, times(1)).deleteById(1L);
    }

    @Test
    public void testValidateAlert() {
        Alert invalidAlert = new Alert();

        assertThrows(IllegalArgumentException.class, () -> alertService.createAlert(invalidAlert));

        invalidAlert.setUserId(1L);
        invalidAlert.setSymbol("");
        assertThrows(IllegalArgumentException.class, () -> alertService.createAlert(invalidAlert));

        invalidAlert.setSymbol("BTC");
        invalidAlert.setBasis(null);
        assertThrows(IllegalArgumentException.class, () -> alertService.createAlert(invalidAlert));

        invalidAlert.setBasis(Alert.Basis.PRICE);
        invalidAlert.setValue(null);
        assertThrows(IllegalArgumentException.class, () -> alertService.createAlert(invalidAlert));

        invalidAlert.setValue(50000.0);
        invalidAlert.setDirection(null);
        assertThrows(IllegalArgumentException.class, () -> alertService.createAlert(invalidAlert));

        invalidAlert.setDirection(Alert.Direction.UP);
        assertDoesNotThrow(() -> alertService.createAlert(invalidAlert));
    }
}
