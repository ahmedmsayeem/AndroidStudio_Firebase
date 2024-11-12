package com.example.videochat.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.videochat.R;
import com.example.videochat.databinding.ActivityLoginBinding;
import com.example.videochat.repository.MainRepository;
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivity extends AppCompatActivity {
    private ActivityLoginBinding views;
    private MainRepository mainRepository;
    private Button enter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        views = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(views.getRoot());

        // Initialize Firebase
        FirebaseDatabase.getInstance().getReference().child("masound").setValue("New World!");

        // Initialize views and repository
        init();
    }

    private void init() {
        mainRepository = MainRepository.getInstance();
        enter = views.enterBtn;  // Ensures that we reference the button correctly

        enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = views.username.getText().toString();

                mainRepository.login(username, () -> {
                    Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(LoginActivity.this, Messaging.class);
                    intent.putExtra("username", username);
                    startActivity(intent);
                    finish();
                });
            }
        });
    }
}
