package com.example.komplex;

import android.graphics.Picture;

import java.io.Serializable;

public class Item implements Serializable {

    private String name;
    private int price;
    private int image;
    private boolean checked;
    private int x;
    private int y;



    public Item(String name, int price, int image, boolean checked, int x, int y) {
        this.name = name;
        this.price = price;
        this.image = image;
        this.checked = checked;
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
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
