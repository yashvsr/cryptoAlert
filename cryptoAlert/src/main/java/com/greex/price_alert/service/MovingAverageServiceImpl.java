package com.greex.price_alert.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MovingAverageServiceImpl implements MovingAverageService{

    @Autowired
    private BinanceServiceImpl binanceService;

    @Override
    public Optional<Double> calculateMovingAverage(String symbol, int windowSize) {
        List<Double> historicalPrices = binanceService.getHistoricalPrices(symbol, windowSize);
        Double average = historicalPrices.stream()
                .mapToDouble(Double::doubleValue)
                .average()
                .orElseGet(() -> Double.MIN_VALUE); 
        if(Double.MIN_VALUE == average) {
            return Optional.empty();
        }
        return Optional.of(average);
    }
}