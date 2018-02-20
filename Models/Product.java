package com.example.tanapone.smartcashier.Models;

import com.example.tanapone.smartcashier.Models.Category;

/**
 * Created by Tanapone on 28/1/2561.
 */

public class Product {
    private String productID;
    private String productName;
    private int productQuantity;
    private Category category;
    private double capitalPrice;
    private double salePrice;

    public String getProductID() {
        return productID;
    }

    public void setProductID(String productID) {
        this.productID = productID;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getProductQuantity() {
        return productQuantity;
    }

    public void setProductQuantity(int productQuantity) {
        this.productQuantity = productQuantity;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public double getCapitalPrice() {
        return capitalPrice;
    }

    public void setCapitalPrice(double capitalPrice) {
        this.capitalPrice = capitalPrice;
    }

    public double getSalePrice() {
        return salePrice;
    }

    public void setSalePrice(double salePrice) {
        this.salePrice = salePrice;
    }
}
