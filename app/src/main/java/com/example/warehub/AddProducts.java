package com.example.warehub;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class AddProducts extends Fragment {



    private EditText productName, productCode, productQuantity, productPrice;
    private Button scanQrButton, saveButton;
    private DatabaseHelper myDb;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_products, container, false);

        // Initialize fields
        productName = view.findViewById(R.id.productName);
        productCode = view.findViewById(R.id.productCode);
        productQuantity = view.findViewById(R.id.productQuantity);
        productPrice = view.findViewById(R.id.productPrice);
        scanQrButton = view.findViewById(R.id.scanQrButton);
        saveButton = view.findViewById(R.id.saveButton);

        // Initialize DatabaseHelper
        myDb = new DatabaseHelper(requireContext());

        // QR Scan Button Listener
        scanQrButton.setOnClickListener(v -> {
            requireActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            IntentIntegrator integrator = IntentIntegrator.forSupportFragment(AddProducts.this);
            integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
            integrator.setPrompt("Scan a QR code");
            integrator.initiateScan();
        });

        // Save Button Listener
        saveButton.setOnClickListener(v -> saveProduct());

        return view;
    }

    // Save product to the database
    private void saveProduct() {
        String name = productName.getText().toString();
        String code = productCode.getText().toString();
        String quantityStr = productQuantity.getText().toString();
        String priceStr = productPrice.getText().toString();

        // Validate inputs
        if (name.isEmpty() || code.isEmpty() || quantityStr.isEmpty() || priceStr.isEmpty()) {
            Toast.makeText(requireContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        int quantity = Integer.parseInt(quantityStr);
        double price = Double.parseDouble(priceStr);

        // Insert data into the database
        boolean isInserted = myDb.insertData(name, code, quantity, price);

        if (isInserted) {
            Toast.makeText(requireContext(), "Product Saved", Toast.LENGTH_SHORT).show();
            // Optionally, clear the fields after saving
            productName.setText("");
            productCode.setText("");
            productQuantity.setText("");
            productPrice.setText("");
        } else {
            Toast.makeText(requireContext(), "Failed to Save Product", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Reset orientation to allow auto-rotation
        requireActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() != null) {
                // Assuming QR contains product data in a specific format, parse it
                String qrData = result.getContents();
                // Example parsing logic
                String[] productDetails = qrData.split(",");

                // Check if we have the expected number of fields
                if (productDetails.length == 4) {
                    productName.setText(productDetails[0]);
                    productCode.setText(productDetails[1]);
                    productQuantity.setText(productDetails[2]);
                    productPrice.setText(productDetails[3]);
                } else {
                    // If the QR data format is incorrect, show a message
                    Toast.makeText(requireContext(), "Invalid QR code format", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(requireContext(), "No data found", Toast.LENGTH_SHORT).show();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}