package com.example.atad;

import android.content.*;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Activity for adding new password entries
 */
public class AddingAccount extends AppCompatActivity {
    // UI elements
    private EditText titleEditText;

    private EditText passwordEditText;
    private Button saveButton;

    // API for checking password breaches
    private LeakAPI leakAPI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adding_account);

        // Initialize UI components
        titleEditText = findViewById(R.id.titleEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        saveButton = findViewById(R.id.saveButton);
        leakAPI = new LeakAPI();

        // Set up save button click listener
        saveButton.setOnClickListener(v -> {
            // Get user input
            String title = titleEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();

            // Validate input
            if (title.isEmpty() || password.isEmpty()) {
                if (title.isEmpty()) {
                    titleEditText.setError("Please enter a website/app name");
                }
                if (password.isEmpty()) {
                    passwordEditText.setError("Please enter a password");
                }
                return;
            }

            // Disable button during processing
            saveButton.setEnabled(false);
            saveButton.setText("Checking password...");

            // Check if password has been breached
            leakAPI.search(password, new LeakAPI.LeakCheckCallback() {
                @Override
                public void onResult(boolean isLeaked) {
                    runOnUiThread(() -> {
                        // Save account and return to previous screen
                        saveAccount(title, password, isLeaked);
                        if (isLeaked) {
                            Toast.makeText(AddingAccount.this,
                                    "Warning: This password has been compromised!",
                                    Toast.LENGTH_LONG).show();
                        }
                    });
                }

                @Override
                public void onError(Exception e) {
                    runOnUiThread(() -> {
                        // Save anyway if check fails
                        Toast.makeText(AddingAccount.this,
                                "Couldn't verify password security. Saved anyway.",
                                Toast.LENGTH_SHORT).show();
                        saveAccount(title, password, false);
                    });
                }
            });
        });
    }

    /**
     * Saves account to SharedPreferences
     */
    private void saveAccount(String title, String password, boolean isBreached) {
        String websiteUrl = ((EditText)findViewById(R.id.websiteEditText)).getText().toString().trim();

        SharedPreferences sharedPreferences = getSharedPreferences("PasswordPrefs", MODE_PRIVATE);
        int count = sharedPreferences.getInt("PasswordCount", 0);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("account_title_" + count, title);
        editor.putString("account_password_" + count, password);
        editor.putString("account_url_" + count, websiteUrl);
        editor.putBoolean("account_breached_" + count, isBreached);
        editor.putInt("PasswordCount", count + 1);
        editor.apply();

        setResult(RESULT_OK);
        finish();
    }
}