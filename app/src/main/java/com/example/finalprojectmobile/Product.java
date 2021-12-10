package com.example.finalprojectmobile;

import android.graphics.Bitmap;

public class Product {

    private String name;
    private int quantity;
    private String imageUrl;
    public Bitmap image;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    @Override
    public String toString() {
        return "Product{" +
                "name='" + name + '\'' +
                ", quantity=" + quantity +
                ", imageUrl='" + imageUrl + '\'' +
                '}';
    }

    public Product(String name, int quantity, String imageUrl) {
        this.name = name;
        this.quantity = quantity;
        this.imageUrl = imageUrl;
    }
}
