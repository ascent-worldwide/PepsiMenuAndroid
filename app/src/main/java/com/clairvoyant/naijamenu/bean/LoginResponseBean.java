package com.clairvoyant.naijamenu.bean;

public class LoginResponseBean {

    private String status;
    private String message;
    private int restaurant_id;
    private String restaurant_name;
    private String restaurant_logo;
    private String restaurant_home_screen_img;
    private String restaurant_menu_landscape_img;
    private String restaurant_menu_portrait_img;
    private String themeColor;
    private int orientation;
    private String brandVideoUrl;
    private int brandVideoUrlVersion;
    private int restaurant_logo_wid_text;
    private int menu_version;

    public int getRestaurant_logo_wid_text() {
        return restaurant_logo_wid_text;
    }

    public void setRestaurant_logo_wid_text(int restaurant_logo_wid_text) {
        this.restaurant_logo_wid_text = restaurant_logo_wid_text;
    }

    public String getBrandVideoUrl() {
        return brandVideoUrl;
    }

    public void setBrandVideoUrl(String brandVideoUrl) {
        this.brandVideoUrl = brandVideoUrl;
    }

    public int getBrandVideoUrlVersion() {
        return brandVideoUrlVersion;
    }

    public void setBrandVideoUrlVersion(int brandVideoUrlVersion) {
        this.brandVideoUrlVersion = brandVideoUrlVersion;
    }

    public int getOrientation() {
        return orientation;
    }

    public void setOrientation(int orientation) {
        this.orientation = orientation;
    }

    public String getThemeColor() {
        return themeColor;
    }

    public void setThemeColor(String themeColor) {
        this.themeColor = themeColor;
    }

    public String getRestaurant_home_screen_img() {
        return restaurant_home_screen_img;
    }

    public void setRestaurant_home_screen_img(String restaurant_home_screen_img) {
        this.restaurant_home_screen_img = restaurant_home_screen_img;
    }

    public String getRestaurant_menu_landscape_img() {
        return restaurant_menu_landscape_img;
    }

    public void setRestaurant_menu_landscape_img(String restaurant_menu_landscape_img) {
        this.restaurant_menu_landscape_img = restaurant_menu_landscape_img;
    }

    public String getRestaurant_menu_portrait_img() {
        return restaurant_menu_portrait_img;
    }

    public void setRestaurant_menu_portrait_img(String restaurant_menu_portrait_img) {
        this.restaurant_menu_portrait_img = restaurant_menu_portrait_img;
    }

    public int getMenu_version() {
        return menu_version;
    }

    public void setMenu_version(int menu_version) {
        this.menu_version = menu_version;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getRestaurant_id() {
        return restaurant_id;
    }

    public void setRestaurant_id(int restaurant_id) {
        this.restaurant_id = restaurant_id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getRestaurant_name() {
        return restaurant_name;
    }

    public void setRestaurant_name(String restaurant_name) {
        this.restaurant_name = restaurant_name;
    }

    public String getRestaurant_logo() {
        return restaurant_logo;
    }

    public void setRestaurant_logo(String restaurant_logo) {
        this.restaurant_logo = restaurant_logo;
    }
}