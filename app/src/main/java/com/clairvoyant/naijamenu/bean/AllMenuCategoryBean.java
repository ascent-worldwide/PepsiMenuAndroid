package com.clairvoyant.naijamenu.bean;

import java.io.Serializable;

public class AllMenuCategoryBean implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private int categoryId;
    private ProductBeanOuter products;

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public ProductBeanOuter getProducts() {
        return products;
    }

    public void setProducts(ProductBeanOuter products) {
        this.products = products;
    }
}
