package com.example.warehub;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
public class ProductAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<Product> productList;
    private ManageItems manageItemsFragment;

    public ProductAdapter(Context context, ArrayList<Product> productList, ManageItems manageItemsFragment) {
        this.context = context;
        this.productList = productList;
        this.manageItemsFragment = manageItemsFragment;
    }

    @Override
    public int getCount() {
        return productList.size();
    }

    @Override
    public Object getItem(int position) {
        return productList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return productList.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
                // Inflate the custom layout for each product
            convertView = LayoutInflater.from(context).inflate(R.layout.item_product, parent, false);
        }

            // Get references to the UI elements in the custom layout
        TextView productName = convertView.findViewById(R.id.product_name);
        TextView productCode = convertView.findViewById(R.id.product_code);
        TextView productQuantity = convertView.findViewById(R.id.product_quantity);
        TextView productPrice = convertView.findViewById(R.id.product_price);
        ImageButton editButton = convertView.findViewById(R.id.edit_button);
        ImageButton deleteButton = convertView.findViewById(R.id.delete_button);

            // Get the current product object
        Product product = productList.get(position);

            // Set the values for the UI elements with meaningful labels
        productName.setText("Name: " + product.getProductName());
        productCode.setText("Code: " + product.getProductCode());
        productQuantity.setText("Quantity: " + product.getQuantity());
        productPrice.setText("Price: " + product.getPrice());

            // Set click listeners for edit and delete actions
        editButton.setOnClickListener(v -> manageItemsFragment.editProduct(product));
        deleteButton.setOnClickListener(v -> manageItemsFragment.deleteProduct(product));

        return convertView;
    }

        // Method to update the product list and notify the adapter to refresh the ListView
    public void updateProductList(ArrayList<Product> newList) {
        this.productList.clear();  // Clear the current list
        this.productList.addAll(newList);  // Add all products from the new list
        notifyDataSetChanged();  // Notify the adapter that the data has changed so it can refresh the ListView
    }
}
