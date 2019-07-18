package com.clairvoyant.naijamenu.bean;

/**
 * Class used to get updated app version from API
 *
 * @author Manish Agrawal
 */
public class AppVersionResponseBean {
    private String status;
    private String message;
    private String app_version;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getApp_version() {
        return app_version;
    }

    public void setApp_version(String app_version) {
        this.app_version = app_version;
    }
}
