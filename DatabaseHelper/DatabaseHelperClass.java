package com.example.tanapone.smartcashier.DatabaseHelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.example.tanapone.smartcashier.Models.Category;
import com.example.tanapone.smartcashier.Models.Product;
import com.example.tanapone.smartcashier.Models.Store;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by NongToey Laptop on 2/19/2018.
 */

public class DatabaseHelperClass extends SQLiteOpenHelper {
    private static String DB_NAME = "SmartCashier";
    public DatabaseHelperClass(Context context) {

        super(context ,DB_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createProductTableQuery = "CREATE TABLE IF NOT EXISTS Products(" +
                "productID INTEGER  PRIMARY KEY AUTOINCREMENT," +
                "productBarcode TEXT UNIQUE," +
                "productName TEXT ," +
                "productQTY INTEGER  ," +
                "categoryID INTEGER ," +
                "capitalPrice DOUBLE ," +
                "salePrice DOUBLE," +
                "minimumQTY INTEGER," +
                "FOREIGN KEY(categoryID) REFERENCES Categories(categoryID) ON DELETE CASCADE)";

        String createCategory = "CREATE TABLE IF NOT EXISTS Categories(" +
                "categoryID INTEGER  PRIMARY KEY AUTOINCREMENT," +
                "categoryName TEXT)";

        String createOrderTableQuery = "CREATE TABLE IF NOT EXISTS Orders(" +
                "orderID INTEGER ," +
                "orderDate DATE ," +
                "orderProduct TEXT ," +
                "orderQTY INTEGER ," +
                "PRIMARY KEY(orderID,orderProduct)," +
                "FOREIGN KEY(orderProduct) REFERENCES Products(productID)ON DELETE CASCADE)";

        String createStoreQuery = "CREATE TABLE IF NOT EXISTS Store(" +
                "storeID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "storeName TEXT ," +
                "storePhoneNumber TEXT ," +
                "storeEmail TEXT ," +
                "storeFB TEXT)";

        db.execSQL(createStoreQuery);
        db.execSQL(createCategory);
        db.execSQL(createProductTableQuery);
        db.execSQL(createOrderTableQuery);
        System.out.println("Created database");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("Drop Table if exists Store");
        db.execSQL("Drop Table if exists Categories");
        db.execSQL("Drop Table if exists Products");
        db.execSQL("Drop Table if exists Orders");
    }


    public boolean addStore(Store store) {
        long result = -1;
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("storeName", store.getStoreName());
            values.put("storeEmail", store.getStoreEmail());
            values.put("storePhoneNumber", store.getStorePhoneNumber());
            values.put("storeFB", store.getStoreFB());
// Inserting Row
            result = db.insert("Store", null, values);
            db.close(); // Closing database connection
        }catch (Exception ex) {
            ex.printStackTrace();
        }
        if (result == -1) {
            return false;
        }else{
            return true;
        }
    }

