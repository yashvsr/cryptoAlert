package com.greex.price_alert.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PriceResponse {

    @JsonProperty("symbol")
    private String symbol;

    @JsonProperty("price")
    private String price;

    public PriceResponse() {}

    public PriceResponse(String symbol, String price) {
        this.price = price;
        this.symbol = symbol;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }
}
