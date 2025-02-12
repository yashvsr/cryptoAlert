package com.greex.price_alert.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest
public class MovingAverageServiceTest {

    @Autowired
    private MovingAverageService movingAverageService;

    @MockBean
    private BinanceServiceImpl mockBinanceService;

    @Test
    public void testCalculateMovingAverage_Success() {
        String symbol = "BTCUSDT";
        int windowSize = 10;
        List<Double> mockPrices = List.of(10000.0, 10200.0, 10100.0, 10300.0, 10200.0, 10105.0, 10108.0, 10150.0, 10175.0, 10100.0);

        when(mockBinanceService.getHistoricalPrices(symbol, windowSize)).thenReturn(mockPrices);

        Optional<Double> average = movingAverageService.calculateMovingAverage(symbol, windowSize);

        assertTrue(average.isPresent());
        assertEquals(10143.8, average.get());
    }

    @Test
    public void testCalculateMovingAverage_EmptyPrices() {
        String symbol = "ETHUSDT";
        int windowSize = 5;

        when(mockBinanceService.getHistoricalPrices(symbol, windowSize)).thenReturn(Collections.emptyList());

        Optional<Double> average = movingAverageService.calculateMovingAverage(symbol, windowSize);

        assertFalse(average.isPresent());
    }
}