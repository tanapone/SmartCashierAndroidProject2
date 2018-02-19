package com.example.tanapone.smartcashier;

import java.util.ArrayList;
import java.util.Vector;

/**
 * Created by Tanapone on 28/1/2561.
 */

public class Store {
    private String storeID;
    private String storeName;
    private String storePhoneNumber;
    private String storeEmail;
    private String storePassword;
    private String storeFB;

    private ArrayList<Order> orders = new ArrayList<Order>();
    private ArrayList<Product> products = new ArrayList<Product>();

    public String getStoreID() {
        return storeID;
    }

    public void setStoreID(String storeID) {
        this.storeID = storeID;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public String getStorePhoneNumber() {
        return storePhoneNumber;
    }

    public void setStorePhoneNumber(String storePhoneNumber) {
        this.storePhoneNumber = storePhoneNumber;
    }

    public String getStoreEmail() {
        return storeEmail;
    }

    public void setStoreEmail(String storeEmail) {
        this.storeEmail = storeEmail;
    }

    public String getStorePassword() {
        return storePassword;
    }

    public void setStorePassword(String storePassword) {
        this.storePassword = storePassword;
    }

    public String getStoreFB() {
        return storeFB;
    }

    public void setStoreFB(String storeFB) {
        this.storeFB = storeFB;
    }

    public ArrayList<Order> getOrders() {
        return orders;
    }

    public void setOrders(ArrayList<Order> orders) {
        this.orders = orders;
    }

    public ArrayList<Product> getProducts() {
        return products;
    }

    public void setProducts(ArrayList<Product> products) {
        this.products = products;
    }

    public void addProducts(Product product){
        this.products.add(product);
    }

    public void addOrders(Order order){
        this.orders.add(order);
    }

}
