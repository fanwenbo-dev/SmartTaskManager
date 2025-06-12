package com.sp.smarttaskmanagerv2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);  // Your splash screen layout

        // Play splash screen music

        // Check if the user is logged in
        SharedPreferences preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        boolean isLoggedIn = preferences.getBoolean("isLoggedIn", false);

        // Wait for 2 seconds, then navigate to the appropriate screen
        new Handler().postDelayed(() -> {
            if (isLoggedIn) {
                startActivity(new Intent(MainActivity.this, HomePageActivity.class));
            } else {
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
            }
            finish();  // Close the MainActivity
        }, 2000); // 2000 milliseconds = 2 seconds
    }

}
