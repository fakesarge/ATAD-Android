// PasswordSearchActivity.java
package com.example.atad;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;


/**
 * Password search. Checks to see if password entered is leaked or not
 */
public class PasswordSearchActivity extends AppCompatActivity {

    ImageButton searchButton;
    EditText searchQuery;
    LeakAPI api;
    TextView leakInfoText, breachStatusText, breachAmountText;

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

        // initialize everything
        searchButton = findViewById(R.id.searchB);
        searchQuery = findViewById(R.id.searchQ);
        leakInfoText = findViewById(R.id.textView8);
        breachStatusText = findViewById(R.id.textView9);
        breachAmountText = findViewById(R.id.textView10);


        api = new LeakAPI();

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //gets what user typed in
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

                // Reset UI
                leakInfoText.setText("Searching...");
                breachStatusText.setText("Breach Status: Checking...");
                breachAmountText.setText("Breach Amount: 0");

                api.search(password, new LeakAPI.LeakCheckCallback() {
                    @Override
                    public void onResult(final boolean isLeaked) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                leakInfoText.setText("Leak Found: HaveIBeenPwned.com");
                                if(isLeaked){breachStatusText.setText("Breach Status: True");breachAmountText.setText("Breach Amount: Multiple");}
                                if(!isLeaked){breachStatusText.setText("Breach Status: False");breachAmountText.setText("Breach Amount: 0");}

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

                                // Update the TextViews with error state
                                leakInfoText.setText("Error checking password");
                                breachStatusText.setText("Breach Status: Unknown");
                                breachAmountText.setText("Breach Amount: Unknown");
                            }
                        });
                    }
                });
            }
        });
    }
}