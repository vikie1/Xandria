package com.xandria.tech.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.xandria.tech.R;
import com.xandria.tech.model.DiscussionModel;

import java.util.List;
import java.util.Objects;

public class DiscussionChatsAdapter extends ArrayAdapter<DiscussionModel> {

    public DiscussionChatsAdapter(@NonNull Context context, @NonNull List<DiscussionModel> objects) {
        super(context, 0, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        DiscussionModel discussion = getItem(position);

        if (convertView == null)
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.chats_layout, parent, false);

        LinearLayout receivedLayout = convertView.findViewById(R.id.received_layout);
        LinearLayout sentLayout = convertView.findViewById(R.id.sent_layout);
        TextView received = convertView.findViewById(R.id.received_chat);
        TextView sent = convertView.findViewById(R.id.sent_chat);

        receivedLayout.setVisibility(View.VISIBLE);
        sentLayout.setVisibility(View.VISIBLE);
        if (discussion.getSender().equals(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getEmail())){
            receivedLayout.setVisibility(View.GONE);
            sent.setText(discussion.getMessage());
        } else {
            sentLayout.setVisibility(View.GONE);
            received.setText(discussion.getMessage());
        }

        return convertView;
    }
}
