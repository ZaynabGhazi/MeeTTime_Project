package com.zaynab.meettime;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class LoginActivity extends AppCompatActivity {

    public static final String TAG = "LoginActivity";
    private TextInputEditText mEtUsername;
    private TextInputEditText mEtPassword;
    private MaterialButton mBtnLogin;
    private MaterialButton mBtnSignup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();
        //user persistence
        if (ParseUser.getCurrentUser() != null) goMainActivity();
        bindView();
        make_login();
        make_signup();
    }

    private void make_signup() {
        mBtnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "SignUp button clicked");
                String username = mEtUsername.getText().toString();
                String password = mEtPassword.getText().toString();
                signupUser(username, password);
            }
        });
    }

    private void make_login() {
        mBtnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "Login button clicked");
                String username = mEtUsername.getText().toString();
                String password = mEtPassword.getText().toString();
                loginUser(username, password);
            }
        });

    }

    private void bindView() {
        mEtUsername = findViewById(R.id.etUsername);
        mEtPassword = findViewById(R.id.etPassword);
        mBtnLogin = findViewById(R.id.btnLogin);
        mBtnSignup = findViewById(R.id.btnSignup);
    }

    private void signupUser(String username, String password) {
        Log.i(TAG, "Attempt to signup user " + username);
        ParseUser user = new ParseUser();
        user.setUsername(username);
        user.setPassword(password);
        user.signUpInBackground(new SignUpCallback() {
            public void done(ParseException e) {
                if (e == null) {
                    goMainActivity();
                    Toast.makeText(LoginActivity.this, "Sign-up successful!", Toast.LENGTH_SHORT).show();
                    user.put("startAvailability", "16:00 16:00 16:00 16:00 16:00 10:00 10:00");
                    user.put("endAvailability", "22:00 22:00 22:00 22:00 22:00 22:00 22:00");

                } else {
                    Log.e(TAG, "Issue with sign-up.", e);
                    Toast.makeText(LoginActivity.this, "Username or password does not match criteria.", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });

    }

    private void loginUser(String username, String password) {
        Log.i(TAG, "Attempt to login user " + username);
        //goTo Main if login successful
        ParseUser.logInInBackground(username, password, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue with login.", e);
                    Toast.makeText(LoginActivity.this, "Username or password incorrect.", Toast.LENGTH_SHORT).show();
                    return;
                }
                goMainActivity();
                Toast.makeText(LoginActivity.this, "Login successful!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void goMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}