    public boolean addCategory(Category category){
        long result = -1;
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("categoryName",category.getCategoryName());
            result = db.insert("Categories", null, values);
            db.close(); // Closing database connection
        }catch (Exception ex) {
            ex.printStackTrace();
        }
        if (result == -1) {
            return false;
        }else{
            return true;
        }
    }

    public ArrayList<Category> getCategories(){
        ArrayList<Category> categories = new ArrayList<Category>();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT categoryID,categoryName FROM Categories",null);
        if (cursor.getCount()!=0) {

            while(cursor.moveToNext()){
                Category category = new Category();
                category.setCategoryID(cursor.getString(0));
                category.setCategoryName(cursor.getString(1));
                categories.add(category);
            }

        }
        return categories;
    }

    public Category getCategoryByID(int id){
        Category category = new Category();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT categoryID,categoryName FROM Categories WHERE categoryID="+id+"",null);
        if(cursor.moveToFirst() && cursor.getCount() >= 1){
            category.setCategoryID(cursor.getString(0));
            category.setCategoryName(cursor.getString(1));
        }
        return category;
    }

    public boolean deleteCategory(Category category){
        long result = -1;
        SQLiteDatabase db = this.getWritableDatabase();

        result = db.delete("Categories","categoryID = "+category.getCategoryID(),null);
        result = db.delete("Products","categoryID = "+category.getCategoryID(),null);
        for(Product product : getProductByCategory(category)) {
            result = db.delete("Orders", "orderProduct ="+product.getProductID(), null);
        }
        db.close();
        if(result ==-1){
            return false;
        }else{
            return true;
        }
    }



    public boolean addProduct(Product product){
        long result = -1;
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("productBarcode",product.getProductBarcodeID());
        values.put("productName",product.getProductName());
        values.put("productQTY",product.getProductQuantity());
        values.put("categoryID",product.getCategory().getCategoryID());
        values.put("capitalPrice",product.getCapitalPrice());
        values.put("salePrice",product.getSalePrice());
        values.put("minimumQTY",product.getMinimum());
        result = db.insert("Products", null, values);
        db.close(); // Closing database connection
        if (result == -1) {
            return false;
        }else{
            return true;
        }
    }

    public boolean updateProduct(Product product){
        long result = -1;
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("productID",product.getProductID());
        values.put("productBarcode",product.getProductBarcodeID());
        values.put("productName",product.getProductName());
        values.put("productQTY",product.getProductQuantity());
        values.put("categoryID",product.getCategory().getCategoryID());
        values.put("capitalPrice",product.getCapitalPrice());
        values.put("salePrice",product.getSalePrice());
        values.put("minimumQTY",product.getMinimum());
        result = db.update("Products",values,"productID = ? ",new String[] {product.getProductID()});
        db.close(); // Closing database connection
        if(result == -1){
            return false;
        }else{
            return true;
        }
    }

    public ArrayList<Product> getProductByCategory(Category category){
        ArrayList<Product> products = new  ArrayList<Product>();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM Products",null);
        if (cursor.getCount()!=0) {

            while(cursor.moveToNext()){
                Product product = new Product();
                product.setProductID(cursor.getString(0));
                product.setProductBarcodeID(cursor.getString(1));
                product.setProductName(cursor.getString(2));
                product.setProductQuantity(cursor.getInt(3));
                product.setCategory(category);
                product.setCapitalPrice(cursor.getDouble(5));
                product.setSalePrice(cursor.getDouble(6));
                product.setMinimum(cursor.getInt(7));
                products.add(product);
            }

        }
        return products;
    }


    public Store getStore() {
        Store stu = new Store();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT storeName,storePhoneNumber,storeEmail,storeFB FROM Store",null);
        if (cursor.getCount()!=0) {
            cursor.moveToFirst();
            stu = new Store(cursor.getString(0),cursor.getString(1),cursor.getString(2),cursor.getString(3));
        }
        db.close();
        return stu;
    }

    public boolean foundData(String table){
        boolean found = false;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM "+table,null);
        if(cursor.getCount()!=0){
            cursor.moveToFirst();
            found = true;
        }
        return found;
    }
    public List<Product> getProducts() {
        List<Product> lists = new ArrayList<Product>();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("Select productID,productBarcode,productName,productQTY,capitalPrice,salePrice,minimumQTY,categoryID  from Products", null);
        if (cursor.moveToFirst() && cursor.getCount() >= 1) {
            do {
                Product product = new Product(cursor.getString(0),cursor.getString(1), cursor.getString(2),
                        Integer.parseInt(cursor.getString(3)),Double.parseDouble(cursor.getString(4)),Double.parseDouble(cursor.getString(5)));
                product.setMinimum(cursor.getInt(6));
                product.setCategory(this.getCategoryByID(cursor.getInt(7)));
                lists.add(product);
            }while (cursor.moveToNext());
        }
        db.close();
        return lists;
    }


    public List<Product> searchProduct(String search) {
        List<Product> lists = new ArrayList<Product>();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("Select productID,productBarcode,productName,productQTY,capitalPrice,salePrice,minimumQTY,categoryID  from Products WHERE productName LIKE '%"+search+"%' OR productBarcode ='"+search+"'", null);
        if (cursor.moveToFirst() && cursor.getCount() >= 1) {
            do {
                Product product = new Product(cursor.getString(0),cursor.getString(1), cursor.getString(2),
                        Integer.parseInt(cursor.getString(3)),Double.parseDouble(cursor.getString(4)),Double.parseDouble(cursor.getString(5)));
                product.setMinimum(cursor.getInt(6));
                product.setCategory(this.getCategoryByID(cursor.getInt(7)));
                lists.add(product);
            }while (cursor.moveToNext());
        }
        db.close();
        return lists;
    }

    public Product searchProductByBarcode(String barcode) {
        Product product = new Product();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT productID,productBarcode,productName,productQTY,capitalPrice,salePrice,minimumQTY,categoryID  FROM Products WHERE productBarcode LIKE '%"+barcode+"%'", null);
        if (cursor.moveToFirst() && cursor.getCount() >= 1) {
            do {
                Product productSearched = new Product(cursor.getString(0),cursor.getString(1), cursor.getString(2),
                        Integer.parseInt(cursor.getString(3)),Double.parseDouble(cursor.getString(4)),Double.parseDouble(cursor.getString(5)));
                productSearched.setMinimum(cursor.getInt(6));
                productSearched.setCategory(this.getCategoryByID(cursor.getInt(7)));
                product = productSearched;
            }while (cursor.moveToNext());
        }
        db.close();
        return product;
    }

    public boolean deleteProduct(Product product) {
        long result = -1;
        try {
            SQLiteDatabase db = this.getWritableDatabase();
// Deleting Row
            result = db.delete("Products", "productID = ?", new String[] {product.getProductID()});
            db.close(); // Closing database connection
        }catch (Exception ex) {
            ex.printStackTrace();
        }
        if (result == -1) {
            return false;
        }else{
            return true;
        }
    }

}
