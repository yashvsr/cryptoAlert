package com.greex.price_alert.service;

import java.util.List;
import java.util.Optional;

public interface BinanceService {
    public Optional<Double> getCurrentPrice(String symbol);
    public List<Double> getHistoricalPrices(String symbol, int windowSize);
}
