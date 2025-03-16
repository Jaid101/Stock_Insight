package com.example.stockinsight;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreferenceCompat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.android.material.appbar.MaterialToolbar;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Set up toolbar with back button
        MaterialToolbar toolbar = findViewById(R.id.settingsToolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        // Load the SettingsFragment properly
        if (savedInstanceState == null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.settings_container, new SettingsFragment());
            transaction.commit();
        }
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {

        private FirebaseAuth mAuth;

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.preferences, rootKey);

            // Theme Preference (Dark Mode)
            SwitchPreferenceCompat themePreference = findPreference("dark_mode");
            if (themePreference != null) {
                themePreference.setOnPreferenceChangeListener((preference, newValue) -> {
                    boolean isDarkMode = (boolean) newValue;
                    SharedPreferences.Editor editor = preference.getSharedPreferences().edit();
                    editor.putBoolean("dark_mode", isDarkMode);
                    editor.apply();

                    if (isDarkMode) {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    } else {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    }
                    return true;
                });
            }

            // Change Password Preference
            Preference changePasswordPref = findPreference("change_password");
            if (changePasswordPref != null) {
                changePasswordPref.setOnPreferenceClickListener(preference -> {
                    resetPassword();
                    return true;
                });
            }

            // About Developer Preference
            Preference aboutDeveloperPref = findPreference("about_developer");
            if (aboutDeveloperPref != null) {
                aboutDeveloperPref.setOnPreferenceClickListener(preference -> {
                    Intent intent = new Intent(getActivity(), AboutDeveloperActivity.class);
                    startActivity(intent);
                    return true;
                });
            }
        }

        // Firebase Password Reset
        private void resetPassword() {
            mAuth = FirebaseAuth.getInstance();
            FirebaseUser user = mAuth.getCurrentUser();
            if (user != null) {
                String email = user.getEmail();
                if (email != null && !email.isEmpty()) {
                    mAuth.sendPasswordResetEmail(email)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    Toast.makeText(getActivity(), "Password reset link sent to your email!", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(getActivity(), "Error! Email not found.", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }
        }
    }
}
