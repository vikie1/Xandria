package com.xandria.tech.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.xandria.tech.R;

import java.util.List;

public class CategoriesRecyclerAdapter extends RecyclerView.Adapter<CategoriesRecyclerAdapter.ViewHolder>{
    private final List<String> categories;
    private final Context context;

    public CategoriesRecyclerAdapter(Context context, List<String> categories) {
        this.context = context;
        this.categories = categories;
    }

    @NonNull
    @Override
    public CategoriesRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.simple_list, parent, false);
        return new CategoriesRecyclerAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String category = categories.get(position);
        holder.categoryView.setText(category);
        holder.categoryView.setOnClickListener(v -> Toast.makeText(v.getContext(), category + " clicked", Toast.LENGTH_SHORT).show());
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private final TextView categoryView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryView = itemView.findViewById(R.id.simple_list_text);
        }
    }
}
