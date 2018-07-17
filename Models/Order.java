package com.example.tanapone.smartcashier.Models;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Tanapone on 28/1/2561.
 */

public class Order {
    private String orderID;
    private Date orderDate;
    private ArrayList<Product> orderProduct = new ArrayList<Product>();

    public String getOrderID() {
        return orderID;
    }

    public void setOrderID(String orderID) {
        this.orderID = orderID;
    }

    public Date getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }

    public ArrayList<Product> getOrderProduct() {
        return orderProduct;
    }

    public void setOrderProduct(ArrayList<Product> orderProduct) {
        this.orderProduct = orderProduct;
    }

}
