package com.example.warehub;

import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;

public class ManageItems extends Fragment {

    private DatabaseHelper databaseHelper;
    private ListView productListView;
    private EditText searchBox;
    // private ImageView clearSearch;
    private ProductAdapter productAdapter;
    private ArrayList<Product> productList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_manage_items, container, false);

        // Initialize UI elements
        productListView = view.findViewById(R.id.product_list_view);
        searchBox = view.findViewById(R.id.search_box);
//        clearSearch = view.findViewById(R.id.clear_search);

        // Initialize Database Helper
        databaseHelper = new DatabaseHelper(getContext());

        // Load all products initially
        loadProducts();

        // Search box functionality
        searchBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String query = s.toString();
                if (!query.isEmpty()) {
                    searchProducts(query);
                } else {
                    loadProducts(); // Load all products if search box is empty
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        // Clear search functionality
//        clearSearch.setOnClickListener(v -> {
//            searchBox.setText("");
//            loadProducts();
//        });
//
        return view;
    }

    // Method to load all products from the database
    private void loadProducts() {
        productList = databaseHelper.getAllProducts();
        productAdapter = new ProductAdapter(getContext(), productList, this);
        productListView.setAdapter(productAdapter);
    }

    // Method to search products based on user query
    private void searchProducts(String query) {
        ArrayList<Product> searchedProducts = databaseHelper.searchProducts(query);
        productAdapter.updateProductList(searchedProducts);
    }

    // Method to handle deleting a product
    public void deleteProduct(final Product product) {
        // Show confirmation dialog
        new AlertDialog.Builder(getContext())
                .setTitle("Delete Product")
                .setMessage("Are you sure you want to delete " + product.getProductName() + "?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    // Delete product from the database
                    int result = databaseHelper.deleteProduct(product.getId());
                    if (result > 0) {
                        Toast.makeText(getContext(), "Product deleted", Toast.LENGTH_SHORT).show();
                        loadProducts(); // Refresh product list after deletion
                    } else {
                        Toast.makeText(getContext(), "Failed to delete product", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

    // Method to handle editing a product
    public void editProduct(final Product product) {
        // Inflate the edit product layout
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_edit_product, null);
        final EditText editProductName = dialogView.findViewById(R.id.edit_product_name);
        final EditText editProductCode = dialogView.findViewById(R.id.edit_product_code);
        final EditText editProductQuantity = dialogView.findViewById(R.id.edit_product_quantity);
        final EditText editProductPrice = dialogView.findViewById(R.id.edit_product_price);

        // Populate the edit fields with the current product details
        editProductName.setText(product.getProductName());
        editProductCode.setText(product.getProductCode());
        editProductQuantity.setText(String.valueOf(product.getQuantity()));
        editProductPrice.setText(String.valueOf(product.getPrice()));

        // Create and show edit product dialog with rounded corners
        AlertDialog dialog = new AlertDialog.Builder(getContext(), R.style.RoundedAlertDialog)
                .setView(dialogView)
                .setPositiveButton("Update", (dialog1, which) -> {
                    // Get updated product details
                    String newName = editProductName.getText().toString();
                    String newCode = editProductCode.getText().toString();
                    int newQuantity = Integer.parseInt(editProductQuantity.getText().toString());
                    double newPrice = Double.parseDouble(editProductPrice.getText().toString());


                    // Update the product object
                    product.setProductName(newName);
                    product.setProductCode(newCode);
                    product.setQuantity(newQuantity);
                    product.setPrice(newPrice);

                    // Update the product in the database
                    boolean result = databaseHelper.updateProduct(product);
                    if (result) {
                        Toast.makeText(getContext(), "Product updated", Toast.LENGTH_SHORT).show();
                        loadProducts(); // Refresh product list after update
                    } else {
                        Toast.makeText(getContext(), "Failed to update product", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .create();

        dialog.setOnShowListener(dialogInterface -> {
            // Get the positive button (Save) and change its text color to green
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.GREEN);

            // Get the negative button (Cancel) and change its text color to red
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.RED);


        });

        dialog.show();
    }

}
