package com.clairvoyant.naijamenu.bean;

import java.io.Serializable;

public class DrawerBean implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private String itemName;
    private int itemImg;
    private boolean isSelected = false;


    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public int getItemImg() {
        return itemImg;
    }

    public void setItemImg(int itemImg) {
        this.itemImg = itemImg;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }
}