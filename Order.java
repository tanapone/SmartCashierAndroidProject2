package com.example.tanapone.smartcashier;

import java.util.ArrayList;
import java.util.Date;
import java.util.Vector;

/**
 * Created by Tanapone on 28/1/2561.
 */

public class Order {
    private String orderID;
    private Date orderDate;
    private ArrayList<Product> orderProduct = new ArrayList<Product>();
    private ArrayList<Integer> orderQuantity = new ArrayList<Integer>();

}