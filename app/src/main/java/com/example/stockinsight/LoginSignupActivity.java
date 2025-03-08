package com.example.stockinsight;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.identity.SignInCredential;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;

import java.util.Objects;

public class LoginSignupActivity extends AppCompatActivity {

    private EditText emailInput, passwordInput, confirmPasswordInput;
    private Button emailSignInButton, switchModeButton, resetPasswordButton;
    private TextView titleText;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;
    private boolean isLoginMode = true; // Track login/signup mode

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize Firebase Auth
        FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();

        // Check if user is already signed in
        FirebaseUser currentUser = mAuth.getCurrentUser();
        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        boolean isLoggedIn = prefs.getBoolean("isLoggedIn", false);

        if (currentUser != null || isLoggedIn) {
            navigateToMain();
            return;
        }

        setContentView(R.layout.activity_login_signup);

        // UI elements
        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        confirmPasswordInput = findViewById(R.id.etConfirmPassword);
        SignInButton googleSignInButton = findViewById(R.id.googleSignInButton);
        emailSignInButton = findViewById(R.id.emailSignInButton);
        switchModeButton = findViewById(R.id.switchModeButton);
        resetPasswordButton = findViewById(R.id.btnResetPassword);
        titleText = findViewById(R.id.titleText);
        progressBar = findViewById(R.id.progressBar);

        progressBar.setVisibility(View.GONE);
        confirmPasswordInput.setVisibility(View.GONE); // Hide by default
        resetPasswordButton.setVisibility(View.GONE); // Hide in sign-up mode

        // Toggle between Login & Sign-Up
        switchModeButton.setOnClickListener(v -> {
            isLoginMode = !isLoginMode;
            if (isLoginMode) {
                titleText.setText("Login to Stock Insight");
                switchModeButton.setText("New here? Sign Up");
                confirmPasswordInput.setVisibility(View.GONE);
                resetPasswordButton.setVisibility(View.VISIBLE);
            } else {
                titleText.setText("Create an Account");
                switchModeButton.setText("Already have an account? Login");
                confirmPasswordInput.setVisibility(View.VISIBLE);
                resetPasswordButton.setVisibility(View.GONE);
            }
        });

        // Email & Password Authentication
        emailSignInButton.setOnClickListener(v -> {
            if (!isInternetAvailable()) {
                Toast.makeText(this, "Please connect to the internet!", Toast.LENGTH_SHORT).show();
                return;
            }

            String email = emailInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();
            String confirmPassword = confirmPasswordInput.getText().toString().trim();

            if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                Toast.makeText(this, "Enter email and password", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!isLoginMode) {
                if (TextUtils.isEmpty(confirmPassword)) {
                    Toast.makeText(this, "Confirm your password", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!password.equals(confirmPassword)) {
                    Toast.makeText(this, "Passwords do not match!", Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            if (isLoginMode) {
                loginWithEmail(email, password);
            } else {
                signUpWithEmail(email, password);
            }
        });

        // Google Sign-In
        googleSignInButton.setOnClickListener(v -> signInWithGoogle());

        // Reset Password
        resetPasswordButton.setOnClickListener(v -> resetPassword());
    }


    private void loginWithEmail(String email, String password) {
        progressBar.setVisibility(View.VISIBLE);
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    progressBar.setVisibility(View.GONE);
                    if (task.isSuccessful()) {
                        saveLoginState();
                        navigateToMain();
                    } else {
                        if (task.getException() instanceof FirebaseAuthInvalidUserException) {
                            Toast.makeText(this, "User does not exist! Please sign up.", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(this, "Login Failed!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void signUpWithEmail(String email, String password) {
        progressBar.setVisibility(View.VISIBLE);

        mAuth.fetchSignInMethodsForEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && Objects.requireNonNull(task.getResult().getSignInMethods()).size() > 0) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(this, "User already exists! Please log in.", Toast.LENGTH_SHORT).show();
                    } else {
                        // Proceed with sign-up if user doesn't exist
                        mAuth.createUserWithEmailAndPassword(email, password)
                                .addOnCompleteListener(signUpTask -> {
                                    progressBar.setVisibility(View.GONE);
                                    if (signUpTask.isSuccessful()) {
                                        saveLoginState();
                                        navigateToMain();
                                    } else {
                                        if (signUpTask.getException() instanceof FirebaseAuthUserCollisionException) {
                                            Toast.makeText(this, "User already exists!", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(this, "Sign-Up Failed!", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    }
                });
    }

    private void signInWithGoogle() {
        Intent signInIntent = new Intent(this, GoogleSignInActivity.class);
        startActivity(signInIntent);
    }

    private void resetPassword() {
        String email = emailInput.getText().toString().trim();
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Enter your registered email!", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    progressBar.setVisibility(View.GONE);
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Password reset link sent to your email!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Error! Email not found.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void saveLoginState() {
        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("isLoggedIn", true);
        editor.apply();
    }

    private void navigateToMain() {
        startActivity(new Intent(LoginSignupActivity.this, DashboardActivity.class));
        finish();
    }

    private boolean isInternetAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            return activeNetwork != null && activeNetwork.isConnected();
        }
        return false;
    }
}
