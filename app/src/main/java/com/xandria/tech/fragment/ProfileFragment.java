package com.xandria.tech.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.text.HtmlCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.xandria.tech.R;
import com.xandria.tech.activity.user.LoginActivity;
import com.xandria.tech.constants.FirebaseRefs;
import com.xandria.tech.model.User;

import java.util.Objects;

public class ProfileFragment extends Fragment {
    DatabaseReference firebaseDatabaseReference;
    String userId;

    View view;
    Context context;
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_profile, container, false);

        firebaseDatabaseReference = FirebaseDatabase.getInstance().getReference(FirebaseRefs.USERS);
        userId = Objects.requireNonNull(Objects.requireNonNull(
                        FirebaseAuth.getInstance().getCurrentUser()).getEmail())
                .replaceAll("[\\-+. ^:,]","_");

        Button logoutBtn = view.findViewById(R.id.logout_btn);
        logoutBtn.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(context, LoginActivity.class));
        });

        getUser();
        return  view;
    }

    private void getUser() {
        firebaseDatabaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if (snapshot.getKey().equals(userId)) {
                    populateUser(Objects.requireNonNull(snapshot.getValue(User.class)));
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if (snapshot.getKey().equals(userId)) {
                    populateUser(Objects.requireNonNull(snapshot.getValue(User.class)));
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                if (snapshot.getKey().equals(userId)) {
                    populateUser(Objects.requireNonNull(snapshot.getValue(User.class)));
                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if (snapshot.getKey().equals(userId)) {
                    populateUser(Objects.requireNonNull(snapshot.getValue(User.class)));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context, "An error occurred while retrieving the details", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void populateUser(User value) {
        TextView usernameText = view.findViewById(R.id.display_username);
        TextView emailText = view.findViewById(R.id.display_email);
        TextView phoneText = view.findViewById(R.id.display_phone);
        TextView addressText = view.findViewById(R.id.display_address);

        usernameText.setText(HtmlCompat.fromHtml(
                getString(R.string.name).concat(" ").concat(value.getName()),
                HtmlCompat.FROM_HTML_MODE_COMPACT
        ));
        emailText.setText(HtmlCompat.fromHtml(
                getString(R.string.email).concat(" ").concat(value.getEmail()),
                HtmlCompat.FROM_HTML_MODE_COMPACT
        ));
        phoneText.setText(HtmlCompat.fromHtml(
                getString(R.string.phone).concat(" ").concat(value.getPhoneNumber()),
                HtmlCompat.FROM_HTML_MODE_COMPACT
        ));
        addressText.setText(HtmlCompat.fromHtml(
                getString(R.string.address).concat(" ").concat(value.getLocation().getAddress()),
                HtmlCompat.FROM_HTML_MODE_COMPACT
        ));
    }
}