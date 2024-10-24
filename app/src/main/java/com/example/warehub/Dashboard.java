package com.example.warehub;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;



import android.database.Cursor;
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
    private DatabaseHelper myDb;
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

        // Initialize database
        myDb = new DatabaseHelper(requireContext());

        // Update dashboard information
        updateDashboard();

        return view;
    }

    // Method to update the dashboard with the latest data from the database
    private void updateDashboard() {
        // Get total items and total valuation
        int totalItems = getTotalItems();
        double totalValuation = getTotalValuation();

        // Set total items and valuation in the TextViews
        totalItemsTextView.setText(String.valueOf(totalItems));
        totalValuationTextView.setText(String.format("Rs. %.2f", totalValuation));

        // Load low quantity items
        loadLowQuantityItems();
    }

    // Method to get total number of items in the database
    private int getTotalItems() {
        return myDb.getAllProducts().size();
    }

    // Method to calculate total valuation of items (sum of quantity * price for each product)
    private double getTotalValuation() {
        double totalValuation = 0.0;
        ArrayList<Product> products = myDb.getAllProducts();
        for (Product product : products) {
            totalValuation += product.getQuantity() * product.getPrice();
        }
        return totalValuation;
    }

    // Method to load low quantity items (products with quantity < 3)
    private void loadLowQuantityItems() {
        lowQuantityProducts = myDb.getLowQuantityProducts();
        lowQuantityAdapter = new ProductAdapter(requireContext(), lowQuantityProducts, null); // Assuming you have a ProductAdapter for the ListView
        lowQuantityListView.setAdapter(lowQuantityAdapter);
    }
}
