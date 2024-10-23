package com.example.warehub;

public class Bill {
    private int id;
    private String customerName;
    private double totalAmount;
    private String pdfPath;
    private String billDate; // Added
    // Added

    public Bill(int id, String customerName, double totalAmount, String pdfPath, String billDate) {
        this.id = id;
        this.customerName = customerName;
        this.totalAmount = totalAmount;
        this.pdfPath = pdfPath;
        this.billDate = billDate; // Added
          // Added
    }

    public int getId() {
        return id;
    }

    public String getCustomerName() {
        return customerName;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public String getPdfPath() {
        return pdfPath;
    }

    public String getBillDate() { // Added
        return billDate;
    }


}
