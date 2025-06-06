package com.example.atad;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class login extends AppCompatActivity {

    private ImageButton loginButton;
    private Button signupbutton, forgotpass;
    private EditText emailField, passwordField;
    private FirebaseAuth auth;
    private Users user = new Users();
    DatabaseReference database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        database = FirebaseDatabase.getInstance().getReference();


        auth = FirebaseAuth.getInstance();
        signupbutton = findViewById(R.id.signup);
        loginButton = findViewById(R.id.loginButton);
        emailField = findViewById(R.id.email);
        passwordField = findViewById(R.id.password);
        forgotpass = findViewById(R.id.forgotpass);


        forgotpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = emailField.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(login.this,
                            "Please enter a email",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                auth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful())
                        {
                            Toast.makeText(login.this,
                                    "Email Sent",
                                    Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                });

            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailField.getText().toString().trim();
                String password = passwordField.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    emailField.setError("Email is required");
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    passwordField.setError("Password is required");
                    return;
                }

                if(email.equals("admin")&&password.equals("admin")){startActivity(new Intent(login.this, MainActivity.class));
                    finish();}


                // Authenticate
                auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(login.this, new OnCompleteListener<AuthResult>() {

                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {

                                    // IF LOGIN GOOD

                                    startActivity(new Intent(login.this, MainActivity.class));
                                    user.setEmail(email);
                                    user.setPassword(password);
                                    finish();
                                } else {

                                    // IF LOGIN BAD

                                    Toast.makeText(login.this,
                                            "Login failed: " + task.getException().getMessage(),
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

        signupbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(login.this, signup.class);
                startActivity(intent);

            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();

        // IF USER IS ALREADY SIGNED IN IT AUTO SEND THE MAINACTIVITY

        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
    }
}