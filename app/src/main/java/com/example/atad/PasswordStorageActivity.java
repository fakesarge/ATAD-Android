package com.example.atad;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.List;

public class PasswordStorageActivity extends AppCompatActivity {
    private static final String PREFS_NAME = "PasswordPrefs";
    private static final String KEY_PASSWORD_COUNT = "PasswordCount";

    private LinearLayout passwordsContainer;
    private List<Account> accountList;
    private Button addButton;





    private final ActivityResultLauncher<Intent> addPasswordLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    loadPasswordsFromPreferences();
                    refreshPasswordViews();
                }
            });


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_storage);

        passwordsContainer = findViewById(R.id.passwordsContainer);
        addButton = findViewById(R.id.addButton);
        accountList = new ArrayList<>();

        addButton.setOnClickListener(v -> {
            Intent intent = new Intent(PasswordStorageActivity.this, AddingAccount.class);
            addPasswordLauncher.launch(intent);
        });

        // lload passwords when activity starts
        loadPasswordsFromPreferences();
        displayPasswords();
    }

    private void displayPasswords() {
        for (Account account : accountList) {
            createPasswordView(account);
        }
    }

    private void loadPasswordsFromPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        int passwordCount = sharedPreferences.getInt(KEY_PASSWORD_COUNT, 0);
        accountList.clear(); // Clear existing list before loading

        for (int i = 0; i < passwordCount; i++) {
            String title = sharedPreferences.getString("account_title_" + i, "");
            String password = sharedPreferences.getString("account_password_" + i, "");

            if (!title.isEmpty() && !password.isEmpty()) {
                Account account = new Account(title, password);
                accountList.add(account);
            }
        }
    }

    private void createPasswordView(final Account account) {
        View passwordView = getLayoutInflater().inflate(R.layout.password_item, null);
        TextView titleTextView = passwordView.findViewById(R.id.titleTextView);
        TextView passwordTextView = passwordView.findViewById(R.id.passwordTextView);

        titleTextView.setText(account.getTitle());
        passwordTextView.setText(account.getMaskedPassword());

        passwordView.setOnLongClickListener(v -> {
            showDeleteDialog(account);
            return true;
        });

        passwordView.setOnClickListener(v -> {
            passwordTextView.setText(account.getPassword());
            passwordView.postDelayed(() -> passwordTextView.setText(account.getMaskedPassword()), 2000);
        });

        passwordsContainer.addView(passwordView);
    }

    private void showDeleteDialog(final Account account) {
        new AlertDialog.Builder(this)
                .setTitle("Delete this account?")
                .setMessage("Are you sure you want to delete " + account.getTitle() + "?")
                .setPositiveButton("Delete", (dialog, which) -> deleteAccountAndRefresh(account))
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteAccountAndRefresh(Account account) {
        accountList.remove(account);
        savePasswordsToPreferences();
        refreshPasswordViews();
    }

    private void refreshPasswordViews() {
        passwordsContainer.removeAllViews(); // Clear all views first
        for (Account account : accountList) {
            createPasswordView(account); // Recreate views for all accounts
        }
    }

    private void savePasswordsToPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putInt(KEY_PASSWORD_COUNT, accountList.size());
        for (int i = 0; i < accountList.size(); i++) {
            Account account = accountList.get(i);
            editor.putString("account_title_" + i, account.getTitle());
            editor.putString("account_password_" + i, account.getPassword());
        }
        editor.apply();
    }

}