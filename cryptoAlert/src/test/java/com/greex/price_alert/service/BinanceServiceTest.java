package com.greex.price_alert.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.web.client.RestTemplate;

import com.greex.price_alert.dto.PriceResponse;

@SpringBootTest
public class BinanceServiceTest {

    @Autowired
    private BinanceService binanceService;

    @MockBean
    private RestTemplate mockRestTemplate;

    @Test
    public void testGetCurrentPrice_Success() throws Exception {
        String symbol = "BTCUSDT";
        String price = "10200.00";

        // Mock RestTemplate to return a successful response
        Mockito.when(mockRestTemplate.getForObject(Mockito.anyString(), Mockito.eq(PriceResponse.class))).thenReturn(new PriceResponse(symbol, price));

        Optional<Double> currentPrice = binanceService.getCurrentPrice(symbol);

        assertEquals(10200.00, currentPrice.get());
    }

    @Test
    public void testGetHistoricalPrices_Success() throws Exception {
        String symbol = "XRPUSDT";
        int windowSize = 2;
        List<List<String>> mockResponse = List.of(
                List.of("1715904000000",
                "65235.21000000",
                "67451.20000000",
                "65106.38000000",
                "67024.00000000",
                "26292.23409000",
                "1715990399999",
                "1745971822.34474550",
                "1117254",
                "13301.09525000",
                "883129353.16334290",
                "0"),
                List.of("1715990400000",
                "67024.00000000",
                "67400.01000000",
                "66600.00000000",
                "66915.20000000",
                "14441.25774000",
                "1716076799999",
                "967145944.68353170",
                "734067",
                "6911.13424000",
                "462859253.58791080",
                "0")
        );

        // Mock RestTemplate to return historical data
        Mockito.when(mockRestTemplate.getForObject(Mockito.anyString(), Mockito.eq(List.class))).thenReturn(mockResponse);

        List<Double> historicalPrices = binanceService.getHistoricalPrices(symbol, windowSize);

        assertEquals(2, historicalPrices.size());  // Verify only closing prices are returned
        assertEquals(67024.00, historicalPrices.get(0));
        assertEquals(66915.20, historicalPrices.get(1));
    }

}