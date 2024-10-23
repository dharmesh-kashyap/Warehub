package com.example.warehub;



import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class BillAdapter extends RecyclerView.Adapter<BillAdapter.BillViewHolder> {
    private ArrayList<Bill> bills;
    private OnBillClickListener listener;

    public BillAdapter(ArrayList<Bill> bills, OnBillClickListener listener) {
        this.bills = bills;
        this.listener = listener;
    }

    @NonNull
    @Override
    public BillViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the new layout for each bill item
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.biil_item, parent, false);
        return new BillViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BillViewHolder holder, int position) {
        Bill bill = bills.get(position);

        // Set the views with the bill data
        holder.customerNameTextView.setText(bill.getCustomerName());
        holder.totalAmountTextView.setText(String.format("₹%.2f", bill.getTotalAmount())); // Format amount in rupees
        holder.billDateTextView.setText(bill.getBillDate());

        holder.itemView.setOnClickListener(v -> listener.onBillClicked(bill));

        holder.deleteButton.setOnClickListener(v -> {
            // Delete bill logic
            DatabaseHelper db = new DatabaseHelper(holder.itemView.getContext());
            if (db.deleteBill(bill.getId())) {
                bills.remove(position);
                notifyItemRemoved(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return bills.size();
    }

    public void updateBills(ArrayList<Bill> newBills) {
        this.bills.clear();
        this.bills.addAll(newBills);
        notifyDataSetChanged();
    }

    class BillViewHolder extends RecyclerView.ViewHolder {
        TextView customerNameTextView;
        TextView totalAmountTextView;
        TextView billDateTextView;
        Button deleteButton;

        public BillViewHolder(@NonNull View itemView) {
            super(itemView);
            // Reference the updated IDs from item_bill.xml
            customerNameTextView = itemView.findViewById(R.id.bill_customer_name);
            totalAmountTextView = itemView.findViewById(R.id.bill_total_amount);
            billDateTextView = itemView.findViewById(R.id.bill_date);
            deleteButton = itemView.findViewById(R.id.delete_button);
        }
    }

    public interface OnBillClickListener {
        void onBillClicked(Bill bill);
    }
}

