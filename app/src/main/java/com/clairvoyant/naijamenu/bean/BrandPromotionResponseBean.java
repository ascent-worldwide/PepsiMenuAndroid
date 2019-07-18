package com.clairvoyant.naijamenu.bean;

import java.io.Serializable;

public class BrandPromotionResponseBean implements Serializable {
    private static final long serialVersionUID = 1L;
    private boolean status;
    private String brandVideoUrl;
    private int brandVideoUrlVersion;

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
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

}
