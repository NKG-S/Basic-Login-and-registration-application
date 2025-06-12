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

public class Registration extends AppCompatActivity {

    private static final String TAG = "EmailPassword";
    private FirebaseAuth mAuth;  // FirebaseAuth instance
    private TextInputEditText nameEditText, emailEditText, passwordEditText, addressEditText, mobileEditText;
    private MaterialButton registerButton;
    private TextView signInTextView;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);  // Only one call to super.onCreate()
        setContentView(R.layout.activity_registration);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

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
                createAccount(email, password);  // Pass email and password correctly
            }
        }
    }

    // Password validation logic: check for minimum length and complexity requirements
    private String isPasswordValid(String password) {
        // Check each condition and return corresponding message for missing requirements
        if (password.length() < 6) {
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

    private void createAccount(String email, String password) {
        // [START create_user_with_email]
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            progressBar.setVisibility(View.GONE);
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(Registration.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        // [END create_user_with_email]
    }
}