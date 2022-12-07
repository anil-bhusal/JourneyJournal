package com.ismt.journeyjournal;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.*;
import android.content.SharedPreferences;
import android.content.Context;
import android.os.Bundle;

import com.ismt.journeyjournal.journal.JournalDashboard;
import com.ismt.journeyjournal.views.Login;

public class SplashScreen extends AppCompatActivity {
        /*Splash screen timer*/
    private static int SPLASH_SCREEN = 2000;
    private SharedPreferences sharedPreference;
    boolean check = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        sharedPreference = getSharedPreferences("journal_preference", Context.MODE_PRIVATE);
        boolean check = sharedPreference.getBoolean("User_login", false);


        new Handler().postDelayed(() -> {
           /* check whether user is login or not*/
            if (check) {
                startActivity(new Intent(this, JournalDashboard.class));
            }
            else{
                startActivity(new Intent(this, Login.class));
            }

            /*close this activity*/
            finish();
        }, SPLASH_SCREEN);
    }

}