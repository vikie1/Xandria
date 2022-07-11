package com.xandria.tech.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.text.HtmlCompat;

import com.squareup.picasso.Picasso;
import com.xandria.tech.R;
import com.xandria.tech.model.BookRecyclerModel;

import java.util.List;

public class GoogleBooksListViewAdapter extends ArrayAdapter<BookRecyclerModel> {

    public GoogleBooksListViewAdapter(@NonNull Context context, @NonNull List<BookRecyclerModel> objects) {
        super(context, 0, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        BookRecyclerModel book = getItem(position);
        if (convertView == null)
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.larger_pic_with_some_description, parent, false);

        ImageView imageView = convertView.findViewById(R.id.image_of_the_book);
        TextView titleView = convertView.findViewById(R.id.lv_book_title);
        TextView authorView = convertView.findViewById(R.id.lv_book_author);
        TextView descView = convertView.findViewById(R.id.lv_book_description);

        titleView.setText(HtmlCompat.fromHtml(
                getContext().getString(R.string.book_title).concat(" ").concat(book.getTitle()),
                HtmlCompat.FROM_HTML_MODE_COMPACT
        ));
        if (book.getAuthors() != null)
            authorView.setText(HtmlCompat.fromHtml(
                    getContext().getString(R.string.author_name).concat(" ").concat(book.getAuthors()),
                    HtmlCompat.FROM_HTML_MODE_COMPACT
            ));
        else authorView.setText("");
        if (book.getDescription() != null)
            descView.setText(HtmlCompat.fromHtml(
                    getContext().getString(R.string.description).concat(" ").concat(book.getDescription()),
                    HtmlCompat.FROM_HTML_MODE_COMPACT
            ));
        else descView.setText("");
        Picasso.get().load(book.getThumbnail()).into(imageView);

        return convertView;
    }
}
