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
import com.xandria.tech.model.ReturnOrdersModel;

import java.util.List;

public class ReturnedOrdersListViewAdapter extends ArrayAdapter<ReturnOrdersModel> {

    public ReturnedOrdersListViewAdapter(@NonNull Context context, @NonNull List<ReturnOrdersModel> objects) {
        super(context, 0, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ReturnOrdersModel returnOrders = getItem(position);
        if (convertView == null)
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_view_with_image, parent, false);

        TextView bookTitle = convertView.findViewById(R.id.order_book_id);
        TextView orderDate = convertView.findViewById(R.id.order_date);
        ImageView thumbnail = convertView.findViewById(R.id.book_image_in_list);

        bookTitle.setText(HtmlCompat.fromHtml(
                getContext().getString(R.string.book_title).concat(" ").concat(returnOrders.getBookTitle()),
                HtmlCompat.FROM_HTML_MODE_COMPACT
        ));
        orderDate.setText(HtmlCompat.fromHtml(
                getContext().getString(R.string.ordered_from).concat(" ").concat(returnOrders.getHostId()),
                HtmlCompat.FROM_HTML_MODE_COMPACT
        ));
        Picasso.get().load(returnOrders.getBookThumbNailUrl()).into(thumbnail);

        return convertView;
    }
}
