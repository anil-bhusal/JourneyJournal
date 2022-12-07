package com.ismt.journeyjournal.views;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.ismt.journeyjournal.R;
import com.ismt.journeyjournal.journal.Journal;
import com.ismt.journeyjournal.journal.JournalDashboard;
import com.ismt.journeyjournal.user.User;
import com.ismt.journeyjournal.user.UserRepo;

import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.content.Intent;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatTextView;

public class Register extends AppCompatActivity {
    private AppCompatTextView loginHere;
    private AppCompatEditText userName, txtPassword, txtCheckPassword, email;
    private Button btnRegister;
    private UserRepo reposatory;
    private AppCompatCheckBox chekBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        loginHere = findViewById(R.id.loginHere);
        email = findViewById(R.id.email);
        userName = findViewById(R.id.userName);
        txtPassword = findViewById(R.id.txtPassword);
        txtCheckPassword = findViewById(R.id.txtCheckPassword);
        reposatory = new UserRepo(this);
        btnRegister = findViewById(R.id.btnRegister);btnRegister = findViewById(R.id.btnRegister);
        chekBox = findViewById(R.id.showHidePwd);

        /*for Checkbox*/
        chekBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(chekBox.getText().toString().equals("Show Password")){
                    chekBox.setText("Hide Password");
                    txtPassword.setTransformationMethod(null);
                    txtCheckPassword.setTransformationMethod(null);
                }
                else {
                    chekBox.setText("Show Password");
                    txtPassword.setTransformationMethod(new PasswordTransformationMethod());
                    txtCheckPassword.setTransformationMethod(new PasswordTransformationMethod());
                }
            }
        });

        /*for Login*/
        loginHere.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Register.this, Login.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                userName.getText().clear();
                txtPassword.getText().clear();
                txtCheckPassword.getText().clear();
                startActivity(intent);
            }
        });

        /*for Register*/
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (view.getId() == R.id.btnRegister) {
                    if(registerUser()){
                        long check = reposatory.insertUser(
                                new User(
                                        userName.getText().toString(),
                                        0,
                                        email.getText().toString(),
                                        txtPassword.getText().toString(),
                                        txtCheckPassword.getText().toString()
                                ));

                        Log.e("inserted",check+"");
                        Intent i = new Intent(Register.this, Login.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        userName.getText().clear();
                        email.getText().clear();
                        txtPassword.getText().clear();
                        txtCheckPassword.getText().clear();
                        startActivity(i);
                    }
                }

            }
        });

    }

    /*for Password validation*/
    private boolean validatePassword() {
        String p = txtPassword.getText().toString();
        if (p.isEmpty()) {
            txtPassword.setError("Field cannot be empty");
            return false;
        } else if (p.length() < 6) {
            txtPassword.setError("Password must be minimum of 6 characters.");
            return false;
        } else {
            txtPassword.setError(null);
            return true;
        }
    }
    /*for Re-Password validation*/
    private boolean validateRePwd() {
        String re = txtCheckPassword.getText().toString();
        String p = txtCheckPassword.getText().toString();
        if (re.isEmpty()) {
            txtCheckPassword.setError("Field cannot be empty");
            return false;
        } else if (!re.equals(p)) {
            txtCheckPassword.setError("Password does not match.");
            return false;
        } else {
            txtCheckPassword.setError(null);
            return true;
        }
    }

    /*for email validation*/
    private boolean validateEmail() {
        String e = email.getText().toString();
        if (e.isEmpty()) {
            email.setError("Field cannot be empty");
            return false;
        } else if (!e.matches("[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                "\\@" +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                "(" +
                "\\." +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                ")+")) {
            email.setError("Please enter valid Email");
            return false;
        } else {
            email.setError(null);
            return true;
        }
    }

    /*for username validation */
    private boolean validateUsername() {
        String e = userName.getText().toString();
        if (e.isEmpty()) {
            userName.setError("Field cannot be empty");
            return false;
        } else if (!e.matches("[a-zA-Z0-9._-]{1,16}")) {
            userName.setError("Please enter valid Email");
            return false;
        } else {
            userName.setError(null);
            return true;
        }
    }

    /* For Register User*/
    private boolean registerUser(){
        String u = userName.getText().toString();
        String pass = txtPassword.getText().toString();
        String passCheck = txtCheckPassword.getText().toString();
        if(!validateUsername() || !validateEmail() || !validatePassword()  || !validateRePwd()){
            return false;
        }else if(!pass.equals(passCheck)){
            txtCheckPassword.setError("Password does not match!");
            return false;
        }else{
            Toast.makeText(Register.this, "Signup Successfully", Toast.LENGTH_SHORT).show();
            return true;
        }
    }
}