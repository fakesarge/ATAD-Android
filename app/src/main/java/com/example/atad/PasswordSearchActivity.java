package com.example.atad;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
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
                try {
                    Toast.makeText(PasswordSearchActivity.this,
                            "Works " + api.search(searchQuery.getText().toString()),
                            Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Toast.makeText(PasswordSearchActivity.this,
                            new RuntimeException(e).getMessage().toString(),
                            Toast.LENGTH_SHORT).show();

                }
            }
        });







    }
}