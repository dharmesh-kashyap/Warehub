package com.example.warehub;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.layout.element.Image;

public class BillingPage extends Fragment {

    private AutoCompleteTextView customerNameInput;
    private TextView totalAmountTextView;
    private LinearLayout productListContainer;
    private Button generateBillButton, addProductButton;
    private DatabaseHelper databaseHelper;
    private double totalAmount = 0.0;
    private ArrayList<Product> productList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_billing_page, container, false);

        initializeViews(view);
        databaseHelper = new DatabaseHelper(getActivity());
        addProductRow();

        addProductButton.setOnClickListener(v -> addProductRow());
        generateBillButton.setOnClickListener(v -> generateBill());

        return view;
    }

    private void initializeViews(View view) {
        customerNameInput = view.findViewById(R.id.customer_name_input);
        totalAmountTextView = view.findViewById(R.id.total_amount_text_view);
        productListContainer = view.findViewById(R.id.product_list_container);
        generateBillButton = view.findViewById(R.id.generate_bill_button);
        addProductButton = view.findViewById(R.id.add_product_button);
    }

    private void addProductRow() {
        View productRow = LayoutInflater.from(getActivity()).inflate(R.layout.product_row, productListContainer, false);

        Spinner productNameInput = productRow.findViewById(R.id.product_name_input); // Changed to Spinner
        Spinner quantityInput = productRow.findViewById(R.id.quantity_input);
        TextView priceInput = productRow.findViewById(R.id.price_input);
        Button removeProductButton = productRow.findViewById(R.id.remove_product_button);

        removeProductButton.setOnClickListener(v -> productListContainer.removeView(productRow));

        setupProductSpinner(productNameInput, quantityInput, priceInput);
        productListContainer.addView(productRow);
    }

    private void setupProductSpinner(Spinner productNameInput, Spinner quantityInput, TextView priceInput) {
        ArrayList<Product> availableProducts = databaseHelper.getAllProductsWithQuantityGreaterThanZero();
        ArrayList<String> productNames = getProductNames(availableProducts);

        // Add a "Select Product" hint at the start of the list
        productNames.add(0, "Product");

        ArrayAdapter<String> productAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, productNames);
        productAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        productNameInput.setAdapter(productAdapter);

        // Prevent selection of the hint item
        productNameInput.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    // Do nothing if the first item (hint) is selected
                    return;
                }

                String selectedProductName = (String) parent.getItemAtPosition(position);
                Product selectedProduct = getProductByName(selectedProductName, availableProducts);

                if (selectedProduct != null) {
                    priceInput.setText(String.valueOf(selectedProduct.getPrice()));
                    setupQuantitySpinner(quantityInput, selectedProduct.getQuantity());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });
    }



    private void setupQuantitySpinner(Spinner quantityInput, int maxQuantity) {
        ArrayList<Integer> quantities = new ArrayList<>();
        for (int i = 1; i <= maxQuantity; i++) {
            quantities.add(i);
        }
        ArrayAdapter<Integer> quantityAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, quantities);
        quantityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        quantityInput.setAdapter(quantityAdapter);
    }

    private ArrayList<String> getProductNames(ArrayList<Product> products) {
        ArrayList<String> names = new ArrayList<>();
        for (Product product : products) {
            names.add(product.getProductName());
        }
        return names;
    }

    private Product getProductByName(String productName, ArrayList<Product> products) {
        for (Product product : products) {
            if (product.getProductName().equals(productName)) {
                return product;
            }
        }
        return null;
    }

    private void generateBill() {
        String customerName = customerNameInput.getText().toString().trim();
        if (customerName.isEmpty()) {
            Toast.makeText(getActivity(), "Please enter a customer name", Toast.LENGTH_SHORT).show();
            return;
        }

        totalAmount = 0.0;
        productList.clear();

        for (int i = 0; i < productListContainer.getChildCount(); i++) {
            View productRow = productListContainer.getChildAt(i);
            Spinner productNameInput = productRow.findViewById(R.id.product_name_input);
            Spinner quantityInput = productRow.findViewById(R.id.quantity_input);
            TextView priceInput = productRow.findViewById(R.id.price_input);

            // Check if the user selected the hint (index 0)
            if (productNameInput.getSelectedItemPosition() == 0) {
                Toast.makeText(getActivity(), "Please select a product", Toast.LENGTH_SHORT).show();
                return;
            }

            String productName = (String) productNameInput.getSelectedItem();
            int selectedQuantity = (int) quantityInput.getSelectedItem();
            String priceText = priceInput.getText().toString().trim();

            if (priceText.isEmpty()) {
                Toast.makeText(getActivity(), "Price is not available", Toast.LENGTH_SHORT).show();
                return;
            }

            double price = Double.parseDouble(priceText);
            totalAmount += selectedQuantity * price;
            com.example.warehub.Product selectedProduct = databaseHelper.getProductByName(productName);

            // Deduct selected quantity from database
            if (selectedProduct != null) {
                int newQuantity = selectedProduct.getQuantity() - selectedQuantity;
                selectedProduct.setQuantity(newQuantity);
                databaseHelper.updateProductquantity(selectedProduct);
            }

            productList.add(new Product(productName, selectedQuantity, price));
        }

        totalAmountTextView.setText("Total Amount: " + totalAmount);
        saveBillToDatabase(customerName);
    }



    private void saveBillToDatabase(String customerName) {
        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        String pdfName = "Bill_" + customerName + "_" + currentDate + ".pdf";
        String pdfPath = getActivity().getExternalFilesDir(null) + "/" + pdfName;

        if (databaseHelper.insertBill(customerName, totalAmount, pdfPath, currentDate)) {
            Toast.makeText(getActivity(), "Bill generated and saved!", Toast.LENGTH_SHORT).show();
            createPDF(customerName, productList, totalAmount, pdfPath);
        } else {
            Toast.makeText(getActivity(), "Error saving bill", Toast.LENGTH_SHORT).show();
        }
    }

    private void createPDF(String customerName, ArrayList<Product> products, double totalAmount, String pdfPath) {
        try {
            PdfWriter writer = new PdfWriter(pdfPath);
            PdfDocument pdfDocument = new PdfDocument(writer);
            Document document = new Document(pdfDocument);

            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.thisisfinal);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            Image pdfImage = new Image(ImageDataFactory.create(stream.toByteArray()));
            pdfImage.setWidth(200);
            document.add(pdfImage);

            document.add(new Paragraph("Bill for: " + customerName));
            document.add(new Paragraph("Total Amount: " + totalAmount));
            document.add(new Paragraph("Products:"));

            Table table = new Table(3);
            table.addHeaderCell("Product Name");
            table.addHeaderCell("Quantity");
            table.addHeaderCell("Price");

            for (Product product : products) {
                table.addCell(product.getProductName());
                table.addCell(String.valueOf(product.getQuantity()));
                table.addCell(String.valueOf(product.getPrice()));
            }

            document.add(table);
            document.close();
            openGeneratedPDF(pdfPath);

            resetPage();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void openGeneratedPDF(String pdfPath) {
        File pdfFile = new File(pdfPath);
        Uri pdfUri = FileProvider.getUriForFile(getActivity(), getActivity().getPackageName() + ".fileprovider", pdfFile);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(pdfUri, "application/pdf");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(intent);
    }

    private void resetPage() {
        // Clear customer name input
        customerNameInput.setText("");

        // Reset total amount display
        totalAmountTextView.setText("Total Amount: 0.0");

        // Remove all product rows
        productListContainer.removeAllViews();

        // Optionally, add a new product row
        addProductRow();
    }

//    public static class Product {
//        private String name;
//        private int quantity;
//        private double price;
//
//        public Product(String name, int quantity, double price) {
//            this.name = name;
//            this.quantity = quantity;
//            this.price = price;
//        }
//
//        public String getName() {
//            return name;
//        }
//
//        public int getQuantity() {
//            return quantity;
//        }
//
//        public void setQuantity(int quantity) {
//            this.quantity = quantity;
//        }
//
//        public double getPrice() {
//            return price;
//        }
//    }
}
