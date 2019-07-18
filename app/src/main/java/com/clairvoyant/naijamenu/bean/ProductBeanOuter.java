package com.clairvoyant.naijamenu.bean;

public class ProductBeanOuter {

    private String status;
    private String message;
    private ProductBean[] productlist;

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

    public ProductBean[] getProductlist() {
        return productlist;
    }

    public void setProductlist(ProductBean[] productlist) {
        this.productlist = productlist;
    }
}