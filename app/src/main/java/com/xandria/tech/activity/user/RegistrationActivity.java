package com.xandria.tech.activity.user;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.xandria.tech.R;
import com.xandria.tech.constants.FirebaseRefs;
import com.xandria.tech.model.User;

import java.util.Objects;

public class RegistrationActivity extends AppCompatActivity {

    private TextInputEditText username, email, password, confirmPwd, phone;
    private ProgressBar PB;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        username = findViewById(R.id.regUserName);
        email = findViewById(R.id.regEmail);
        phone = findViewById(R.id.regPhone);
        password = findViewById(R.id.regPassword);
        confirmPwd = findViewById(R.id.regConfirmPassword);
        Button registerBtn = findViewById(R.id.RegisterBtn);
        TextView loginTxtBtn = findViewById(R.id.LoginTextBtn);
        PB = findViewById(R.id.loadingPB);
        mAuth = FirebaseAuth.getInstance();

        loginTxtBtn.setOnClickListener(view -> startActivity(new Intent(RegistrationActivity.this, LoginActivity.class)));

        registerBtn.setOnClickListener(view -> {
            PB.setVisibility(View.VISIBLE);
            String userNameStr = Objects.requireNonNull(username.getText()).toString();
            String emailStr = Objects.requireNonNull(email.getText()).toString();
            String passwordStr = Objects.requireNonNull(password.getText()).toString();
            String confirmPwdStr = Objects.requireNonNull(confirmPwd.getText()).toString();
            if(!passwordStr.equals(confirmPwdStr)){
                Toast.makeText(RegistrationActivity.this, "Please Enter Matching Passwords", Toast.LENGTH_SHORT).show();
            }else if(TextUtils.isEmpty(userNameStr )
                    && TextUtils.isEmpty(emailStr) &&
                    TextUtils.isEmpty(passwordStr) && TextUtils.isEmpty(confirmPwdStr)){
                Toast.makeText(RegistrationActivity.this, "Enter All Fields", Toast.LENGTH_SHORT).show();
            }else{
                mAuth.createUserWithEmailAndPassword(emailStr, passwordStr).addOnCompleteListener(RegistrationActivity.this, task -> {
                    if(task.isSuccessful()){
                        PB.setVisibility(View.GONE);
                        setUpUser();
                        Toast.makeText(RegistrationActivity.this, "User Registered", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(RegistrationActivity.this, LoginActivity.class));
                        finish();
                    }else{
                        PB.setVisibility(View.GONE);
                        Toast.makeText(RegistrationActivity.this, "Failed to Register User", Toast.LENGTH_SHORT).show();
                        Log.w(TAG, "createUserWithEmail:failure", task.getException());
                    }
                });
            }
        });
    }

    private void setUpUser() {
        String email = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getEmail();
        String userId = email != null ? email.replaceAll("[\\-+. ^:,]", "_") : null;
        User user = new User(
                Objects.requireNonNull(username.getText()).toString(),
                userId,
                email,
                Objects.requireNonNull(phone.getText()).toString()
        );

        DatabaseReference userDBRef = FirebaseDatabase.getInstance().getReference(FirebaseRefs.USERS);
        userDBRef.child(user.getUserId()).setValue(user);
    }
}