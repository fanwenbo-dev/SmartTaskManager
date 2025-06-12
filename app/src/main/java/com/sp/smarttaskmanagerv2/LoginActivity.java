package com.sp.smarttaskmanagerv2;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.auth.PhoneAuthOptions;

import java.util.concurrent.TimeUnit;

public class LoginActivity extends AppCompatActivity {

    private EditText phoneEditText, emailEditText, passwordEditText, otpEditText;
    private Button loginButton;
    private TextView signUpTextView;
    private FirebaseAuth auth;
    private String verificationId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance();

        // Check if user is already logged in
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            startActivity(new Intent(LoginActivity.this, HomePageActivity.class));
            finish();
        }

        // Initialize UI elements
        phoneEditText = findViewById(R.id.etLoginPhoneNumber);
        emailEditText = findViewById(R.id.etLoginPhoneOrEmail);
        passwordEditText = findViewById(R.id.etLoginPassword);
        otpEditText = findViewById(R.id.etLoginOtp);
        loginButton = findViewById(R.id.btnLogin);
        signUpTextView = findViewById(R.id.tvSignUp);

        // Navigate to SignUpActivity when "Sign Up" is clicked
        signUpTextView.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
            finish();
        });

        // Handle Login button click
        loginButton.setOnClickListener(v -> {
            String phone = phoneEditText.getText().toString().trim();
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();
            String otp = otpEditText.getText().toString().trim();

            // Validate inputs
            if (phone.isEmpty() && email.isEmpty() || password.isEmpty()) {
                Toast.makeText(LoginActivity.this, "Please enter phone/email and password", Toast.LENGTH_SHORT).show();
            } else if (otp.isEmpty() && !phone.isEmpty()) {
                // Proceed with Phone Number OTP verification
                sendVerificationCode(phone);
            } else if (!otp.isEmpty()) {
                verifyOtp(otp);
            } else {
                loginUser(email, password);
            }
        });
    }

    // Phone number verification function
    private void sendVerificationCode(String phoneNumber) {
        PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks =
                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                        // Automatically verify the OTP
                        signInWithPhoneAuthCredential(phoneAuthCredential);
                    }

                    @Override
                    public void onVerificationFailed(@NonNull com.google.firebase.FirebaseException e) {
                        Toast.makeText(LoginActivity.this, "Verification failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onCodeSent(@NonNull String verificationId,
                                           @NonNull PhoneAuthProvider.ForceResendingToken token) {
                        // Store verification ID to use it for OTP verification
                        LoginActivity.this.verificationId = verificationId;
                        Toast.makeText(LoginActivity.this, "OTP sent!", Toast.LENGTH_SHORT).show();
                    }
                };

        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(auth)
                        .setPhoneNumber(phoneNumber)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS)  // Timeout duration
                        .setActivity(this)                  // Activity (for callback binding)
                        .setCallbacks(callbacks)            // OnVerificationStateChangedCallbacks
                        .build();

        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    // Verify OTP and sign in
    private void verifyOtp(String otp) {
        if (verificationId != null) {
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, otp);
            signInWithPhoneAuthCredential(credential);
        } else {
            Toast.makeText(LoginActivity.this, "Verification ID is null", Toast.LENGTH_SHORT).show();
        }
    }

    // Sign in with Phone Auth Credential
    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = task.getResult().getUser();
                        Toast.makeText(LoginActivity.this, "Login successful!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(LoginActivity.this, HomePageActivity.class));
                        finish();
                    } else {
                        Toast.makeText(LoginActivity.this, "OTP verification failed", Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void loginUser(String email, String password) {
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        startActivity(new Intent(LoginActivity.this, HomePageActivity.class));
                        finish();
                    } else {
                        Toast.makeText(LoginActivity.this, "Login failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }
}
