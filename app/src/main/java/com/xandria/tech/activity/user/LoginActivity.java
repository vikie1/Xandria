package com.xandria.tech.activity.user;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.xandria.tech.MainActivity;
import com.xandria.tech.R;
import com.xandria.tech.constants.LoggedInUser;

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
        TextView resetTextBtn = findViewById(R.id.reset_password_txt);
        mAuth = FirebaseAuth.getInstance();

        registerTextBtn.setOnClickListener(view -> startActivity(new Intent(LoginActivity.this, RegistrationActivity.class)));
        resetTextBtn.setOnClickListener(v -> showResetDialog());
        loginBtn.setOnClickListener(view -> {
            String emailStr = Objects.requireNonNull(email.getText()).toString();
            String passwordStr = Objects.requireNonNull(password.getText()).toString();
            if(TextUtils.isEmpty(emailStr) && TextUtils.isEmpty(passwordStr)){
                Toast.makeText(LoginActivity.this, "Enter your Credentials", Toast.LENGTH_SHORT).show();
            }else{
                mAuth.signInWithEmailAndPassword(emailStr, passwordStr).addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                        LoggedInUser.getInstance(this).init();
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish();
                    }else{
                        Toast.makeText(LoginActivity.this, "Failed to Login", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void showResetDialog() {
        Dialog dialog = new Dialog(LoginActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.request_email_layout);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        TextInputEditText emailInput = dialog.findViewById(R.id.password_rst_email);
        Button confirm = dialog.findViewById(R.id.password_reset_btn);

        confirm.setOnClickListener(v -> {
            FirebaseAuth auth = FirebaseAuth.getInstance();
            String emailAddress = emailInput.getText() == null ? "" : emailInput.getText().toString();

            if (emailAddress.isEmpty()) {
                Toast.makeText(LoginActivity.this, "Enter a valid email", Toast.LENGTH_LONG).show();
                return;
            }
            auth.sendPasswordResetEmail(emailAddress).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(LoginActivity.this, "Check your email inbox", Toast.LENGTH_LONG).show();
                    dialog.dismiss();
                }
                else Toast.makeText(LoginActivity.this, "Error " + task.getException().getLocalizedMessage(), Toast.LENGTH_LONG).show();
            });
        });

        dialog.show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();
        if(user!=null){
            LoggedInUser.getInstance(this).init();
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            this.finish();
        }
    }
}