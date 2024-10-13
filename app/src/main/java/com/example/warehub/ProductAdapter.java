package com.example.warehub;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.BaseAdapter;
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
            convertView = LayoutInflater.from(context).inflate(R.layout.item_product, parent, false);
        }

        TextView productName = convertView.findViewById(R.id.product_name);
        TextView productCode = convertView.findViewById(R.id.product_code);
        TextView productQuantity = convertView.findViewById(R.id.product_quantity);
        TextView productPrice = convertView.findViewById(R.id.product_price);
        Button editButton = convertView.findViewById(R.id.edit_button);
        Button deleteButton = convertView.findViewById(R.id.delete_button);

        Product product = productList.get(position);

        productName.setText(product.getProductName());
        productCode.setText(product.getProductCode());
        productQuantity.setText("Quantity: " + product.getQuantity());
        productPrice.setText("Price: " + product.getPrice());

        editButton.setOnClickListener(v -> manageItemsFragment.editProduct(product));
        deleteButton.setOnClickListener(v -> manageItemsFragment.deleteProduct(product));

        return convertView;
    }

    public void updateProductList(ArrayList<Product> newList) {
        productList.clear();
        productList.addAll(newList);
        notifyDataSetChanged();
    }
}
