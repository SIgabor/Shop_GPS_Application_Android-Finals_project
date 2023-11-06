package com.example.komplex;

import android.graphics.Picture;

public class Item {

    private String name;
    private int price;
    private int image;
    private boolean checked;



    public Item(String name, int price, int image, boolean checked) {
        this.name = name;
        this.price = price;
        this.image = image;
        this.checked = checked;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return String.valueOf(price);
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public String isChecked() {
        return String.valueOf(checked);
    }
    public boolean getChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }
}
