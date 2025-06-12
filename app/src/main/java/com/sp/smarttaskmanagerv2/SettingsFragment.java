package com.sp.smarttaskmanagerv2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import com.google.firebase.auth.FirebaseAuth;

public class SettingsFragment extends Fragment {

    private SharedPreferences sharedPreferences;
    private static final String PREFS_NAME = "SettingsPrefs";
    private static final String KEY_THEME = "theme_mode";
    private static final String KEY_NOTIFICATIONS = "notifications_enabled";
    private static final String KEY_LOCATION = "location_enabled";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        sharedPreferences = requireContext().getSharedPreferences(PREFS_NAME, 0);

        initializeThemeSettings(view);
        initializeNotificationSettings(view);
        initializeLocationSettings(view);
        setupChangePasswordButton(view);
        setupLogoutButton(view);
        setAppVersion(view);

        return view;
    }

    private void initializeThemeSettings(View view) {
        RadioGroup themeRadioGroup = view.findViewById(R.id.themeRadioGroup);
        int savedTheme = sharedPreferences.getInt(KEY_THEME, AppCompatDelegate.MODE_NIGHT_NO);

        if (savedTheme == AppCompatDelegate.MODE_NIGHT_YES) {
            ((RadioButton) view.findViewById(R.id.radioDark)).setChecked(true);
        } else {
            ((RadioButton) view.findViewById(R.id.radioLight)).setChecked(true);
        }

        themeRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            int selectedTheme = AppCompatDelegate.MODE_NIGHT_NO;
            if (checkedId == R.id.radioDark) {
                selectedTheme = AppCompatDelegate.MODE_NIGHT_YES;
            }

            AppCompatDelegate.setDefaultNightMode(selectedTheme);
            sharedPreferences.edit().putInt(KEY_THEME, selectedTheme).apply();
            requireActivity().recreate();
        });
    }

    private void initializeNotificationSettings(View view) {
        Switch notificationSwitch = view.findViewById(R.id.switchNotifications);
        boolean notificationsEnabled = sharedPreferences.getBoolean(KEY_NOTIFICATIONS, true);
        notificationSwitch.setChecked(notificationsEnabled);

        notificationSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            sharedPreferences.edit().putBoolean(KEY_NOTIFICATIONS, isChecked).apply();
            // Implement your notification toggle logic here
        });
    }

    private void initializeLocationSettings(View view) {
        Switch locationSwitch = view.findViewById(R.id.switchLocation);
        boolean locationEnabled = sharedPreferences.getBoolean(KEY_LOCATION, false);
        locationSwitch.setChecked(locationEnabled);

        locationSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            sharedPreferences.edit().putBoolean(KEY_LOCATION, isChecked).apply();
            // Implement your location services toggle logic here
        });
    }

    private void setupChangePasswordButton(View view) {
        Button changePasswordButton = view.findViewById(R.id.btnChangePassword);
        changePasswordButton.setOnClickListener(v -> {
            // Start ChangePassword Activity
            startActivity(new Intent(requireActivity(), ChangePasswordActivity.class));
        });
    }

    private void setupLogoutButton(View view) {
        Button logoutButton = view.findViewById(R.id.btnLogout);
        logoutButton.setOnClickListener(v -> logoutUser());
    }

    private void setAppVersion(View view) {
        // If you want to dynamically set version, you can use:
        // String versionName = BuildConfig.VERSION_NAME;
        // TextView versionText = view.findViewById(R.id.txtVersion);
        // versionText.setText("V" + versionName);
    }

    private void logoutUser() {
        FirebaseAuth.getInstance().signOut();

        // Clear preferences on logout
        sharedPreferences.edit().clear().apply();

        Intent intent = new Intent(getActivity(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}
