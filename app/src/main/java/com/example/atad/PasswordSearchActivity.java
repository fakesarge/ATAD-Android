package com.example.atad;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class PasswordSearchActivity extends AppCompatActivity {

    Button searchButton;
    EditText searchQuery;
    LeakAPI api;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_password_search);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        searchButton = findViewById(R.id.searchB);
        searchQuery = findViewById(R.id.searchQ);
        api = new LeakAPI();

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String password = searchQuery.getText().toString();
                if (password.isEmpty()) {
                    Toast.makeText(PasswordSearchActivity.this,
                            "Please enter a password",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                // Show loading message
                Toast.makeText(PasswordSearchActivity.this,
                        "Checking password...",
                        Toast.LENGTH_SHORT).show();

                api.search(password, new LeakAPI.LeakCheckCallback() {
                    @Override
                    public void onResult(final boolean isLeaked) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                String message = isLeaked
                                        ? "This password has been leaked! Do not use it!"
                                        : "This password hasn't been found in any leaks (but still choose a strong one!)";
                                Toast.makeText(PasswordSearchActivity.this,
                                        message,
                                        Toast.LENGTH_LONG).show();
                            }
                        });
                    }

                    @Override
                    public void onError(final Exception e) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(PasswordSearchActivity.this,
                                        "Error: " + e.getMessage(),
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            }
        });
    }
}