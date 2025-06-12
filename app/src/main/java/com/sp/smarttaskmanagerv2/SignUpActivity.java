package com.sp.smarttaskmanagerv2;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignUpActivity extends AppCompatActivity {

    private EditText phoneEditText, emailEditText, passwordEditText;
    private Button signUpButton;
    private TextView alreadyHaveAccountTextView;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance();

        // Initialize UI elements
        phoneEditText = findViewById(R.id.etSignupPhone);
        emailEditText = findViewById(R.id.etSignupEmail);
        passwordEditText = findViewById(R.id.etSignupPassword);
        signUpButton = findViewById(R.id.btnSignup);
        alreadyHaveAccountTextView = findViewById(R.id.tvAlreadyHaveAccount);

        // Navigate to LoginActivity when "Already have an account?" is clicked
        alreadyHaveAccountTextView.setOnClickListener(v -> {
            startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
            finish(); // Close signup screen
        });

        // Handle Sign Up button click
        signUpButton.setOnClickListener(v -> {
            String phone = phoneEditText.getText().toString().trim();
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();

            if (phone.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(SignUpActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            } else if (password.length() < 6) {
                Toast.makeText(SignUpActivity.this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
            } else {
                registerUser(phone, email, password);
            }
        });
    }

    private void registerUser(String phone, String email, String password) {
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = auth.getCurrentUser();
                        // Optionally, save the phone number to the user's profile
                        // FirebaseUser user = auth.getCurrentUser();
                        // user.updatePhoneNumber(phone); // This can be done later using Firebase Phone Authentication

                        Toast.makeText(SignUpActivity.this, "Signup successful!", Toast.LENGTH_SHORT).show();

                        // Navigate to HomePageActivity after successful signup
                        startActivity(new Intent(SignUpActivity.this, HomePageActivity.class));
                        finish();
                    } else {
                        Toast.makeText(SignUpActivity.this, "Signup failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }
}
