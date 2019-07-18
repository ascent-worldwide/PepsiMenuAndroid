package com.clairvoyant.naijamenu.bean;

public class ProductBean {

    private int productId;
    private String productName;
    private String productDesc;
    private float price;
    private String productType;
    private int spiceLevel;
    private int preparationTime;
    private int detail_url_type;
    private String productUrl;
    private String[] productDetailUrl;

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductDesc() {
        return productDesc;
    }

    public void setProductDesc(String productDesc) {
        this.productDesc = productDesc;
    }

    public String getProductType() {
        return productType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }

    public int getSpiceLevel() {
        return spiceLevel;
    }

    public void setSpiceLevel(int spiceLevel) {
        this.spiceLevel = spiceLevel;
    }

    public int getPreparationTime() {
        return preparationTime;
    }

    public void setPreparationTime(int preparationTime) {
        this.preparationTime = preparationTime;
    }

    public int getDetail_url_type() {
        return detail_url_type;
    }

    public void setDetail_url_type(int detail_url_type) {
        this.detail_url_type = detail_url_type;
    }

    public String getProductUrl() {
        return productUrl;
    }

    public void setProductUrl(String productUrl) {
        this.productUrl = productUrl;
    }

    public String[] getProductDetailUrl() {
        return productDetailUrl;
    }

    public void setProductDetailUrl(String[] productDetailUrl) {
        this.productDetailUrl = productDetailUrl;
    }
}