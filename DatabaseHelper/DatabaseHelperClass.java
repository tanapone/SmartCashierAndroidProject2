package com.example.tanapone.smartcashier.DatabaseHelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.tanapone.smartcashier.Models.Category;
import com.example.tanapone.smartcashier.Models.Order;
import com.example.tanapone.smartcashier.Models.Product;
import com.example.tanapone.smartcashier.Models.Store;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
        db.close();
        if(result == -1){
            return false;
        }else{
            return true;
        }

    }
    public boolean updateProductQTY(String productID,String qty){
        long result = -1;
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("productQTY",qty);
        result = db.update("Products",values,"productID = "+productID,null);
        db.close();
        if(result == -1){
            return false;
        }else{
            return true;
        }

    }
    public ArrayList<Product> getProductByCategory(Category category){
        ArrayList<Product> products = new  ArrayList<Product>();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM Products WHERE categoryID = "+category.getCategoryID(),null);
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
        Cursor cursor = db.rawQuery("SELECT storeID,storeName,storePhoneNumber,storeEmail,storeFB FROM Store",null);
        if (cursor.getCount()!=0) {
            cursor.moveToFirst();
            stu = new Store(cursor.getInt(0),cursor.getString(1),cursor.getString(2),cursor.getString(3),cursor.getString(4));
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

    public List<Product> getProductsMinimum() {
        List<Product> lists = new ArrayList<Product>();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("Select productID,productBarcode,productName,productQTY,capitalPrice,salePrice,minimumQTY,categoryID  from Products where productQTY<=minimumQTY", null);
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

    public Product getProductByID(String productID){
        Product product = new Product();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("Select productID,productBarcode,productName,productQTY,capitalPrice,salePrice,minimumQTY,categoryID  from Products WHERE productID = "+productID, null);
        if (cursor.moveToFirst() && cursor.getCount() >= 1) {
                product = new Product(cursor.getString(0),cursor.getString(1), cursor.getString(2),
                        Integer.parseInt(cursor.getString(3)),Double.parseDouble(cursor.getString(4)),Double.parseDouble(cursor.getString(5)));
                product.setMinimum(cursor.getInt(6));
                product.setCategory(this.getCategoryByID(cursor.getInt(7)));
        }
        db.close();
        return product;
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

    public boolean addOrder(Order order){
        long result = -1;
        try{
            SQLiteDatabase db = this.getWritableDatabase();
            for(int i =0;i<order.getOrderProduct().size();i++) {
                ContentValues values = new ContentValues();
                values.put("orderID", order.getOrderID());
                DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                Date date = order.getOrderDate();
                values.put("orderDate", dateFormat.format(date));
                values.put("orderProduct",String.valueOf(order.getOrderProduct().get(i).getProductID()));
                values.put("orderQTY",String.valueOf(order.getOrderProduct().get(i).getProductQuantity()));
                result = db.insert("Orders", null, values);
            }
            for(int i =0;i<order.getOrderProduct().size();i++){
                Product product = new Product();
                product = getProductByID(order.getOrderProduct().get(i).getProductID());
                int newQty = product.getProductQuantity() - order.getOrderProduct().get(i).getProductQuantity();
                product.setProductQuantity(newQty);
                updateProduct(product);
                System.out.println("Inserted : "+i);
            }

            db.close();
        }catch (Exception ex) {
            ex.printStackTrace();
        }
        if(result==-1){
            return false;
        }else{
            return true;
        }
    }

    public String getLastOrderID(){
        String lastID = "";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT MAX(orderID) FROM ORDERS",null);
        if (cursor.moveToFirst() && cursor.getCount() >= 1) {
            do {
                lastID = cursor.getString(0);
            }while (cursor.moveToNext());
        }
        db.close();
        return lastID;
    }

    public boolean retriveProduct(Product product){
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
        result = db.insert("Products", null, values);
        db.close(); // Closing database connection
        if (result == -1) {
            return false;
        }else{
            return true;
        }
    }


    public boolean retriveCategory(Category category){
        long result = -1;
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("categoryID",category.getCategoryID());
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

    public boolean updateStore(Store store) {
        long result = -1;
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("storeID", store.getStoreID());
            values.put("storeName", store.getStoreName());
            values.put("storePhoneNumber", store.getStorePhoneNumber());
            values.put("storeEmail", store.getStoreEmail());
            values.put("storeFB", store.getStoreFB());
    // Updating Row
            result = db.update("Store", values, "storeID = ?",
                    new String[] {store.getStoreID()+""});
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

    public void deleteTable(){
            SQLiteDatabase db = this.getWritableDatabase();
            db.execSQL("DELETE FROM Orders");
            db.execSQL("DELETE FROM Categories");
             db.execSQL("DELETE FROM Products");
            db.close();
    }

    public ArrayList<Product> findProfit(){
        ArrayList<Product> products = new ArrayList<Product>();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT orderProduct , SUM(orderQTY) FROM Orders GROUP BY orderProduct", null);
        if (cursor.moveToFirst() && cursor.getCount() >= 1) {
            do {
                System.out.println("OUTPUT : "+cursor.getString(0)+","+cursor.getString(1));
                Product product = new Product();
                product = getProductByID(cursor.getString(0));
                product.setProductQuantity(cursor.getInt(1));
                products.add(product);
            }while (cursor.moveToNext());
        }
        db.close();
        return products;
    }
}
