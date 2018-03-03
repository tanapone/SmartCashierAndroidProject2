package com.example.tanapone.smartcashier.Models;

import java.util.ArrayList;

/**
 * Created by Tanapone on 28/1/2561.
 */

public class Store {
    private String storeName;
    private String storePhoneNumber;
    private String storeEmail;
    private String storeFB;


    public Store(String storeName, String storePhoneNumber, String storeEmail, String storeFB) {
        this.storeName = storeName;
        this.storePhoneNumber = storePhoneNumber;
        this.storeEmail = storeEmail;
        this.storeFB = storeFB;
    }

    public Store(){}

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public String getStorePhoneNumber() {
        return storePhoneNumber;
    }

    public void setStorePhoneNumber(String storePhoneNumber) {this.storePhoneNumber = storePhoneNumber;}

    public String getStoreEmail() {
        return storeEmail;
    }

    public void setStoreEmail(String storeEmail) {
        this.storeEmail = storeEmail;
    }

    public String getStoreFB() {
        return storeFB;
    }

    public void setStoreFB(String storeFB) {
        this.storeFB = storeFB;
    }

    @Override
    public String toString() {
        return "Store{" +
                "storeName='" + storeName + '\'' +
                ", storePhoneNumber='" + storePhoneNumber + '\'' +
                ", storeEmail='" + storeEmail + '\'' +
                ", storeFB='" + storeFB + '\'' +
                '}';
    }
}
