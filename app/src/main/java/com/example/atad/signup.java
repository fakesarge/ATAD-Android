package com.example.atad;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class signup extends AppCompatActivity {

    ImageButton signup;
    EditText rg_password, rg_repassword, rg_email, rg_username;
    FirebaseAuth auth;
    DatabaseReference database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signup);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance().getReference();

        signup = findViewById(R.id.signup);
        rg_username = findViewById(R.id.username);
        rg_email = findViewById(R.id.email);
        rg_password = findViewById(R.id.password);
        rg_repassword = findViewById(R.id.repassword);

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = rg_username.getText().toString().trim();
                String email = rg_email.getText().toString().trim();
                String password = rg_password.getText().toString().trim();
                String repassword = rg_repassword.getText().toString().trim();

                if (TextUtils.isEmpty(name) || TextUtils.isEmpty(email) ||
                        TextUtils.isEmpty(password) || TextUtils.isEmpty(repassword)) {
                    Toast.makeText(signup.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                }
                else if (!password.equals(repassword)) {
                    Toast.makeText(signup.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                }
                else if (password.length() < 6) {
                    Toast.makeText(signup.this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
                }
                else {
                    auth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        String userId = auth.getCurrentUser().getUid();

                                        // hashMap to store user data
                                        HashMap<String, String> userMap = new HashMap<>();
                                        userMap.put("userId", userId);
                                        userMap.put("username", name);
                                        userMap.put("email", email);
                                        userMap.put("profileImage", "default");

                                        // saving data
                                        database.child("Users").child(userId).setValue(userMap)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            Toast.makeText(signup.this, "Registration successful!", Toast.LENGTH_SHORT).show();
                                                            Intent intent = new Intent(signup.this, login.class);
                                                            startActivity(intent);
                                                            finish(); // Close the signup activity
                                                        } else {
                                                            Toast.makeText(signup.this, "Failed to save user data: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                    } else {
                                        Toast.makeText(signup.this, "Registration failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });
    }
}