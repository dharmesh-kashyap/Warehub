package com.example.warehub;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

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

    // Get all products from the database
    public ArrayList<Product> getAllProducts() {
        ArrayList<Product> productList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(COL_1));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(COL_2));
                String code = cursor.getString(cursor.getColumnIndexOrThrow(COL_3));
                int quantity = cursor.getInt(cursor.getColumnIndexOrThrow(COL_4));
                double price = cursor.getDouble(cursor.getColumnIndexOrThrow(COL_5));

                Product product = new Product(id, name, code, quantity, price);
                productList.add(product);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return productList;
    }

    // Search products based on user query
    // Search products based on user query
    public ArrayList<Product> searchProducts(String query) {
        ArrayList<Product> searchedProducts = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        // SQL query to search by both product name and product code
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " +
                        COL_2 + " LIKE ? OR " + COL_3 + " LIKE ?",
                new String[]{"%" + query + "%", "%" + query + "%"});  // Using the query for both name and code search

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(COL_1));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(COL_2));
                String code = cursor.getString(cursor.getColumnIndexOrThrow(COL_3));
                int quantity = cursor.getInt(cursor.getColumnIndexOrThrow(COL_4));
                double price = cursor.getDouble(cursor.getColumnIndexOrThrow(COL_5));

                Product product = new Product(id, name, code, quantity, price);
                searchedProducts.add(product);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return searchedProducts;
    }




    // Update product in the database
    public boolean updateProduct(Product product) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_2, product.getProductName());
        contentValues.put(COL_3, product.getProductCode());
        contentValues.put(COL_4, product.getQuantity());
        contentValues.put(COL_5, product.getPrice());
        int result = db.update(TABLE_NAME, contentValues, COL_1 + " = ?", new String[]{String.valueOf(product.getId())});
        return result > 0; // returns true if updated successfully
    }

    // Delete product from the database
    public int deleteProduct(int productId) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NAME, COL_1 + " = ?", new String[]{String.valueOf(productId)});
    }
}
