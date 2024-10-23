package com.example.warehub;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;

public class GenerateBill extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_generate_bill, container, false);

        // Find buttons
        Button btnGenerateBill = view.findViewById(R.id.btn_generate_bill);
        Button btnViewAllBills = view.findViewById(R.id.btn_view_all_bills);


        ImageView placeholderImage = view.findViewById(R.id.placeholder_image);
        placeholderImage.setVisibility(View.VISIBLE);

        // Set up navigation
        btnGenerateBill.setOnClickListener(v -> {
            // Navigate to BillingPageFragment
            view.findViewById(R.id.placeholder_image).setVisibility(View.GONE);
            BillingPage billingPageFragment = new BillingPage();
            replaceFragment(billingPageFragment);
        });

        btnViewAllBills.setOnClickListener(v -> {
            // Navigate to ViewBillsFragment
            view.findViewById(R.id.placeholder_image).setVisibility(View.GONE);
            ViewBills viewBillsFragment = new ViewBills();
            replaceFragment(viewBillsFragment);
        });

        return view;
    }

    private void replaceFragment(Fragment fragment) {


        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment); // Use the container for fragment replacement
        transaction.addToBackStack(null); // Add to back stack to allow "back" navigation
        transaction.commit();
    }
}
