package com.xandria.tech.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;
import com.xandria.tech.R;
import com.xandria.tech.activity.order.CreateOrderActivity;
import com.xandria.tech.model.BookRecyclerModel;

import java.util.ArrayList;
import java.util.Objects;

public class BookRecyclerAdapter extends RecyclerView.Adapter<BookRecyclerAdapter.ViewHolder> {
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
    public void onBindViewHolder(@NonNull BookRecyclerAdapter.ViewHolder holder, int position) {
        BookRecyclerModel booksModel = BookRecyclerModelArrayList.get(position);
        holder.bookTitle.setText(booksModel.getTitle());
        holder.bookAuthor.setText(booksModel.getAuthors());
        Picasso.get().load(booksModel.getThumbnail()).into(holder.bookImage);
        if (Objects.equals(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getEmail(),
                booksModel.getUserId())) { // you shouldn't order what you already own
            holder.orderButton.setVisibility(View.GONE);
        }
        else {
            holder.orderButton.setVisibility(View.VISIBLE);
        }
        holder.orderButton.setOnClickListener(v -> {
            Intent intent = new Intent(context, CreateOrderActivity.class);
            intent.putExtra(CreateOrderActivity.EXTRA_BOOK, booksModel);
            context.startActivity(intent);
        });
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
        Button orderButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            bookTitle = itemView.findViewById(R.id.BookTitleTextView);
            bookAuthor = itemView.findViewById(R.id.BookAuthorNameTextView);
            bookImage = itemView.findViewById(R.id.BookImageView);
            orderButton = itemView.findViewById(R.id.order_button);
        }
    }
    public interface BookClickInterface {
        void onBookClick(int position);
    }
}
