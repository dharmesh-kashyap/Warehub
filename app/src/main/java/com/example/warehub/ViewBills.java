package com.example.warehub;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;

public class ViewBills extends Fragment {
    private RecyclerView billsRecyclerView;
    private BillAdapter billAdapter;
    private DatabaseHelper databaseHelper;
    private EditText searchEditText;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_view_bills, container, false);

        billsRecyclerView = view.findViewById(R.id.bills_recycler_view);
        searchEditText = view.findViewById(R.id.search_edit_text);
        databaseHelper = new DatabaseHelper(getContext());

        // Setup RecyclerView
        billsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        billAdapter = new BillAdapter(new ArrayList<>(), this::onBillClicked);
        billsRecyclerView.setAdapter(billAdapter);

        displayBills();

        // Add search functionality
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                filterBills(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        return view;
    }

    private void displayBills() {
        ArrayList<Bill> bills = databaseHelper.getAllBills();
        billAdapter.updateBills(bills);
    }

    private void filterBills(String query) {
        ArrayList<Bill> allBills = databaseHelper.getAllBills();
        ArrayList<Bill> filteredBills = new ArrayList<>();

        for (Bill bill : allBills) {
            if (bill.getCustomerName().toLowerCase().contains(query.toLowerCase())) {
                filteredBills.add(bill);
            }
        }

        billAdapter.updateBills(filteredBills);
    }

    private void onBillClicked(Bill bill) {
        // View bill PDF
        File pdfFile = new File(bill.getPdfPath());
        if (!pdfFile.exists()) {
            Toast.makeText(getContext(), "PDF file not found!", Toast.LENGTH_SHORT).show();
            return;
        }

        Uri pdfUri = FileProvider.getUriForFile(getContext(),
                getContext().getPackageName() + ".fileprovider",
                pdfFile);

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(pdfUri, "application/pdf");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Toast.makeText(getContext(), "No app available to view PDF.", Toast.LENGTH_SHORT).show();
        }
    }
}
