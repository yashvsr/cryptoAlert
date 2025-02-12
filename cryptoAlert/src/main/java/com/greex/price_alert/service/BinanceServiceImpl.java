package com.greex.price_alert.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.greex.price_alert.dto.PriceResponse;

import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Service
public class BinanceServiceImpl implements BinanceService {

    private static final Logger logger = Logger.getLogger(BinanceService.class.getName());

    @Value("${binance.api.url}")
    private String binanceApiUrl;

    @Autowired
    private final RestTemplate restTemplate;

    @Autowired
    public BinanceServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
    // TODO : Use Protobuf for communication with Binance APIs
    @Override
    public Optional<Double> getCurrentPrice(String symbol) {
        String url = binanceApiUrl + "/api/v3/ticker/price?symbol=" + symbol;
        try {
            PriceResponse response = restTemplate.getForObject(url, PriceResponse.class);
            if (response != null && response.getPrice() != null) {
                return Optional.of(Double.parseDouble(response.getPrice()));
            } else {
                logger.warning("Failed to fetch current price for symbol: " + symbol);
                throw new BinanceApiException("No price data found for symbol: " + symbol);
            }
        } catch (Exception e) {
            logger.severe("Error fetching current price for symbol: " + symbol + " Reason: " + e.getMessage());
            throw new BinanceApiException("Failed to fetch current price from Binance", e);
        }
    }

    @Override
    public List<Double> getHistoricalPrices(String symbol, int windowSize) {
        String url = binanceApiUrl + "/api/v3/klines?symbol=" + symbol + "&interval=1d&limit=" + windowSize;
        try {
            List<List<String>> response = restTemplate.getForObject(url, List.class);
            if (response != null) {
                return response.stream()
                        .map(kline -> Double.parseDouble(kline.get(4)))  // Close price is the 5th element
                        .collect(Collectors.toList());
            } else {
                logger.warning("Failed to fetch historical prices for symbol: " + symbol);
                throw new BinanceApiException("No historical data found for symbol: " + symbol);
            }
        } catch (Exception e) {
            logger.severe("Error fetching historical prices for symbol: " + symbol + " Reason: " + e.getMessage());
            throw new BinanceApiException("Failed to fetch historical prices from Binance", e);
        }
    }

    public static class BinanceApiException extends RuntimeException {
        public BinanceApiException(String message) {
            super(message);
        }

        public BinanceApiException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}