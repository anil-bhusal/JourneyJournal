package com.ismt.journeyjournal;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.widget.AppCompatButton;

import com.ismt.journeyjournal.views.Login;
import com.ismt.journeyjournal.views.Register;

public class MainActivity extends AppCompatActivity {

    private AppCompatButton cRegister, cLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cLogin = findViewById(R.id.cLogin);
        cRegister = findViewById(R.id.cRegister);

        cLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, Login.class));
            }
        });

        cRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, Register.class));
            }
        });

    }
}