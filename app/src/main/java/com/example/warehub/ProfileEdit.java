package com.example.warehub;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class ProfileEdit extends Fragment {

    private EditText etFullName, etEmail, etCompanyName, etPhone, etUsername, etPassword, etConfirmPassword;
    private Button btnSaveChanges, btnLogout;
    private DBHelper dbHelper;  // DBHelper instance

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile_edit, container, false);

        // Initialize DBHelper
        dbHelper = new DBHelper(getContext());

        // Initialize views
        etFullName = view.findViewById(R.id.etFullName);
        etEmail = view.findViewById(R.id.etEmail);
        etCompanyName = view.findViewById(R.id.etCompanyName);
        etPhone = view.findViewById(R.id.etPhone);
        etUsername = view.findViewById(R.id.etUsername);
        etPassword = view.findViewById(R.id.etPassword);
        etConfirmPassword = view.findViewById(R.id.etConfirmPassword);
        btnSaveChanges = view.findViewById(R.id.btnSaveChanges);
        btnLogout = view.findViewById(R.id.btnLogout);

        // Load user data from database
        loadUserData();

        // Set Save Changes button click listener
        btnSaveChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveChanges();
            }
        });

        // Set Logout button click listener
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });

        return view;
    }

    // Method to load user data from the database
    private void loadUserData() {
        // Assuming user identification through username; adjust as per your DBHelper implementation
        String username = "user123";  // Replace this with actual logic to fetch logged-in user's username
        User user = dbHelper.getUserByUsername(username);

        if (user != null) {
            etFullName.setText(user.getFullName());
            etEmail.setText(user.getEmail());
            etCompanyName.setText(user.getCompanyName());
            etPhone.setText(user.getPhone());
            etUsername.setText(user.getUsername());
            etPassword.setText(user.getPassword());  // Be cautious with displaying passwords
        } else {
            Toast.makeText(getContext(), "Failed to load user data", Toast.LENGTH_SHORT).show();
        }
    }

    // Method to save changes
    private void saveChanges() {
        String fullName = etFullName.getText().toString();
        String email = etEmail.getText().toString();
        String companyName = etCompanyName.getText().toString();
        String phone = etPhone.getText().toString();
        String username = etUsername.getText().toString();
        String password = etPassword.getText().toString();
        String confirmPassword = etConfirmPassword.getText().toString();

        if (password.equals(confirmPassword)) {
            // Save the data to the database
            boolean isUpdated = dbHelper.updateUser(username, fullName, email, companyName, phone, password);
            if (isUpdated) {
                Toast.makeText(getContext(), "Profile updated successfully!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Failed to update profile", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getContext(), "Passwords do not match!", Toast.LENGTH_SHORT).show();
        }
    }

    // Method to handle logout
    private void logout() {
        Intent intent = new Intent(requireActivity(), registration.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);

        Toast.makeText(getContext(), "Logged out successfully!", Toast.LENGTH_SHORT).show();
        requireActivity().finish();
    }
}
