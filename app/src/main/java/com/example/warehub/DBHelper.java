package com.example.warehub;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class DBHelper extends SQLiteOpenHelper {
    public static final String DBNAME = "Login.db";

    public DBHelper(Context context) {
        super(context, "Login.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase MyDB) {
        MyDB.execSQL("CREATE TABLE users(username TEXT PRIMARY KEY, password TEXT, email TEXT, fullname TEXT, companyname TEXT, phone TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase MyDB, int i, int i1) {
        MyDB.execSQL("DROP TABLE IF EXISTS users");
    }

    // Insert data into the database
    public Boolean insertData(String username, String password, String email, String fullname, String companyname, String phone) {
        SQLiteDatabase MyDB = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("username", username);
        contentValues.put("password", password);
        contentValues.put("email", email);
        contentValues.put("fullname", fullname);
        contentValues.put("companyname", companyname);
        contentValues.put("phone", phone);
        long result = MyDB.insert("users", null, contentValues);
        return result != -1;
    }

    // Check if the username exists
    public Boolean checkusername(String username) {
        SQLiteDatabase MyDB = this.getWritableDatabase();
        Cursor cursor = MyDB.rawQuery("SELECT * FROM users WHERE username = ?", new String[]{username});
        return cursor.getCount() > 0;
    }

    // Check if the username and password match
    public Boolean checkUsernameAndPassword(String username, String password) {
        SQLiteDatabase MyDB = this.getWritableDatabase();
        Cursor cursor = MyDB.rawQuery("SELECT * FROM users WHERE username = ? AND password = ?", new String[]{username, password});
        return cursor.getCount() > 0;
    }

    // Update the password for the given username
    public Boolean updatePassword(String username, String newPassword) {
        SQLiteDatabase MyDB = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("password", newPassword);
        long result = MyDB.update("users", contentValues, "username = ?", new String[]{username});
        return result != -1;
    }

    // Retrieve user data by username
    public User getUserByUsername(String username) {
        SQLiteDatabase MyDB = this.getReadableDatabase();
        Cursor cursor = MyDB.rawQuery("SELECT * FROM users WHERE username = ?", new String[]{username});
        if (cursor != null && cursor.moveToFirst()) {
            String fullname = cursor.getString(cursor.getColumnIndexOrThrow("fullname"));
            String email = cursor.getString(cursor.getColumnIndexOrThrow("email"));
            String companyname = cursor.getString(cursor.getColumnIndexOrThrow("companyname"));
            String phone = cursor.getString(cursor.getColumnIndexOrThrow("phone"));
            String password = cursor.getString(cursor.getColumnIndexOrThrow("password"));
            cursor.close();
            return new User(username, fullname, email, companyname, phone, password);  // Assuming you have a User model
        }
        return null;
    }

    // Update user profile information
    public Boolean updateUser(String username, String fullname, String email, String companyname, String phone, String password) {
        SQLiteDatabase MyDB = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("fullname", fullname);
        contentValues.put("email", email);
        contentValues.put("companyname", companyname);
        contentValues.put("phone", phone);
        contentValues.put("password", password);
        long result = MyDB.update("users", contentValues, "username = ?", new String[]{username});
        return result != -1;
    }

    // Update the password for the given username
    public Boolean updatepassword(String username, String newPassword) {
        SQLiteDatabase MyDB = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("password", newPassword);
        long result = MyDB.update("users", contentValues, "username = ?", new String[]{username});
        return result != -1;
    }
    // Add these methods to your DBHelper.java file
    public ArrayList<Product> getAllProducts() {
        ArrayList<Product> products = new ArrayList<>();
        SQLiteDatabase MyDB = this.getReadableDatabase();
        Cursor cursor = MyDB.rawQuery("SELECT * FROM products", null);

        if (cursor.moveToFirst()) {
            do {
                String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                int quantity = cursor.getInt(cursor.getColumnIndexOrThrow("quantity"));
                double price = cursor.getDouble(cursor.getColumnIndexOrThrow("price"));
                products.add(new Product(name, quantity, price));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return products;
    }

    public double getTotalValuation() {
        double totalValuation = 0.0;
        SQLiteDatabase MyDB = this.getReadableDatabase();
        Cursor cursor = MyDB.rawQuery("SELECT quantity, price FROM products", null);

        if (cursor.moveToFirst()) {
            do {
                int quantity = cursor.getInt(0);
                double price = cursor.getDouble(1);
                totalValuation += quantity * price;
            } while (cursor.moveToNext());
        }
        cursor.close();
        return totalValuation;
    }

    public ArrayList<Product> getLowQuantityProducts(int threshold) {
        ArrayList<Product> lowQuantityProducts = new ArrayList<>();
        SQLiteDatabase MyDB = this.getReadableDatabase();
        Cursor cursor = MyDB.rawQuery("SELECT * FROM products WHERE quantity < ?", new String[]{String.valueOf(threshold)});

        if (cursor.moveToFirst()) {
            do {
                String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                int quantity = cursor.getInt(cursor.getColumnIndexOrThrow("quantity"));
                double price = cursor.getDouble(cursor.getColumnIndexOrThrow("price"));
                lowQuantityProducts.add(new Product(name, quantity, price));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return lowQuantityProducts;
    }

}




