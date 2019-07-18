package com.clairvoyant.naijamenu.bean;

import java.io.Serializable;

public class MenuCategoryOuterBean implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private String status;
    private String message;
    private MenuCategoryBean[] categories;

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

    public MenuCategoryBean[] getCategories() {
        return categories;
    }

    public void setCategories(MenuCategoryBean[] categories) {
        this.categories = categories;
    }
}