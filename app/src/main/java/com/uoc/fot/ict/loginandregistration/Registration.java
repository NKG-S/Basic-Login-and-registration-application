package com.uoc.fot.ict.loginandregistration;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FieldValue;

import java.util.HashMap;
import java.util.Map;

public class Registration extends AppCompatActivity {

    private static final String TAG = "EmailPassword";
    private FirebaseAuth mAuth;  // FirebaseAuth instance
    private FirebaseFirestore db;  // Firestore instance

    private TextInputEditText nameEditText, emailEditText, passwordEditText, addressEditText, mobileEditText;
    private MaterialButton registerButton;
    private TextView signInTextView;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        // Initialize Firebase instances
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();  // Initialize Firestore

        // Initialize views
        nameEditText = findViewById(R.id.nameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        addressEditText = findViewById(R.id.addressEditText);
        mobileEditText = findViewById(R.id.mobileEditText);
        registerButton = findViewById(R.id.registerButton);
        signInTextView = findViewById(R.id.signInTextView);
        progressBar = findViewById(R.id.progressBar);

        // Register button click listener
        registerButton.setOnClickListener(v -> {
            progressBar.setVisibility(View.VISIBLE);
            setRegisterButton();
        });

        // Sign in click listener
        signInTextView.setOnClickListener(v -> {
            Intent intent = new Intent(Registration.this, Login.class);
            startActivity(intent);
            finish();
        });
    }

    private void setRegisterButton() {
        String name = nameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String address = addressEditText.getText().toString().trim();
        String mobile = mobileEditText.getText().toString().trim();

        if (name.isEmpty() || email.isEmpty() || password.isEmpty() || address.isEmpty() || mobile.isEmpty()) {
            Toast.makeText(Registration.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
        } else {
            // Check if password meets complexity requirements
            String passwordValidationResult = isPasswordValid(password);

            if (!passwordValidationResult.equals("Password is valid")) {
                Toast.makeText(Registration.this, passwordValidationResult, Toast.LENGTH_SHORT).show();
            } else {
                createAccount(email, password, name, address, mobile);  // Pass email, password, and user info correctly
            }
        }
    }

    // Password validation logic: check for minimum length and complexity requirements
    private String isPasswordValid(String password) {
        // Check each condition and return corresponding message for missing requirements
        if (password.length() < 8) {
            return "Password must be at least 6 characters long.";
        } else if (!password.matches(".*[a-z].*")) {
            return "Password must contain at least one lowercase letter.";
        } else if (!password.matches(".*[A-Z].*")) {
            return "Password must contain at least one uppercase letter.";
        } else if (!password.matches(".*[0-9].*")) {
            return "Password must contain at least one digit.";
        } else if (!password.matches(".*[!@#$%^&*(),.?\":{}|<>].*")) {
            return "Password must contain at least one special character.";
        }

        // If all conditions are satisfied
        return "Password is valid";
    }

    // Method to create a user and save additional data to Firestore
    private void createAccount(String email, String password, String name, String address, String mobile) {
        // Create Firebase Authentication account
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    progressBar.setVisibility(View.GONE);

                    if (task.isSuccessful()) {
                        Log.d(TAG, "createUserWithEmail:success");
                        FirebaseUser user = mAuth.getCurrentUser();

                        // Create a map of user data to save in Firestore
                        Map<String, Object> userData = new HashMap<>();
                        userData.put("name", name);
                        userData.put("email", email);
                        userData.put("address", address);
                        userData.put("mobile", mobile);
                        userData.put("createdAt", FieldValue.serverTimestamp());

                        // Save user data to Firestore
                        if (user != null) {
                            db.collection("users").document(user.getUid())
                                    .set(userData)
                                    .addOnSuccessListener(aVoid -> {
                                        Log.d(TAG, "User data successfully written!");
                                        Toast.makeText(Registration.this, "Registration successful", Toast.LENGTH_SHORT).show();
                                        // Redirect to main activity or login
                                        startActivity(new Intent(Registration.this, MainActivity.class));
                                        finish();
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.w(TAG, "Error writing user data", e);
                                        Toast.makeText(Registration.this, "Registration successful but failed to save profile data", Toast.LENGTH_SHORT).show();
                                    });
                        }
                    } else {
                        Log.w(TAG, "createUserWithEmail:failure", task.getException());
                        Toast.makeText(Registration.this, "Authentication failed: " +
                                task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
