package com.xandria.tech.activity.book;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.xandria.tech.R;
import com.xandria.tech.adapter.DiscussionChatsAdapter;
import com.xandria.tech.constants.FirebaseRefs;
import com.xandria.tech.model.DiscussionModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class BookDiscussionActivity extends AppCompatActivity {
    public static final String EXTRA_BOOK_ID = "bookId";
    public static final String EXTRA_BOOK_TITLE = "bookTitle";

    List<DiscussionModel> discussionModelList;
    DiscussionChatsAdapter discussionChatsAdapter;
    DatabaseReference firebaseDatabaseReference;

    private String bookId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_discussion);

        bookId = (String) getIntent().getExtras().get(EXTRA_BOOK_ID);
        String bookTitle = (String) getIntent().getExtras().get(EXTRA_BOOK_TITLE);

        TextView bookTitleView = findViewById(R.id.book_title);
        bookTitleView.setSelected(true);
        bookTitleView.setText(bookTitle);

        firebaseDatabaseReference = FirebaseDatabase.getInstance().getReference(FirebaseRefs.BOOK_DISCUSSION);

        discussionModelList = new ArrayList<>();
        discussionChatsAdapter = new DiscussionChatsAdapter(this, discussionModelList);
        ListView chatsListView = findViewById(R.id.chats_list);
        chatsListView.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        chatsListView.setAdapter(discussionChatsAdapter);

        onMessageSendAction();
        getChats();
    }

    private void getChats() {
        firebaseDatabaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if (Objects.equals(snapshot.getKey(), bookId))
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
        EditText messageEditText = findViewById(R.id.chat_input);
        ImageButton sendButton = findViewById(R.id.chat_send_btn);

        sendButton.setOnClickListener(v -> {
            if (messageEditText.getText().toString().trim().equals("")){
                messageEditText.requestFocus();
            } else {
                DiscussionModel discussion = new DiscussionModel();
                discussion.setSender(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getEmail());
                discussion.setMessage(messageEditText.getText().toString());
                discussion.setTimeSent();

                firebaseDatabaseReference.child(bookId).setValue(discussion);
                Toast.makeText(BookDiscussionActivity.this, "Message sent", Toast.LENGTH_SHORT).show();
                messageEditText.setText("");
            }
        });
    }
}