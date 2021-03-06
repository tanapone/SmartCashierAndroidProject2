package com.example.tanapone.smartcashier.Models;

/**
 * Created by NongToey Laptop on 2/19/2018.
 */

public class Category {
    private String categoryID;
    private String categoryName;

    public Category(){

    }

    public Category(String categoryID, String categoryName) {
        this.categoryID = categoryID;
        this.categoryName = categoryName;
    }

    public String getCategoryID() {
        return categoryID;
    }

    public void setCategoryID(String categoryID) {
        this.categoryID = categoryID;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    @Override
    public String toString(){
        return categoryName;
    }
}
