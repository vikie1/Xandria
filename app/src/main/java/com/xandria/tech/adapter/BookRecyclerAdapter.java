package com.xandria.tech.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.xandria.tech.R;
import com.xandria.tech.model.BookRecyclerModel;

import java.util.ArrayList;

public class BookRecyclerAdapter extends
        RecyclerView.Adapter<BookRecyclerAdapter.ViewHolder> {
    private final ArrayList<BookRecyclerModel> BookRecyclerModelArrayList;
    private final Context context;
    private final BookClickInterface bookClickInterface;

    public BookRecyclerAdapter(ArrayList<BookRecyclerModel> bookRecyclerModelArrayList,
                               Context context, BookClickInterface bookClickInterface) {
        BookRecyclerModelArrayList = bookRecyclerModelArrayList;
        this.context = context;
        this.bookClickInterface = bookClickInterface;
    }


    @NonNull
    @Override
    public BookRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.book_rv_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookRecyclerAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        BookRecyclerModel booksModel = BookRecyclerModelArrayList.get(position);
        holder.bookTitle.setText(booksModel.getTitle());
        holder.bookAuthor.setText(booksModel.getAuthors());
        Picasso.get().load(booksModel.getThumbnail()).into(holder.bookImage);
        holder.itemView.setOnClickListener(view -> bookClickInterface.onBookClick(position));

    }

    @Override
    public int getItemCount() {
        return BookRecyclerModelArrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private final TextView bookTitle;
        private final TextView bookAuthor;
                ImageView bookImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            bookTitle = itemView.findViewById(R.id.BookTitleTextView);
            bookAuthor = itemView.findViewById(R.id.BookAuthorNameTextView);
            bookImage = itemView.findViewById(R.id.BookImageView);
        }
    }
    public interface BookClickInterface {
        void onBookClick(int position);
    }
}
