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
    private TextView totalAmountTextView,totalItemsTextView;
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

        addProductRow(); // Add the first product row by default.

        addProductButton.setOnClickListener(v -> {
            addProductRow();
            //Toast.makeText(getActivity(), "Select a product before choosing quantity", Toast.LENGTH_SHORT).show();
        });

        generateBillButton.setOnClickListener(v -> generateBill());


        Button btnGenerateBill = getActivity().findViewById(R.id.btn_generate_bill);
        Button btnViewAllBills = getActivity().findViewById(R.id.btn_view_all_bills);

        if (btnGenerateBill != null) btnGenerateBill.setVisibility(View.GONE);
        if (btnViewAllBills != null) btnViewAllBills.setVisibility(View.GONE);

        return view;
    }

    private void initializeViews(View view) {
        customerNameInput = view.findViewById(R.id.customer_name_input);
        totalItemsTextView = view.findViewById(R.id.total_items_text_view);
        totalAmountTextView = view.findViewById(R.id.total_amount_text_view);
        productListContainer = view.findViewById(R.id.product_list_container);
        generateBillButton = view.findViewById(R.id.generate_bill_button);
        addProductButton = view.findViewById(R.id.add_product_button);
    }

    private void addProductRow() {
        View productRow = LayoutInflater.from(getActivity()).inflate(R.layout.product_row, productListContainer, false);

        Spinner productNameInput = productRow.findViewById(R.id.product_name_input);
        Spinner quantityInput = productRow.findViewById(R.id.quantity_input);
        Button removeProductButton = productRow.findViewById(R.id.remove_product_button);

        // Disable quantity selection initially
        quantityInput.setEnabled(false);

        removeProductButton.setOnClickListener(v -> {
            productListContainer.removeView(productRow);
            updateTotals(); // Recalculate totals when a product row is removed.
        });

        setupProductSpinner(productNameInput, quantityInput); // Set up product selection
        setupQuantitySpinnerWithValidation(productNameInput, quantityInput); // Set up quantity with validation
        productListContainer.addView(productRow);
        updateTotals(); // Update totals after adding a new product row.
    }

    private void setupProductSpinner(Spinner productNameInput, Spinner quantityInput) {
        ArrayList<Product> availableProducts = databaseHelper.getAllProductsWithQuantityGreaterThanZero();
        ArrayList<String> productNames = getProductNames(availableProducts);
        productNames.add(0, "Select Product");

        ArrayAdapter<String> productAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, productNames);
        productAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        productNameInput.setAdapter(productAdapter);

        productNameInput.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    quantityInput.setEnabled(false); // Disable quantity if no product is selected
                    initializeQuantitySpinner(quantityInput, 0); // Reset quantity options
                } else {
                    quantityInput.setEnabled(true); // Enable quantity when a product is selected
                    Product selectedProduct = availableProducts.get(position - 1);
                    initializeQuantitySpinner(quantityInput, selectedProduct.getQuantity());
                }
                updateTotals();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void setupQuantitySpinnerWithValidation(Spinner productNameInput, Spinner quantityInput) {
        quantityInput.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0 && productNameInput.getSelectedItemPosition() == 0) {
                    Toast.makeText(getActivity(), "Please select a product first", Toast.LENGTH_SHORT).show();
                    quantityInput.setSelection(0); // Reset to "Pick Qty"
                } else if (position > 0) {
                    updateTotals();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }


    private ArrayList<String> getProductNames(ArrayList<Product> products) {
        ArrayList<String> productNames = new ArrayList<>();
        for (Product product : products) {
            productNames.add(product.getProductName());
        }
        return productNames;
    }

    private void initializeQuantitySpinner(Spinner quantityInput, int maxQuantity) {
        ArrayList<String> quantities = new ArrayList<>();
        quantities.add("Pick Qty"); // Default option

        for (int i = 1; i <= maxQuantity; i++) {
            quantities.add(String.valueOf(i));
        }

        ArrayAdapter<String> quantityAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, quantities);
        quantityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        quantityInput.setAdapter(quantityAdapter);

        // Set up the quantity selection listener to check product selection first
        quantityInput.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Get the parent row to access the product spinner
                View parentRow = (View) parent.getParent();
                Spinner productNameInput = parentRow.findViewById(R.id.product_name_input);

                // Show toast if quantity is selected before product
                if (position > 0 && productNameInput.getSelectedItemPosition() == 0) {
                    Toast.makeText(getActivity(), "Select a product first", Toast.LENGTH_SHORT).show();
                    quantityInput.setSelection(0); // Reset to "Pick Qty" if no product is selected
                } else if (position > 0 && productNameInput.getSelectedItemPosition() > 0) {
                    // Only update totals if both product and quantity are selected
                    updateTotals();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }




    private void updateTotals() {
        int totalItems = 0;
        double totalAmount = 0.0;

        int rowCount = productListContainer.getChildCount();
        for (int i = 0; i < rowCount; i++) {
            View productRow = productListContainer.getChildAt(i);
            Spinner quantityInput = productRow.findViewById(R.id.quantity_input);
            Spinner productNameInput = productRow.findViewById(R.id.product_name_input);

            if (quantityInput.getSelectedItemPosition() > 0 && productNameInput.getSelectedItemPosition() > 0) {
                String productName = (String) productNameInput.getSelectedItem();
                int quantity = Integer.parseInt((String) quantityInput.getSelectedItem());

                Product product = databaseHelper.getProductByName(productName);
                totalAmount += quantity * product.getPrice();
                totalItems += quantity;
            }
        }

        totalItemsTextView.setText("Total Items: " + totalItems);
        totalAmountTextView.setText("Total Amount: " + totalAmount);
    }

    private void generateBill() {
        String customerName = customerNameInput.getText().toString().trim();
        if (customerName.isEmpty()) {
            Toast.makeText(getActivity(), "Please enter a customer name", Toast.LENGTH_SHORT).show();
            return;
        }

        totalAmount = 0.0;
        productList.clear();

        // Validate each product row to ensure all dropdowns are selected
        for (int i = 0; i < productListContainer.getChildCount(); i++) {
            View productRow = productListContainer.getChildAt(i);
            Spinner productNameInput = productRow.findViewById(R.id.product_name_input);
            Spinner quantityInput = productRow.findViewById(R.id.quantity_input);

            if (productNameInput.getSelectedItemPosition() == 0 || quantityInput.getSelectedItemPosition() == 0) {
                Toast.makeText(getActivity(), "Please select both product and quantity for each item", Toast.LENGTH_SHORT).show();
                return; // Exit if any dropdown is not selected
            }
        }

        // Process each row if validation is successful
        for (int i = 0; i < productListContainer.getChildCount(); i++) {
            View productRow = productListContainer.getChildAt(i);
            Spinner productNameInput = productRow.findViewById(R.id.product_name_input);
            Spinner quantityInput = productRow.findViewById(R.id.quantity_input);

            String productName = (String) productNameInput.getSelectedItem();
            int selectedQuantity = Integer.parseInt((String) quantityInput.getSelectedItem());

            Product selectedProduct = databaseHelper.getProductByName(productName);
            double price = selectedProduct.getPrice();

            totalAmount += selectedQuantity * price;
            int newQuantity = selectedProduct.getQuantity() - selectedQuantity;
            selectedProduct.setQuantity(newQuantity);
            databaseHelper.updateProductquantity(selectedProduct);

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
        Uri pdfUri = FileProvider.getUriForFile(getActivity(), "com.example.warehub.fileprovider", pdfFile);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(pdfUri, "application/pdf");
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(intent);
    }

    private void resetPage() {
        customerNameInput.setText("");
        totalAmountTextView.setText("Total Amount: 0.0");
        productListContainer.removeAllViews();
        addProductRow();
    }
}
