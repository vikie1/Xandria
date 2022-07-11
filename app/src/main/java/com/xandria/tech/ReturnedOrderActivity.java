package com.xandria.tech;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.text.HtmlCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;
import com.xandria.tech.constants.FirebaseRefs;
import com.xandria.tech.model.ReturnOrdersModel;

public class ReturnedOrderActivity extends AppCompatActivity {
    public static final String EXTRA_RETURN_ORDER = "Returned order";

    ReturnOrdersModel returnOrder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_returned_order);

        returnOrder = getIntent().getParcelableExtra(EXTRA_RETURN_ORDER);
        if (returnOrder != null) populateOrderView();
        handleButtonClicks();
    }

    private void populateOrderView() {
        TextView bookTitle = findViewById(R.id.orderedBookTitle);
        TextView bookCollectionLocation = findViewById(R.id.orderedBookCollectionLocation);
        TextView dateOrdered = findViewById(R.id.orderedBookDateOrdered);
        TextView orderedFrom = findViewById(R.id.orderedBookHostLocationUserId);
        ImageView bookImage = findViewById(R.id.ordered_book_image);

        // HtmlCompat allow us to add html in strings in this case to make part of it bold
        bookTitle.setText(HtmlCompat.fromHtml(
                getString(R.string.book_title).concat(" ").concat(returnOrder.getBookTitle()),
                HtmlCompat.FROM_HTML_MODE_COMPACT
        ));
        bookCollectionLocation.setText(HtmlCompat.fromHtml(
                getString(R.string.collection_location).concat(" ").concat(returnOrder.getPickUpLocation().getAddress()),
                HtmlCompat.FROM_HTML_MODE_COMPACT
        ));
        orderedFrom.setText(HtmlCompat.fromHtml(
                getString(R.string.ordered_from).concat(" ").concat(returnOrder.getHostId()),
                HtmlCompat.FROM_HTML_MODE_COMPACT
        ));
        dateOrdered.setText(HtmlCompat.fromHtml(
                getString(R.string.address).concat(" ").concat(returnOrder.getReturnAddress().getAddress()),
                HtmlCompat.FROM_HTML_MODE_COMPACT
        ));
        Picasso.get().load(returnOrder.getBookThumbNailUrl()).into(bookImage);
    }

    private void handleButtonClicks() {
        Button backToMain = findViewById(R.id.orderToMainPage);
        Button joinDiscussion = findViewById(R.id.joinDiscussion);

        backToMain.setOnClickListener(view -> {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });

        joinDiscussion.setOnClickListener(view -> {
            Intent intent = new Intent(ReturnedOrderActivity.this, BookDiscussionActivity.class);
            intent.putExtra(BookDiscussionActivity.EXTRA_BOOK_TITLE, returnOrder.getBookTitle());
            intent.putExtra(BookDiscussionActivity.EXTRA_BOOK_ID, returnOrder.getBookId());
            startActivity(intent);
        });

    }
}