package com.greex.price_alert.service;

import java.util.Optional;

public interface MovingAverageService {
    public Optional<Double> calculateMovingAverage(String symbol, int windowSize);
}
