package com.example.atad;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class AddingAccount extends AppCompatActivity {
    private EditText titleEditText;
    private EditText passwordEditText;
    private Button saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_adding_account);

        titleEditText = findViewById(R.id.titleEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        saveButton = findViewById(R.id.saveButton);

        saveButton.setOnClickListener(v -> saveAccount());
    }

    private void saveAccount() {
        String title = titleEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        if (!title.isEmpty() && !password.isEmpty()) {
            // Create a new Account object
            Account newAccount = new Account(title, password);

            // Get the existing list from SharedPreferences
            SharedPreferences sharedPreferences = getSharedPreferences("PasswordPrefs", MODE_PRIVATE);
            int count = sharedPreferences.getInt("PasswordCount", 0);

            // Save the new account
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("account_title_" + count, title);
            editor.putString("account_password_" + count, password);
            editor.putInt("PasswordCount", count + 1);
            editor.apply();

            // Return to main activity
            setResult(RESULT_OK);
            finish();
        } else {
            if (title.isEmpty()) titleEditText.setError("Title is required");
            if (password.isEmpty()) passwordEditText.setError("Password is required");
        }
    }
}
