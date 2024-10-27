package com.example.warehub;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import java.util.ArrayList;

public class Dashboard extends Fragment {

    private TextView totalItemsTextView, totalValuationTextView;
    private ListView lowQuantityListView;
    private DatabaseHelper databaseHelper;
    private ProductAdapter lowQuantityAdapter;
    private ArrayList<Product> lowQuantityProducts;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);

        // Initialize UI components
        totalItemsTextView = view.findViewById(R.id.total_items_text_view);
        totalValuationTextView = view.findViewById(R.id.total_valuation_text_view);
        lowQuantityListView = view.findViewById(R.id.low_quantity_list_view);

        // Initialize DatabaseHelper instance
        databaseHelper = new DatabaseHelper(requireContext());

        // Update dashboard information
        updateDashboard();

        return view;
    }

    // Method to update the dashboard with the latest data from the database
    private void updateDashboard() {
        // Get total items count
        int totalItems = databaseHelper.getAllProducts().size();
        totalItemsTextView.setText(String.valueOf(totalItems));

        // Get total valuation
        double totalValuation = calculateTotalValuation();
        totalValuationTextView.setText(String.format("Rs. %.2f", totalValuation));

        // Load low quantity items
        loadLowQuantityItems();
    }

    // Method to calculate the total valuation from all products in the database
    private double calculateTotalValuation() {
        double totalValuation = 0.0;
        ArrayList<Product> allProducts = databaseHelper.getAllProducts();
        for (Product product : allProducts) {
            totalValuation += product.getQuantity() * product.getPrice();
        }
        return totalValuation;
    }

    // Method to load low quantity items (products with quantity < threshold)
    private void loadLowQuantityItems() {
        int lowQuantityThreshold = 3;
        lowQuantityProducts = databaseHelper.getLowQuantityProducts();

        // Assuming you have a ProductAdapter for displaying products
        lowQuantityAdapter = new ProductAdapter(requireContext(), lowQuantityProducts, new ManageItems());
        lowQuantityListView.setAdapter(lowQuantityAdapter);
    }
}
