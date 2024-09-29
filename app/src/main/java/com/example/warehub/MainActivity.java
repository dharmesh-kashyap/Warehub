package com.example.warehub;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.FrameLayout;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private FrameLayout frameLayout;
    private static final String CURRENT_FRAGMENT_KEY = "currentFragment";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        bottomNavigationView = findViewById(R.id.bottomNavView);
        frameLayout = findViewById(R.id.frameLayout);

        if (savedInstanceState != null) {
            Fragment currentFragment = getSupportFragmentManager().getFragment(savedInstanceState, CURRENT_FRAGMENT_KEY);
            if (currentFragment != null) {
                loadFragment(currentFragment);
            }
        } else {
            loadFragment(new Dashboard());
        }

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener(){
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                Fragment selectedFragment = null;

                // Handle navigation item selection
                if (itemId == R.id.dashboard) {
                    selectedFragment = new Dashboard();
                } else if (itemId == R.id.manageItems) {
                    selectedFragment = new ManageItems();
                } else if (itemId == R.id.addProducts) {
                    selectedFragment = new AddProducts();
                } else if (itemId == R.id.generateBill) {
                    selectedFragment = new GenerateBill();
                } else if (itemId == R.id.report) {
                    selectedFragment = new Report();
                }

                if (selectedFragment != null) {
                    // Replace the fragment
                    loadFragment(selectedFragment);
                }

                return true;
            }


        });

    }

    private void loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout, fragment);  // Always replace to prevent overlap
        fragmentTransaction.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);  // Inflate your menu here
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Fragment selectedFragment = null;

        // Handle options menu item clicks with if-else
        if (item.getItemId() == R.id.Name) {
            selectedFragment = new ProfileEdit();  // Handle fragment for menu item "Name"
        } else if (item.getItemId() == R.id.settings) {
            selectedFragment = new settings();  // Handle fragment for menu item "Settings"
          }
        else if (item.getItemId() == R.id.contact_us) {
            selectedFragment = new ContactUs();  // Handle fragment for menu item "Contact Us"
        }
        else {
            return super.onOptionsItemSelected(item);  // Handle the default case
        }

        // Load the fragment if one is selected
        loadFragment(selectedFragment);  // Load the selected fragment

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


}