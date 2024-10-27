package com.example.warehub;



import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;

public class BillAdapter extends RecyclerView.Adapter<BillAdapter.BillViewHolder> {
    private ArrayList<Bill> bills;
    private OnBillClickListener listener;
    private Context context;

    public BillAdapter(ArrayList<Bill> bills, OnBillClickListener listener) {
        this.bills = bills;
        this.listener = listener;
    }

    @NonNull
    @Override
    public BillViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.biil_item, parent, false);
        return new BillViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BillViewHolder holder, int position) {
        Bill bill = bills.get(position);

        holder.customerNameTextView.setText(bill.getCustomerName());
        holder.billNumberTextView.setText(String.valueOf(bill.getId()));
        holder.totalAmountTextView.setText(String.format("₹%.2f", bill.getTotalAmount()));

        holder.downloadButton.setOnClickListener(v -> downloadBill(bill));
        holder.deleteButton.setOnClickListener(v -> deleteBill(bill, position));
    }

    private void downloadBill(Bill bill) {
        File pdfFile = new File(bill.getPdfPath());
        if (!pdfFile.exists()) {
            Toast.makeText(context, "PDF file not found!", Toast.LENGTH_SHORT).show();
            return;
        }

        Uri pdfUri = FileProvider.getUriForFile(context,
                context.getPackageName() + ".fileprovider", pdfFile);

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(pdfUri, "application/pdf");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        if (intent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(intent);
        } else {
            Toast.makeText(context, "No app available to view PDF.", Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteBill(Bill bill, int position) {
        DatabaseHelper db = new DatabaseHelper(context);
        if (db.deleteBill(bill.getId())) {
            bills.remove(position);
            notifyItemRemoved(position);
            Toast.makeText(context, "Bill deleted", Toast.LENGTH_SHORT).show();
        }
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
        TextView customerNameTextView, billNumberTextView, totalAmountTextView;
        ImageView downloadButton, deleteButton;

        public BillViewHolder(@NonNull View itemView) {
            super(itemView);
            customerNameTextView = itemView.findViewById(R.id.bill_customer_name);
            billNumberTextView = itemView.findViewById(R.id.bill_number);
            totalAmountTextView = itemView.findViewById(R.id.bill_total_amount);
            downloadButton = itemView.findViewById(R.id.download_button);
            deleteButton = itemView.findViewById(R.id.delete_button);
        }
    }

    public interface OnBillClickListener {
        void onBillClicked(Bill bill);
    }
}
