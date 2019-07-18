package com.clairvoyant.naijamenu.bean;

/**
 * DTO class used for confirm password screen
 *
 * @author Manish Agrawal
 */
public class ConfirmPasswordRequestBean {

    private String device_type;
    private String app_version;
    private int restaurant_id;
    private String password;

    public String getDevice_type() {
        return device_type;
    }

    public void setDevice_type(String device_type) {
        this.device_type = device_type;
    }

    public String getApp_version() {
        return app_version;
    }

    public void setApp_version(String app_version) {
        this.app_version = app_version;
    }

    public int getRestaurant_id() {
        return restaurant_id;
    }

    public void setRestaurant_id(int restaurant_id) {
        this.restaurant_id = restaurant_id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
