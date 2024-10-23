package com.example.warehub;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.FrameLayout;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity2 extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private FrameLayout frameLayout;
    private static final String CURRENT_FRAGMENT_KEY = "currentFragment";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main2);

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);  // Enable the back button in the toolbar

        bottomNavigationView = findViewById(R.id.bottomNavView);
        frameLayout = findViewById(R.id.frameLayout);

        if (savedInstanceState != null) {
            Fragment currentFragment = getSupportFragmentManager().getFragment(savedInstanceState, CURRENT_FRAGMENT_KEY);
            if (currentFragment != null) {
                loadFragment(currentFragment, false);
            }
        } else {
            loadFragment(new Dashboard(), false);
        }

        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            Fragment selectedFragment = getFragmentForMenuItem(item.getItemId());
            if (selectedFragment != null) {
                loadFragment(selectedFragment, true);
            }
            return true;
        });
    }

    private Fragment getFragmentForMenuItem(int itemId) {
        if (itemId == R.id.dashboard) {
            return new Dashboard();
        } else if (itemId == R.id.manageItems) {
            return new ManageItems();
        } else if (itemId == R.id.addProducts) {
            return new AddProducts();
        } else if (itemId == R.id.generateBill) {
            return new GenerateBill();
        } else if (itemId == R.id.report) {
            return new Report();
        }
        return null; // Return null if no fragment matches
    }

    private void loadFragment(Fragment fragment, boolean addToBackStack) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment currentFragment = fragmentManager.findFragmentById(R.id.frameLayout);

        // Only replace the fragment if it's not already displayed
        if (currentFragment == null || !currentFragment.getClass().equals(fragment.getClass())) {
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);  // Optional: add transition
            fragmentTransaction.replace(R.id.frameLayout, fragment);
            if (addToBackStack) {
                fragmentTransaction.addToBackStack(null); // Add the current fragment to the back stack
            }
            fragmentTransaction.commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);  // Inflate your menu here
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // Handle the back button in the toolbar
        if (item.getItemId() == android.R.id.home) { // This is the back button in the toolbar
            onBackPressed(); // Call the method to handle back navigation
            return true;
        }

        Fragment selectedFragment = null;

        // Handle options menu item clicks with if-else
        if (item.getItemId() == R.id.Name) {
            selectedFragment = new ProfileEdit();  // Handle fragment for menu item "Name"
        } else if (item.getItemId() == R.id.settings) {
            selectedFragment = new settings();  // Handle fragment for menu item "Settings"
        } else if (item.getItemId() == R.id.contact_us) {
            selectedFragment = new ContactUs();  // Handle fragment for menu item "Contact Us"
        }

        // Load the fragment if one is selected
        if (selectedFragment != null) {
            loadFragment(selectedFragment, true);  // Load the selected fragment
        }

        return true;
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        // Save the current fragment
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.frameLayout);
        if (currentFragment != null) {
            getSupportFragmentManager().putFragment(outState, CURRENT_FRAGMENT_KEY, currentFragment);
        }
    }

    @Override
    public void onBackPressed() {
        FragmentManager fragmentManager = getSupportFragmentManager();

        if (fragmentManager.getBackStackEntryCount() > 0) {
            fragmentManager.popBackStack();  // Pop the fragment from back stack

            // Update the BottomNavigationView to reflect the current fragment
            fragmentManager.addOnBackStackChangedListener(this::updateBottomNavigationView);

        } else {
            showExitConfirmationDialog(); // Show confirmation dialog to exit the app
        }
    }

    private void updateBottomNavigationView() {
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.frameLayout);

        if (currentFragment instanceof Dashboard) {
            bottomNavigationView.setSelectedItemId(R.id.dashboard);
        } else if (currentFragment instanceof ManageItems) {
            bottomNavigationView.setSelectedItemId(R.id.manageItems);
        } else if (currentFragment instanceof AddProducts) {
            bottomNavigationView.setSelectedItemId(R.id.addProducts);
        } else if (currentFragment instanceof GenerateBill) {
            bottomNavigationView.setSelectedItemId(R.id.generateBill);
        } else if (currentFragment instanceof Report) {
            bottomNavigationView.setSelectedItemId(R.id.report);
        } else if (currentFragment instanceof ProfileEdit) {
            bottomNavigationView.setSelectedItemId(R.id.Name);
        } else if (currentFragment instanceof settings) {
            bottomNavigationView.setSelectedItemId(R.id.settings);
        } else if (currentFragment instanceof ContactUs) {
            bottomNavigationView.setSelectedItemId(R.id.contact_us);
        }
    }

    private void showExitConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Confirm Exit")
                .setMessage("Are you sure you want to exit?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        MainActivity.super.onBackPressed();  // Exit the app
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }
}
