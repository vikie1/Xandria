package com.xandria.tech.constants;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.xandria.tech.model.User;

import java.util.Objects;

public class LoggedInUser {
    private static LoggedInUser loggedInUser;
    private User currentUser;
    private Context context;

    public static LoggedInUser getInstance(Context... context){
        if (loggedInUser == null) loggedInUser = new LoggedInUser();
        if (context.length > 0) loggedInUser.setContext(context[0]);
        return loggedInUser;
    }

    public void init(){
        updateUser();
    }

    private void updateUser(){
        DatabaseReference firebaseDatabaseReference = FirebaseDatabase.getInstance().getReference(FirebaseRefs.USERS);
        String userId = Objects.requireNonNull(Objects.requireNonNull(
                        FirebaseAuth.getInstance().getCurrentUser()).getEmail())
                .replaceAll("[\\-+. ^:,]","_");
        getUser(firebaseDatabaseReference, userId);
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public Context getContext() {
        return context;
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }

    private LoggedInUser(){ }

    private void getUser(DatabaseReference firebaseDatabaseReference, String userId) {
        firebaseDatabaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if (snapshot.getKey().equals(userId)) {
                    setCurrentUser(Objects.requireNonNull(snapshot.getValue(User.class)));
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if (snapshot.getKey().equals(userId)) {
                    setCurrentUser(Objects.requireNonNull(snapshot.getValue(User.class)));
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                if (snapshot.getKey().equals(userId)) {
                    setCurrentUser(Objects.requireNonNull(snapshot.getValue(User.class)));
                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if (snapshot.getKey().equals(userId)) {
                    setCurrentUser(Objects.requireNonNull(snapshot.getValue(User.class)));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context, "An error occurred while retrieving the details", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
