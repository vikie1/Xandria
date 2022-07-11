package com.xandria.tech;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.xandria.tech.adapter.DiscussionChatsAdapter;
import com.xandria.tech.constants.FirebaseRefs;
import com.xandria.tech.model.DiscussionModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DiscussionFragment extends Fragment {
    List<DiscussionModel> discussionModelList;
    DiscussionChatsAdapter discussionChatsAdapter;
    DatabaseReference firebaseDatabaseReference;

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
        view = inflater.inflate(R.layout.fragment_discussion, container, false);
        firebaseDatabaseReference = FirebaseDatabase.getInstance().getReference(FirebaseRefs.DISCUSSIONS);

        discussionModelList = new ArrayList<>();
        discussionChatsAdapter = new DiscussionChatsAdapter(context, discussionModelList);
        ListView chatsListView = view.findViewById(R.id.chats_list);
        chatsListView.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        chatsListView.setAdapter(discussionChatsAdapter);

        onMessageSendAction();
        getChats();
        return view;
    }

    private void getChats() {
        firebaseDatabaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                discussionChatsAdapter.add(snapshot.getValue(DiscussionModel.class));
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                discussionChatsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                discussionChatsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                discussionChatsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                discussionChatsAdapter.notifyDataSetChanged();
            }
        });
    }

    public void onMessageSendAction() {
        EditText messageEditText = view.findViewById(R.id.chat_input);
        ImageButton sendButton = view.findViewById(R.id.chat_send_btn);

        sendButton.setOnClickListener(v -> {
            if (messageEditText.getText().toString().trim().equals("")){
                messageEditText.requestFocus();
            } else {
                DiscussionModel discussion = new DiscussionModel();
                discussion.setSender(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getEmail());
                discussion.setMessage(messageEditText.getText().toString());
                discussion.setTimeSent();

                firebaseDatabaseReference.push().setValue(discussion);
                Toast.makeText(context, "Message sent", Toast.LENGTH_SHORT).show();
                messageEditText.setText("");
            }
        });
    }
}