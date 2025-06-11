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

public class PasswordStorageActivity extends AppCompatActivity {
    private static final String PREFS_NAME = "PasswordPrefs";
    private static final String KEY_PASSWORD_COUNT = "PasswordCount";

    private LinearLayout passwordsContainer;
    private Button addButton;
    private List<Account> accountList;
    private LeakAPI leakAPI;

    // Biometric authentication
    private Executor executor;
    private BiometricPrompt biometricPrompt;
    private BiometricPrompt.AuthenticationCallback authCallback;
    private Account currentAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_storage);
// Check if biometric auth is available
        BiometricManager biometricManager = BiometricManager.from(this);
        switch (biometricManager.canAuthenticate(
                BiometricManager.Authenticators.BIOMETRIC_STRONG |
                        BiometricManager.Authenticators.DEVICE_CREDENTIAL
        )) {
            case BiometricManager.BIOMETRIC_SUCCESS:
                // Ready to use biometric auth
                break;
            default:
                Toast.makeText(this,
                        "Biometric authentication not available",
                        Toast.LENGTH_LONG).show();
                break;
        }
        // Initialize UI
        passwordsContainer = findViewById(R.id.passwordsContainer);
        addButton = findViewById(R.id.addButton);
        accountList = new ArrayList<>();
        leakAPI = new LeakAPI();

        // Setup biometric authentication
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
                        Toast.makeText(PasswordStorageActivity.this, "Authentication failed", Toast.LENGTH_SHORT).show()
                );
            }
        };
        biometricPrompt = new BiometricPrompt(this, executor, authCallback);

        addButton.setOnClickListener(v -> {
            startActivityForResult(new Intent(this, AddingAccount.class), 1);
        });

        loadPasswordsFromPreferences();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            loadPasswordsFromPreferences();
        }
    }

    private void loadPasswordsFromPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        int passwordCount = sharedPreferences.getInt(KEY_PASSWORD_COUNT, 0);
        accountList.clear();

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

    private void refreshPasswordViews() {
        passwordsContainer.removeAllViews();

        for (Account account : accountList) {
            View passwordView = getLayoutInflater().inflate(R.layout.password_item, null);

            TextView titleTextView = passwordView.findViewById(R.id.titleTextView);
            TextView passwordTextView = passwordView.findViewById(R.id.passwordTextView);
            TextView breachTextView = passwordView.findViewById(R.id.breachTextView);
            Button loginButton = passwordView.findViewById(R.id.loginButton);

            titleTextView.setText("Account: " + account.getTitle());
            passwordTextView.setText("Password: " + account.getMaskedPassword());

            if (account.isBreached()) {
                breachTextView.setText("Status: Breached");
                breachTextView.setTextColor(Color.RED);
            } else {
                breachTextView.setText("Status: Secure");
                breachTextView.setTextColor(Color.GREEN);
            }

            passwordView.setOnClickListener(v -> {
                passwordTextView.setText("Password: " + account.getPassword());
                passwordView.postDelayed(() ->
                                passwordTextView.setText("Password: " + account.getMaskedPassword()),
                        2000
                );
            });

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

    private void showBiometricPrompt() {
        BiometricManager biometricManager = BiometricManager.from(this);
        if (biometricManager.canAuthenticate(
                BiometricManager.Authenticators.BIOMETRIC_STRONG |
                        BiometricManager.Authenticators.DEVICE_CREDENTIAL
        ) != BiometricManager.BIOMETRIC_SUCCESS) {
            // If biometric isn't available, go straight to login
            launchAutoLogin(currentAccount);
            return;
        }

        // Otherwise show biometric prompt
        BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Verify Identity")
                .setSubtitle("Confirm it's you to auto-login")
                .setNegativeButtonText("Use Password Instead")
                .setAllowedAuthenticators(
                        BiometricManager.Authenticators.BIOMETRIC_STRONG |
                                BiometricManager.Authenticators.DEVICE_CREDENTIAL
                )
                .build();

        biometricPrompt.authenticate(promptInfo);
    }

    private void launchAutoLogin(Account account) {
        try {
            // Open website
            String url = account.getWebsiteUrl();
            if (!url.startsWith("http://") && !url.startsWith("https://")) {
                url = "https://" + url;
            }
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(browserIntent);

            // Copy password to clipboard
            ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("Password", account.getPassword());
            clipboard.setPrimaryClip(clip);

            Toast.makeText(this,
                    "Password copied to clipboard. Please paste it on the website",
                    Toast.LENGTH_LONG).show();

        } catch (Exception e) {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}