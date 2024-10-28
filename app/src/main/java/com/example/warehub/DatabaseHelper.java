package com.example.warehub;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "warehub.db";
    private static final int DATABASE_VERSION = 2;  // Updated version for bills
    private static final String TABLE_PRODUCTS = "products";
    private static final String TABLE_BILLS = "bills";

    // Product table columns
    private static final String COLUMN_PRODUCT_ID = "ID";
    private static final String COLUMN_PRODUCT_NAME = "PRODUCT_NAME";
    private static final String COLUMN_PRODUCT_CODE = "PRODUCT_CODE";
    private static final String COLUMN_QUANTITY = "QUANTITY";
    private static final String COLUMN_PRICE = "PRICE";

    // Bill table columns
    private static final String COLUMN_BILL_ID = "id";
    private static final String COLUMN_CUSTOMER_NAME = "customer_name";
    private static final String COLUMN_TOTAL_AMOUNT = "total_amount";
    private static final String COLUMN_PDF_PATH = "pdf_path";  // For storing the PDF file path
    private static final String COLUMN_BILL_DATE = "date";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create Products table
        db.execSQL("CREATE TABLE " + TABLE_PRODUCTS + " (" +
                COLUMN_PRODUCT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_PRODUCT_NAME + " TEXT, " +
                COLUMN_PRODUCT_CODE + " TEXT, " +
                COLUMN_QUANTITY + " INTEGER, " +
                COLUMN_PRICE + " REAL)");

        // Create bills table
        String CREATE_BILLS_TABLE = "CREATE TABLE " + TABLE_BILLS + "("
                + COLUMN_BILL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_CUSTOMER_NAME + " TEXT,"
                + COLUMN_TOTAL_AMOUNT + " REAL,"
                + COLUMN_PDF_PATH + " TEXT,"
                + COLUMN_BILL_DATE + " TEXT" + ")";
        db.execSQL(CREATE_BILLS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            // Create the Bills table when upgrading
            db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_BILLS + " (" +
                    COLUMN_BILL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_CUSTOMER_NAME + " TEXT, " +
                    COLUMN_TOTAL_AMOUNT + " REAL, " +
                    COLUMN_PDF_PATH + " TEXT)");
        }
    }

    // ================== Product-related operations ==================

    // Insert product data
    public boolean insertData(String productName, String productCode, int quantity, double price) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_PRODUCT_NAME, productName);
        contentValues.put(COLUMN_PRODUCT_CODE, productCode);
        contentValues.put(COLUMN_QUANTITY, quantity);
        contentValues.put(COLUMN_PRICE, price);
        long result = db.insert(TABLE_PRODUCTS, null, contentValues);
        return result != -1; // returns true if inserted successfully
    }

    // Get all products
    public ArrayList<Product> getAllProducts() {
        ArrayList<Product> productList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_PRODUCTS, null);

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_ID));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_NAME));
                String code = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_CODE));
                int quantity = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_QUANTITY));
                double price = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_PRICE));

                Product product = new Product(id, name, code, quantity, price);
                productList.add(product);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return productList;
    }

    // Search products based on user query
    public ArrayList<Product> searchProducts(String query) {
        ArrayList<Product> searchedProducts = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        // SQL query to search by both product name and product code
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_PRODUCTS + " WHERE " +
                        COLUMN_PRODUCT_NAME + " LIKE ? OR " + COLUMN_PRODUCT_CODE + " LIKE ?",
                new String[]{"%" + query + "%", "%" + query + "%"});  // Using the query for both name and code search

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_ID));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_NAME));
                String code = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_CODE));
                int quantity = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_QUANTITY));
                double price = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_PRICE));

                Product product = new Product(id, name, code, quantity, price);
                searchedProducts.add(product);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return searchedProducts;
    }



    // Update product
    public boolean updateProduct(Product product) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_PRODUCT_NAME, product.getProductName());
        contentValues.put(COLUMN_PRODUCT_CODE, product.getProductCode());
        contentValues.put(COLUMN_QUANTITY, product.getQuantity());
        contentValues.put(COLUMN_PRICE, product.getPrice());
        int result = db.update(TABLE_PRODUCTS, contentValues, COLUMN_PRODUCT_ID + " = ?", new String[]{String.valueOf(product.getId())});
        return result > 0; // returns true if updated successfully
    }

    // Delete product
    public int deleteProduct(int productId) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_PRODUCTS, COLUMN_PRODUCT_ID + " = ?", new String[]{String.valueOf(productId)});
    }

    public static String getTableName() {
        return  TABLE_PRODUCTS;
    }

    // Delete all data from the database
    // Delete all data from the products and bills tables
    public void deleteAllData() {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            db.beginTransaction();  // Begin transaction to ensure atomicity
            db.delete(TABLE_PRODUCTS, null, null);
            db.delete(TABLE_BILLS, null, null);
            db.setTransactionSuccessful();  // Mark transaction as successful
        } finally {
            db.endTransaction();  // End the transaction
        }
        db.close();
    }



    public Product getProductByName(String productName) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_PRODUCTS + " WHERE " + COLUMN_PRODUCT_NAME + " = ? AND " + COLUMN_QUANTITY + " > 0", new String[]{productName});

        if (cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_ID));
            String name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_NAME));
            String code = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_CODE));
            int quantity = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_QUANTITY));
            double price = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_PRICE));

            Product product = new Product(id, name, code, quantity, price);
            cursor.close();
            return product;
        }
        cursor.close();
        return null;
    }

    // Get all products with quantity greater than 0
    public ArrayList<Product> getAllProductsWithQuantityGreaterThanZero() {
        ArrayList<Product> productList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_PRODUCTS + " WHERE " + COLUMN_QUANTITY + " > 0", null);

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_ID));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_NAME));
                String code = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_CODE));
                int quantity = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_QUANTITY));
                double price = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_PRICE));

                productList.add(new Product(id, name, code, quantity, price));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return productList;
    }

    public boolean updateProductquantity(Product product) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("quantity", product.getQuantity());

        int result = db.update("products", contentValues, "id = ?", new String[]{String.valueOf(product.getId())});
        return result > 0;
    }

    // ================== Bill-related operations ==================

    // Insert a new bill
    // Insert a bill into the database
    public boolean insertBill(String customerName, double totalAmount, String pdfPath, String billDate) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_CUSTOMER_NAME, customerName);
        values.put(COLUMN_TOTAL_AMOUNT, totalAmount);
        values.put(COLUMN_PDF_PATH, pdfPath);
        values.put(COLUMN_BILL_DATE, billDate);


        long result = db.insert(TABLE_BILLS, null, values);
        return result != -1; // Returns true if insertion was successful
    }


    // Fetch all bills
    public ArrayList<Bill> getAllBills() {
        ArrayList<Bill> bills = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_BILLS, null);

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_BILL_ID));
                String customerName = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CUSTOMER_NAME));
                double totalAmount = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_TOTAL_AMOUNT));

                String pdfPath = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PDF_PATH));
                String billDate = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_BILL_DATE));

                bills.add(new Bill(id, customerName, totalAmount, pdfPath,billDate));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return bills;
    }

    // Delete a bill
    public boolean deleteBill(int billId) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_BILLS, COLUMN_BILL_ID + " = ?", new String[]{String.valueOf(billId)}) > 0;
    }

    // Method to get products with quantity less than 3
    // Method to get products with quantity less than 3
    public ArrayList<Product> getLowQuantityProducts() {
        ArrayList<Product> lowQuantityProducts = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_PRODUCTS + " WHERE " + COLUMN_QUANTITY + " < 3", null);

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_ID));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_NAME));
                String code = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRODUCT_CODE));
                int quantity = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_QUANTITY));
                double price = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_PRICE));

                Product product = new Product(id, name, code, quantity, price);
                lowQuantityProducts.add(product);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return lowQuantityProducts;
    }


}


