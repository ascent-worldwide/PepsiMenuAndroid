package com.clairvoyant.naijamenu.bean;

public class RateRestaurantBean {
    private String id;
    private String rateRestaurantJSONRequest;

    public String getRateRestaurantJSONRequest() {
        return rateRestaurantJSONRequest;
    }

    public void setRateRestaurantJSONRequest(String rateRestaurantJSONRequest) {
        this.rateRestaurantJSONRequest = rateRestaurantJSONRequest;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
