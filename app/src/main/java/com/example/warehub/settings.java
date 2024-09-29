package com.example.warehub;

import android.content.Context;
import android.os.Bundle;

import android.content.SharedPreferences;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class settings extends Fragment {

    private SwitchCompat themeSwitch;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        themeSwitch = view.findViewById(R.id.switch_theme);

        // Load the current theme preference
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("settings", Context.MODE_PRIVATE);
        boolean isDarkTheme = sharedPreferences.getBoolean("darkTheme", false);
        themeSwitch.setChecked(isDarkTheme);

        // Apply the saved theme when the fragment is loaded
        if (isDarkTheme) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        // Set up the listener to toggle theme when the switch is clicked
        themeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                editor.putBoolean("darkTheme", true);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                editor.putBoolean("darkTheme", false);
            }
            editor.apply();

            FragmentTransaction fragmentTransaction = requireFragmentManager().beginTransaction();
            fragmentTransaction.detach(this).attach(this).commit();
        });

        Button deleteDataButton = view.findViewById(R.id.Resetbutton);
        deleteDataButton.setOnClickListener(v -> showDeleteConfirmationDialog());

        return view;
    }
    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Delete All Data");
        builder.setMessage("Are you sure you want to delete all data?");

        // Create the dialog
        AlertDialog dialog = builder.create();

        // Set up the buttons
        dialog.setButton(AlertDialog.BUTTON_POSITIVE, "Confirm", (dialogInterface, which) -> {
            // Logic to delete the database should go here (leave this empty)
        });
        dialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel", (dialogInterface, which) -> dialog.dismiss());

        // Disable the Confirm button initially
        dialog.setOnShowListener(dialogInterface -> {
            Button confirmButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            confirmButton.setEnabled(false);

            // Delay the enabling of the Confirm button by 3 seconds
            new Handler().postDelayed(() -> confirmButton.setEnabled(true), 3000);
        });

        // Show the dialog
        dialog.show();
    }
}
