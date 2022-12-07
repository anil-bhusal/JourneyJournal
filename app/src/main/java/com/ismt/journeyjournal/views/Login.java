package com.ismt.journeyjournal.views;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.content.Intent;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.appcompat.widget.AppCompatButton;
import androidx.room.Query;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.ismt.journeyjournal.JounrneyDatabase;
import com.ismt.journeyjournal.R;
import com.ismt.journeyjournal.journal.JournalDashboard;
import com.ismt.journeyjournal.user.User;
import com.ismt.journeyjournal.user.UserDao;
import com.ismt.journeyjournal.user.UserRepo;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Login extends AppCompatActivity {
    private Button btnLogin;
    private AppCompatTextView registerHere;
    private AppCompatEditText txtPassword, email;
    private UserRepo userRepo;
    private AppCompatCheckBox chekBox;
    private SharedPreferences sharedPreferences;
    private  AppCompatButton btnGoogleLogin;

    GoogleSignInOptions gso;
    GoogleSignInClient gsc;

    /*Called when the activity is first created. */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        /*getSharedPreferences is use for multiple shared preference files identified*/
      sharedPreferences = getSharedPreferences("journal_preference", Context.MODE_PRIVATE);

        userRepo = new UserRepo(this);
        txtPassword = findViewById(R.id.txtPassword);
        email = findViewById(R.id.email);
        registerHere = findViewById(R.id.registerHere);
        chekBox = findViewById(R.id.showHidePwd);
        btnLogin=findViewById(R.id.btnLogin);
        btnGoogleLogin=findViewById(R.id.btnGoogleLogin);

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        gsc = GoogleSignIn.getClient(this,gso);

        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
        if (acct != null) {
            navigateToDashboard();
        }

        /*for Googlelogin*/
        btnGoogleLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });

        /*for CheckBox*/
        chekBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("User_login", true).apply();

                if(chekBox.getText().toString().equals("Show Password")){
                    chekBox.setText("Hide Password");
                    txtPassword.setTransformationMethod(null);
                }
                else {
                    chekBox.setText("Show Password");
                    txtPassword.setTransformationMethod(new PasswordTransformationMethod());
                }
            }
        });

        /*for Register*/
        /*When we click on registerbtn it will go on  login page  */
        registerHere.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Login.this, Register.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                email.getText().clear();
                txtPassword.getText().clear();
                startActivity(intent);
            }
        });

        /*for Login*/
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String e = email.getText().toString();
                String p = txtPassword.getText().toString();
                if(!validateEmail() || !validatePassword()){
                    return;
                }/*else if(!validate(u)){
                    Toast.makeText(Login.this, "Please enter correct user name", Toast.LENGTH_SHORT).show();
                }*/else{
                    JounrneyDatabase db = JounrneyDatabase.getInstance(getApplicationContext());
                    final UserDao userDao = db.userDao();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            User user = userRepo.checkAuth(e, p);
                            if(user == null){
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        email.setError("Invalid Credentials");
                                        txtPassword.setError("Invalid Credentials");
                                        Toast.makeText(Login.this, "Invalid Credentials", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }else{
                                String name = user.getUserName();
                                startActivity(new Intent(Login.this, JournalDashboard.class)
                                                                .putExtra("name", name)
                                                                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                                                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));

                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putBoolean("User_login", true).apply();
                                email.getText().clear();
                                txtPassword.setText("");
                            }
                       }

                    }).start();
                    Toast.makeText(Login.this, "Hurry! Login Successful", Toast.LENGTH_SHORT).show();
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        /*Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);*/
        if (requestCode == 1000) {
            /*The Task returned from this call is always completed, no need to attach a listener.*/
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try{
                task.getResult(ApiException.class);
                navigateToDashboard();
                Toast.makeText(Login.this, "Hurry! Login Successful", Toast.LENGTH_SHORT).show();
            }catch(ApiException e){
                Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
            }

        }
    }

    void signIn(){
        Intent signInIntent = gsc.getSignInIntent();
        startActivityForResult(signInIntent, 1000);
    }

    void navigateToDashboard(){
        finish();
        Intent intent = new Intent(Login.this, JournalDashboard.class);
        startActivity(intent);
    }

}