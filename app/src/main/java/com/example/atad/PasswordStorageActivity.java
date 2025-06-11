package com.example.atad;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import androidx.appcompat.app.AlertDialog;

/**
 * Activity for managing and displaying stored passwords with auto-login functionality
 */
public class PasswordStorageActivity extends AppCompatActivity {

    // SharedPreferences keys for storing password data
    private static final String PREFS_NAME = "PasswordPrefs";
    private static final String KEY_PASSWORD_COUNT = "PasswordCount";

    // UI components
    private LinearLayout passwordsContainer;
    private Button addButton;

    // Data storage
    private List<Account> accountList;
    private LeakAPI leakAPI;

    // Biometric authentication
    private Executor executor;
    private BiometricPrompt biometricPrompt;
    private BiometricPrompt.AuthenticationCallback authCallback;
    private Account currentAccount; // Currently selected account for auto-login

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_storage);

        initializeViews();
        setupBiometricAuthentication();
        loadSavedPasswords();
    }

    /**
     * Initialize all UI components
     */
    private void initializeViews() {
        passwordsContainer = findViewById(R.id.passwordsContainer);
        addButton = findViewById(R.id.addButton);
        accountList = new ArrayList<>();
        leakAPI = new LeakAPI();

        // Set click listener for adding new passwords
        addButton.setOnClickListener(v -> {
            startActivityForResult(new Intent(this, AddingAccount.class), 1);
        });
    }

    /**
     * Setup biometric authentication system
     */
    private void setupBiometricAuthentication() {
        executor = ContextCompat.getMainExecutor(this);

        authCallback = new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                runOnUiThread(() -> {
                    if (currentAccount != null) {
                        launchAutoLogin(currentAccount);
                    }
                });
            }

            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                runOnUiThread(() ->
                        Toast.makeText(PasswordStorageActivity.this,
                                "Authentication failed: " + errString,
                                Toast.LENGTH_SHORT).show()
                );
            }
        };

        biometricPrompt = new BiometricPrompt(this, executor, authCallback);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Refresh password list when returning from AddingAccount
        if (requestCode == 1 && resultCode == RESULT_OK) {
            loadSavedPasswords();
        }
    }

    /**
     * Load saved passwords from SharedPreferences
     */
    private void loadSavedPasswords() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        int passwordCount = sharedPreferences.getInt(KEY_PASSWORD_COUNT, 0);
        accountList.clear();

        // Load each account from storage
        for (int i = 0; i < passwordCount; i++) {
            String title = sharedPreferences.getString("account_title_" + i, "");
            String password = sharedPreferences.getString("account_password_" + i, "");
            String websiteUrl = sharedPreferences.getString("account_url_" + i, "");
            boolean isBreached = sharedPreferences.getBoolean("account_breached_" + i, false);

            if (!title.isEmpty() && !password.isEmpty()) {
                accountList.add(new Account(title, password, websiteUrl, isBreached));
            }
        }


        refreshPasswordViews();
    }

    /**
     * Shows confirmation dialog before deleting an account
     * @param account The account to potentially delete
     */
    private void showDeleteConfirmationDialog(Account account) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Account")
                .setMessage("Are you sure you want to delete this account?")
                .setPositiveButton("Delete", (dialog, which) -> deleteAccount(account))
                .setNegativeButton("Cancel", null)
                .show();
    }

    /**
     * Deletes an account from storage and refreshes the UI
     * @param account The account to delete
     */
    private void deleteAccount(Account account) {
        // Remove from the list
        accountList.remove(account);

        // Save the updated list
        saveAllAccounts();

        // Refresh the UI
        refreshPasswordViews();

        Toast.makeText(this, "Account deleted", Toast.LENGTH_SHORT).show();
    }

    /**
     * Saves all accounts to SharedPreferences
     */
    private void saveAllAccounts() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // Clear existing data
        editor.clear();

        // Save the count
        editor.putInt(KEY_PASSWORD_COUNT, accountList.size());

        // Save each account
        for (int i = 0; i < accountList.size(); i++) {
            Account account = accountList.get(i);
            editor.putString("account_title_" + i, account.getTitle());
            editor.putString("account_password_" + i, account.getPassword());
            editor.putString("account_url_" + i, account.getWebsiteUrl());
            editor.putBoolean("account_breached_" + i, account.isBreached());
        }

        editor.apply();
    }

    /**
     * Refresh the password list UI
     */
    private void refreshPasswordViews() {
        passwordsContainer.removeAllViews();

        for (Account account : accountList) {
            View passwordView = getLayoutInflater().inflate(R.layout.password_item, null);

            // Get references to views
            TextView titleTextView = passwordView.findViewById(R.id.titleTextView);
            TextView passwordTextView = passwordView.findViewById(R.id.passwordTextView);
            TextView breachTextView = passwordView.findViewById(R.id.breachTextView);
            ImageButton loginButton = passwordView.findViewById(R.id.loginButton);

            // Set account information
            titleTextView.setText("Account: " + account.getTitle());
            passwordTextView.setText("Password: " + account.getMaskedPassword());

            // Set breach status
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
                // Hide password again after 2 seconds
                passwordView.postDelayed(() ->
                                passwordTextView.setText("Password: " + account.getMaskedPassword()),
                        2000
                );
            });
            passwordView.setOnLongClickListener(v -> {
                showDeleteConfirmationDialog(account);
                return true; // consume the long-press event
            });

            // Set up auto login button if URL exists
            if (account.getWebsiteUrl() != null && !account.getWebsiteUrl().isEmpty()) {
                loginButton.setVisibility(View.VISIBLE);
                loginButton.setOnClickListener(v -> {
                    currentAccount = account;
                    showBiometricPrompt();
                });
            } else {
                loginButton.setVisibility(View.GONE);
            }

            passwordsContainer.addView(passwordView);
        }
    }

    /**
     * Show biometric authentication prompt
     */
    private void showBiometricPrompt() {
        BiometricManager biometricManager = BiometricManager.from(this);

        // Check if biometric authentication is available
        if (biometricManager.canAuthenticate(
                BiometricManager.Authenticators.BIOMETRIC_STRONG |
                        BiometricManager.Authenticators.DEVICE_CREDENTIAL
        ) != BiometricManager.BIOMETRIC_SUCCESS) {
            // Fallback to direct login if biometric not available
            launchAutoLogin(currentAccount);
            return;
        }

        // Configure the biometric prompt
        BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Verify Identity")
                .setSubtitle("Confirm it's you to auto-login")
                .setNegativeButtonText("Cancel")
                .setAllowedAuthenticators(
                        BiometricManager.Authenticators.BIOMETRIC_STRONG |
                                BiometricManager.Authenticators.DEVICE_CREDENTIAL
                )
                .build();

        biometricPrompt.authenticate(promptInfo);
    }

    /**
     * Launch website and copy password to clipboard
     * @param account The account to auto-login to
     */
    private void launchAutoLogin(Account account) {
        try {
            // Open website
            String url = account.getWebsiteUrl();
            if (!url.startsWith("http://") && !url.startsWith("https://")) {
                url = "https://" + url; // Ensure URL has protocol
            }

            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(browserIntent);

            // Copy password to clipboard as fallback
            ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("Password", account.getPassword());
            clipboard.setPrimaryClip(clip);

            Toast.makeText(this,
                    "Password copied to clipboard. Please paste it on the website",
                    Toast.LENGTH_LONG).show();

        } catch (Exception e) {
            Toast.makeText(this, "Error: Couldn't open website", Toast.LENGTH_SHORT).show();
        }
    }
}