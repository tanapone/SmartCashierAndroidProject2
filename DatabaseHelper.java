package com.example.tanapone.smartcashier;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by NongToey Laptop on 2/19/2018.
 */

public class DatabaseHelper extends SQLiteOpenHelper {
    private static String DB_NAME = "it411db";
    public DatabaseHelper(Context context) {
        super(context ,DB_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String createProductTableQuery = "CREATE TABLE IF NOT EXISTS Product(" +
                "productID NUMBER PRIMARY KEY ," +
                "productName TEXT ," +
                "productAmount NUMBER ," +
                "productPrice DOUBLE ," +
                "storeID TEXT , FOREIGN KEY(storeID) REFERENCE Store(storeID))";

        String createOrderTableQuery = "CREATE TABLE IF NOT EXISTS Order(" +
                "orderID NUMBER ," +
                "orderDate DATE ," +
                "orderProduct TEXT ," +
                "orderAmount NUMBER ," +
                "storeID TEXT , FOREIGN KEY(storeID) REFERENCE Store(storeID)," +
                "PRIMARY KEY(orderID,orderProduct))";

        String createStoreQuery = "CREATE TABLE IF NOT EXISTS Store(" +
                "storeID NUMBER PRIMARY KEY ," +
                "storeName TEXT ," +
                "storePhoneNumber TEXT ," +
                "storeEmail TEXT ," +
                "storePassword TEXT ," +
                "storeFB TEXT)";

        sqLiteDatabase.execSQL(createStoreQuery);
        sqLiteDatabase.execSQL(createProductTableQuery);
        sqLiteDatabase.execSQL(createOrderTableQuery);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
    
    }
}
