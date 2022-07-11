package com.xandria.tech.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.xandria.tech.R;

import java.util.List;

public class CustomArrayAdapter extends ArrayAdapter<String> {

    public CustomArrayAdapter(@NonNull Context context, @NonNull List<String> objects) {
        super(context, 0, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        String text = getItem(position);
        if (convertView == null)
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.simple_list, parent, false);

        TextView textView = convertView.findViewById(R.id.simple_list_text);
        textView.setText(text);
        return convertView;
    }
}
