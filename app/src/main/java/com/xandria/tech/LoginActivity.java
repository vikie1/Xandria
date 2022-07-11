package com.xandria.tech;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {


    private TextInputEditText email, password;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        email = findViewById(R.id.loginEmail);
        password = findViewById(R.id.loginPassword);
        Button loginBtn = findViewById(R.id.LoginBtn);
        TextView registerTextBtn = findViewById(R.id.RegisterTextBtn);
        mAuth = FirebaseAuth.getInstance();

        registerTextBtn.setOnClickListener(view -> startActivity(new Intent(LoginActivity.this, RegistrationActivity.class)));
        loginBtn.setOnClickListener(view -> {
            String emailStr = Objects.requireNonNull(email.getText()).toString();
            String passwordStr = Objects.requireNonNull(password.getText()).toString();
            if(TextUtils.isEmpty(emailStr) && TextUtils.isEmpty(passwordStr)){
                Toast.makeText(LoginActivity.this, "Enter your Credentials", Toast.LENGTH_SHORT).show();
            }else{
                mAuth.signInWithEmailAndPassword(emailStr, passwordStr).addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish();
                    }else{
                        Toast.makeText(LoginActivity.this, "Failed to Login", Toast.LENGTH_SHORT).show();
                    }
                });
            }

        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();
        if(user!=null){
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            this.finish();
        }
    }
}