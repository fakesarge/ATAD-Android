package com.example.atad;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.List;

/**
 * Displays saved passwords and their security status
 */
public class PasswordStorageActivity extends AppCompatActivity {



    // SharedPreferences keys
    private static final String PREFS_NAME = "PasswordPrefs";
    private static final String KEY_PASSWORD_COUNT = "PasswordCount";

    // UI components
    private LinearLayout passwordsContainer;
    private Button addButton;



    // Data
    private List<Account> accountList;
    private LeakAPI leakAPI;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_storage);


        // Initialize UI
        passwordsContainer = findViewById(R.id.passwordsContainer);
        addButton = findViewById(R.id.addButton);
        accountList = new ArrayList<>();
        leakAPI = new LeakAPI();



        // Set up add button to launch AddingAccount activity
        addButton.setOnClickListener(v -> {
            startActivityForResult(
                    new Intent(this, AddingAccount.class),
                    1
            );
        });



        // Load saved passwords
        loadPasswordsFromPreferences();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Refresh list when returning from AddingAccount
        if (requestCode == 1 && resultCode == RESULT_OK) {
            loadPasswordsFromPreferences();
        }
    }


    /**
     * Loads saved accounts from SharedPreferences
     */
    private void loadPasswordsFromPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        int passwordCount = sharedPreferences.getInt(KEY_PASSWORD_COUNT, 0);
        accountList.clear();

        // Load each account
        for (int i = 0; i < passwordCount; i++) {
            String title = sharedPreferences.getString("account_title_" + i, "");
            String password = sharedPreferences.getString("account_password_" + i, "");
            boolean isBreached = sharedPreferences.getBoolean("account_breached_" + i, false);

            if (!title.isEmpty() && !password.isEmpty()) {
                accountList.add(new Account(title, password, isBreached));
            }
        }

        // Update UI
        refreshPasswordViews();
    }

    /**
     * Displays all saved accounts in the scrollable list
     */
    private void refreshPasswordViews() {
        // Clear existing views
        passwordsContainer.removeAllViews();

        // Create a view for each account
        for (Account account : accountList) {
            View passwordView = getLayoutInflater().inflate(R.layout.password_item, null);

            // Get references to views in the item layout
            TextView titleTextView = passwordView.findViewById(R.id.titleTextView);
            TextView passwordTextView = passwordView.findViewById(R.id.passwordTextView);
            TextView breachTextView = passwordView.findViewById(R.id.breachTextView);

            // Set account information
            titleTextView.setText("Account: " + account.getTitle());
            passwordTextView.setText("Password: " + account.getMaskedPassword());

            // Set breach status color and text
            if (account.isBreached()) {
                breachTextView.setText("Status: Breached");
                breachTextView.setTextColor(Color.RED);
            } else {
                breachTextView.setText("Status: Secure");
                breachTextView.setTextColor(Color.GREEN);
            }

            // Tap to temporarily reveal password
            passwordView.setOnClickListener(v -> {
                passwordTextView.setText("Password: " + account.getPassword());
                // Hide again after 2 seconds
                passwordView.postDelayed(() ->
                                passwordTextView.setText("Password: " + account.getMaskedPassword()),
                        2000
                );
            });

            // Add the account view to the container
            passwordsContainer.addView(passwordView);
        }
    }
}