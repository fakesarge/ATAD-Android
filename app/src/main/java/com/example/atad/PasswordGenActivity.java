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

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PasswordGenActivity extends AppCompatActivity {

    private EditText generatedPassword;
    private Button generateButton, copyButton;
    private CheckBox uppercaseCheckbox, lowercaseCheckbox, numbersCheckbox, symbolsCheckbox;
    private SeekBar lengthSeekBar;
    private TextView lengthText;

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

        // Set up seek bar listener
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

        String password = generateRandomPassword(length, useUpper, useLower, useNumbers, useSymbols);
        generatedPassword.setText(password);
    }

    private String generateRandomPassword(int length, boolean useUpper, boolean useLower,
                                          boolean useNumbers, boolean useSymbols) {
        // Define character sets
        String upper = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String lower = "abcdefghijklmnopqrstuvwxyz";
        String numbers = "0123456789";
        String symbols = "!@#$%^&*()_+-=[]{}|;:,.<>?";

        // Build the character pool based on selected options
        StringBuilder charPool = new StringBuilder();
        if (useUpper) charPool.append(upper);
        if (useLower) charPool.append(lower);
        if (useNumbers) charPool.append(numbers);
        if (useSymbols) charPool.append(symbols);

        // Ensure at least one character from each selected set is included
        List<Character> passwordChars = new ArrayList<>();
        SecureRandom random = new SecureRandom();

        if (useUpper) {
            passwordChars.add(upper.charAt(random.nextInt(upper.length())));
        }
        if (useLower) {
            passwordChars.add(lower.charAt(random.nextInt(lower.length())));
        }
        if (useNumbers) {
            passwordChars.add(numbers.charAt(random.nextInt(numbers.length())));
        }
        if (useSymbols) {
            passwordChars.add(symbols.charAt(random.nextInt(symbols.length())));
        }

        // Fill the rest with random characters
        while (passwordChars.size() < length) {
            passwordChars.add(charPool.charAt(random.nextInt(charPool.length())));
        }

        // Shuffle the characters
        Collections.shuffle(passwordChars);

        // Build the final password string
        StringBuilder password = new StringBuilder();
        for (char c : passwordChars) {
            password.append(c);
        }

        return password.toString();
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