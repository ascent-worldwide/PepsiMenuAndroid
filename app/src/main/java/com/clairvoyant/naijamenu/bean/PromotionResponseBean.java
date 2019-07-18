package com.clairvoyant.naijamenu.bean;

public class PromotionResponseBean {

    private String status;
    private String message;
    private String splash_img_url;
    private int banner_type;
    private BannerListBean[] bannerList;

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

    public String getSplash_img_url() {
        return splash_img_url;
    }

    public void setSplash_img_url(String splash_img_url) {
        this.splash_img_url = splash_img_url;
    }

    public int getBanner_type() {
        return banner_type;
    }

    public void setBanner_type(int banner_type) {
        this.banner_type = banner_type;
    }

    public BannerListBean[] getBannerList() {
        return bannerList;
    }

    public void setBannerList(BannerListBean[] bannerList) {
        this.bannerList = bannerList;
    }
}