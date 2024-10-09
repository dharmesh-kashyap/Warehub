package com.example.warehub;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "inventory.db";
    private static final String TABLE_NAME = "products";
    private static final String COL_1 = "ID";
    private static final String COL_2 = "PRODUCT_NAME";
    private static final String COL_3 = "PRODUCT_CODE";
    private static final String COL_4 = "QUANTITY";
    private static final String COL_5 = "PRICE";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, PRODUCT_NAME TEXT, PRODUCT_CODE TEXT, QUANTITY INTEGER, PRICE REAL)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    // Insert product data into the database
    public boolean insertData(String productName, String productCode, int quantity, double price) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_2, productName);
        contentValues.put(COL_3, productCode);
        contentValues.put(COL_4, quantity);
        contentValues.put(COL_5, price);
        long result = db.insert(TABLE_NAME, null, contentValues);
        return result != -1; // returns true if inserted successfully
    }
}
