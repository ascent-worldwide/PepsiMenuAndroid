package com.clairvoyant.naijamenu.bean;

import java.io.Serializable;

public class BrandPromotionRequestBean implements Serializable {
    private static final long serialVersionUID = 1L;
    private String app_version;
    private String device_type;
    private int restaurant_id;
    private int brandVideoUrlVersion;

    public String getApp_version() {
        return app_version;
    }

    public void setApp_version(String app_version) {
        this.app_version = app_version;
    }

    public String getDevice_type() {
        return device_type;
    }

    public void setDevice_type(String device_type) {
        this.device_type = device_type;
    }

    public int getRestaurant_id() {
        return restaurant_id;
    }

    public void setRestaurant_id(int restaurant_id) {
        this.restaurant_id = restaurant_id;
    }

    public int getBrandVideoUrlVersion() {
        return brandVideoUrlVersion;
    }

    public void setBrandVideoUrlVersion(int brandVideoUrlVersion) {
        this.brandVideoUrlVersion = brandVideoUrlVersion;
    }

}
