package com.clairvoyant.naijamenu.bean;

public class ConfirmPasswordResponseBean {

    private String status;
    private String message;
    private int menu_version;

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

    public int getMenu_version() {
        return menu_version;
    }

    public void setMenu_version(int menu_version) {
        this.menu_version = menu_version;
    }
}
