package com.example.warehub;

public class Product {
    private int id;
    private String productName;
    private String productCode;
    private int quantity;
    private double price;

    public Product(int id, String productName, String productCode, int quantity, double price) {
        this.id = id;
        this.productName = productName;
        this.productCode = productCode;
        this.quantity = quantity;
        this.price = price;
    }

    public int getId() {
        return id;
    }

    public String getProductName() {
        return productName;
    }

    public String getProductCode() {
        return productCode;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
