package com.clairvoyant.naijamenu.bean;

import java.io.Serializable;

public class MenuCategoryBean implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private int categoryId;
    private String categoryName;
    private String doescontainCat;
    private String categoryURL;
    private MenuCategoryBean[] subCategories;

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getDoescontainCat() {
        return doescontainCat;
    }

    public void setDoescontainCat(String doescontainCat) {
        this.doescontainCat = doescontainCat;
    }

    public String getCategoryURL() {
        return categoryURL;
    }

    public void setCategoryURL(String categoryURL) {
        this.categoryURL = categoryURL;
    }

    public MenuCategoryBean[] getSubCategories() {
        return subCategories;
    }

    public void setSubCategories(MenuCategoryBean[] subCategories) {
        this.subCategories = subCategories;
    }
}