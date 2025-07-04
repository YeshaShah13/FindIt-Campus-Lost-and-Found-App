package com.example.findit;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.EmailAuthCredential;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity {

    EditText editTextName, editTextEmail, editTextPwd;
    Button buttonSignUp;
    TextView textViewLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        editTextName = findViewById(R.id.editTextName);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPwd = findViewById(R.id.editTextPwd);
        buttonSignUp = findViewById(R.id.buttonSignUp);
        textViewLogin = findViewById(R.id.textViewLogin);

        buttonSignUp.setOnClickListener(v -> {
            String name = editTextName.getText().toString().trim();
            String email = editTextEmail.getText().toString().trim();
            String password = editTextPwd.getText().toString().trim();

            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            mAuth.createUserWithEmailAndPassword(email, password)
                     .addOnCompleteListener(task -> {
                         if (task.isSuccessful()) {
                             Toast.makeText(this, "Registration Successful", Toast.LENGTH_SHORT).show();
                             startActivity(new Intent(this, LoginActivity.class));
                             finish();
                         } else {
                             Toast.makeText(this, "Registration Failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                         }
                     });
        });


        textViewLogin.setOnClickListener(v -> {
            Toast.makeText(this, "Login Text Clicked", Toast.LENGTH_SHORT).show(); // Debug line
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });

    }
}
