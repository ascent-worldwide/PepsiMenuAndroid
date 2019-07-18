package com.clairvoyant.naijamenu.bean;

public class LoginParamBean {

    private String device_type;
    private String app_version;
    private String device_gcm_token;
    private String device_id;
    private String make;
    private String model;
    private String username;
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

    public String getDevice_gcm_token() {
        return device_gcm_token;
    }

    public void setDevice_gcm_token(String device_gcm_token) {
        this.device_gcm_token = device_gcm_token;
    }

    public String getDevice_id() {
        return device_id;
    }

    public void setDevice_id(String device_id) {
        this.device_id = device_id;
    }

    public String getMake() {
        return make;
    }

    public void setMake(String make) {
        this.make = make;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}