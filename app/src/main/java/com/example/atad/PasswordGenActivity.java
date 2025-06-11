package com.example.atad;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class PasswordGenActivity extends AppCompatActivity {

    private EditText generatedPassword;
    private Button generateButton, copyButton;
    private CheckBox uppercaseCheckbox, lowercaseCheckbox, numbersCheckbox, symbolsCheckbox;
    private SeekBar lengthSeekBar;
    private TextView lengthText;

    private PasswordGen gen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_password_gen);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize views
        generatedPassword = findViewById(R.id.generatedPassword);
        generateButton = findViewById(R.id.generateButton);
        copyButton = findViewById(R.id.copyButton);
        uppercaseCheckbox = findViewById(R.id.uppercaseCheckbox);
        lowercaseCheckbox = findViewById(R.id.lowercaseCheckbox);
        numbersCheckbox = findViewById(R.id.numbersCheckbox);
        symbolsCheckbox = findViewById(R.id.symbolsCheckbox);
        lengthSeekBar = findViewById(R.id.lengthSeekBar);
        lengthText = findViewById(R.id.lengthText);

        gen = new PasswordGen();
        lengthText.setText("Length: " + lengthSeekBar.getProgress());


        //Listener for the length changing
        lengthSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // Minimum length of 8
                int length = Math.max(8, progress);
                lengthText.setText("Length: " + length);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        // Generate button click listener
        generateButton.setOnClickListener(v -> generatePassword());

        // Copy button click listener
        copyButton.setOnClickListener(v -> copyPasswordToClipboard());

        // Generate first password on startup
        generatePassword();
    }

    private void generatePassword() {
        int length = Math.max(8, lengthSeekBar.getProgress());
        boolean useUpper = uppercaseCheckbox.isChecked();
        boolean useLower = lowercaseCheckbox.isChecked();
        boolean useNumbers = numbersCheckbox.isChecked();
        boolean useSymbols = symbolsCheckbox.isChecked();

        if (!useUpper && !useLower && !useNumbers && !useSymbols) {
            Toast.makeText(this, "Please select at least one character type", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            String password = gen.generatePassword(length, useUpper, useLower, useNumbers, useSymbols, true);
            generatedPassword.setText(password);
        } catch (IllegalArgumentException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void copyPasswordToClipboard() {
        String password = generatedPassword.getText().toString();

        if (password.isEmpty()) {
            Toast.makeText(this, "No password to copy", Toast.LENGTH_SHORT).show();
            return;
        }

        ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Generated Password", password);
        clipboard.setPrimaryClip(clip);

        Toast.makeText(this, "Password copied to clipboard", Toast.LENGTH_SHORT).show();
    }
}