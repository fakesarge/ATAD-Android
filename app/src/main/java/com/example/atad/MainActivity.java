package com.example.atad;
import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;

import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    FirebaseAuth auth;
    Button logouts;
    ImageButton open_password_gen, open_breach_search, open_password_storage;

    LeakAPI api = new LeakAPI();
    Users user = new Users();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);



        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        auth = FirebaseAuth.getInstance();
        open_password_gen = findViewById(R.id.openPassword);
        open_breach_search = findViewById(R.id.openDarkWeb);
        open_password_storage = findViewById(R.id.openDb);
        logouts = findViewById(R.id.logout);



        // If the user is already logged in this wont happen. If the current user is null it will redirect.
        if (auth.getCurrentUser() == null){
            Intent intent = new Intent(MainActivity.this, login.class);
            startActivity(intent);
        }


        logouts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MainActivity.this, login.class);

                FirebaseAuth.getInstance().signOut();
                startActivity(intent);
                finish();
            }
        });


        open_password_gen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent( MainActivity.this, PasswordGenActivity.class);
                startActivity(intent);
            }
        });
        open_password_storage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent( MainActivity.this, PasswordSearchActivity.class);
                startActivity(intent);
            }
        });
        open_breach_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent( MainActivity.this, PasswordStorageActivity.class);
                startActivity(intent);
            }
        });











    }
}