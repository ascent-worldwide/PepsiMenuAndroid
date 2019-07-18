package com.clairvoyant.naijamenu.bean;

import java.io.Serializable;

public class AllMenuCategoryOuterBean implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private String status;
    private String message;
    private AllMenuCategoryBean[] categories;

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

    public AllMenuCategoryBean[] getCategories() {
        return categories;
    }

    public void setCategories(AllMenuCategoryBean[] categories) {
        this.categories = categories;
    }
}
