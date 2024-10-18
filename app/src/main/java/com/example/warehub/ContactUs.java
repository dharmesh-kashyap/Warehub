package com.example.warehub;

import android.os.Bundle;
import android.content.Intent;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class ContactUs extends Fragment {

    private Button emailButton, contactButton, instagramButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_contact_us, container, false);

        // Initialize buttons
        emailButton = view.findViewById(R.id.button_email);
        contactButton = view.findViewById(R.id.button_contact);
        instagramButton = view.findViewById(R.id.button_instagram);

        // Set up email button click listener
        emailButton.setOnClickListener(v -> {
            Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
            emailIntent.setData(Uri.parse("mailto:warehub@gmail.com"));  // Update email address
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Customer Support");
            startActivity(Intent.createChooser(emailIntent, "Send Email"));
        });

        contactButton.setOnClickListener(v -> {
            Intent phoneIntent = new Intent(Intent.ACTION_DIAL);
            phoneIntent.setData(Uri.parse("tel:+919876543210"));  // Update phone number
            startActivity(phoneIntent);
        });

        instagramButton.setOnClickListener(v -> {
            Uri instagramUri = Uri.parse("https://www.instagram.com/");  // Update Instagram URL
            Intent instagramIntent = new Intent(Intent.ACTION_VIEW, instagramUri);
            instagramIntent.setPackage("com.instagram.android");  // Open in Instagram app if installed
            try {
                startActivity(instagramIntent);
            } catch (Exception e) {
                // If Instagram app is not installed, open in browser
                startActivity(new Intent(Intent.ACTION_VIEW, instagramUri));
            }
        });

        // Return the inflated view
        return view;
    }
}
