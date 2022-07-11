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
import com.xandria.tech.model.OrdersModel;

import java.util.List;

public class OrdersListViewAdapter extends ArrayAdapter<OrdersModel> {

    public OrdersListViewAdapter(@NonNull Context context, @NonNull List<OrdersModel> objects) {
        super(context, 0, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        OrdersModel order = getItem(position);// get the order for this view
        if (convertView == null)
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_view_with_image, parent, false);
        TextView bookTitle = convertView.findViewById(R.id.order_book_id);
        TextView orderDate = convertView.findViewById(R.id.order_date);
        ImageView thumbnail = convertView.findViewById(R.id.book_image_in_list);

        bookTitle.setText(HtmlCompat.fromHtml(
                getContext().getString(R.string.book_title).concat(" ").concat(order.getBookTitle()),
                HtmlCompat.FROM_HTML_MODE_COMPACT
        ));
        orderDate.setText(HtmlCompat.fromHtml(
                getContext().getString(R.string.date_ordered).concat(" ").concat(order.getDateOrdered()),
                HtmlCompat.FROM_HTML_MODE_COMPACT
        ));
        Picasso.get().load(order.getBookImageUrl()).into(thumbnail);

        return convertView;
    }
}
