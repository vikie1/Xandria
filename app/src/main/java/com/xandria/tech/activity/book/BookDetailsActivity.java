package com.xandria.tech.activity.book;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.text.HtmlCompat;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;
import com.xandria.tech.activity.order.CreateOrderActivity;
import com.xandria.tech.MainActivity;
import com.xandria.tech.R;
import com.xandria.tech.model.BookRecyclerModel;

import java.util.Objects;

public class BookDetailsActivity extends AppCompatActivity {
    public static final String EXTRA_BOOK = "book";

    private BookRecyclerModel book;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_details);

        book = getIntent().getParcelableExtra(EXTRA_BOOK);
        if (book != null) populateBookView();
        handleButtonClicks();
    }

    private void handleButtonClicks() {
        Button bsViewPreviewsBtn = findViewById(R.id.viewPreviewBtn);
        Button bsEditBookBtn = findViewById(R.id.EditBookBtn);
        Button orderButton = findViewById(R.id.order_button);
        Button backToMain = findViewById(R.id.orderToMainPage);
        Button joinDiscussion = findViewById(R.id.joinDiscussion);

        if (Objects.equals(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getEmail(),
                book.getUserId())) { // you shouldn't order what you already own
            orderButton.setVisibility(View.GONE);
            bsEditBookBtn.setVisibility(View.VISIBLE);
        }
        else {
            orderButton.setVisibility(View.VISIBLE);
            bsEditBookBtn.setVisibility(View.GONE);
        }

        if (book.getPreviewLink() != null) {
            bsViewPreviewsBtn.setOnClickListener(view1 -> {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(book.getPreviewLink()));
                startActivity(i);
            });
        } else bsViewPreviewsBtn.setVisibility(View.GONE); // if there's no preview link, the button shouldn't be there

        bsEditBookBtn.setOnClickListener(view12 -> {
            Intent i = new Intent(BookDetailsActivity.this, EditBookActivity.class);
            i.putExtra("book", book);
            startActivity(i);
        });

        orderButton.setOnClickListener(v -> {
            Intent intent = new Intent(BookDetailsActivity.this, CreateOrderActivity.class);
            intent.putExtra(CreateOrderActivity.EXTRA_BOOK, book);
            startActivity(intent);
        });

        backToMain.setOnClickListener(view -> {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });

        joinDiscussion.setOnClickListener(view -> {
            Intent intent = new Intent(BookDetailsActivity.this, BookDiscussionActivity.class);
            intent.putExtra(BookDiscussionActivity.EXTRA_BOOK_ID, book.getBookID());
            intent.putExtra(BookDiscussionActivity.EXTRA_BOOK_TITLE, book.getTitle());
            startActivity(intent);
        });
    }

    private void populateBookView() {
        TextView bookTitle = findViewById(R.id.book_title);
        TextView author = findViewById(R.id.book_authors);
        TextView publisher = findViewById(R.id.book_publisher);
        TextView publishDate = findViewById(R.id.publish_date);
        TextView ISBN = findViewById(R.id.book_ISBN);
        TextView owner = findViewById(R.id.book_owner);
        TextView description = findViewById(R.id.book_description);
        ImageView bookImage = findViewById(R.id.book_image);

        bookTitle.setText(HtmlCompat.fromHtml(
                getString(R.string.book_title).concat(" ").concat(book.getTitle()),
                HtmlCompat.FROM_HTML_MODE_COMPACT
        ));
        if (book.getAuthors() != null) {
            author.setText(HtmlCompat.fromHtml(
                    getString(R.string.author_name).concat(" ").concat(book.getAuthors()),
                    HtmlCompat.FROM_HTML_MODE_COMPACT
            ));
        } else author.setText("");
        if (book.getPublisher() != null) {
            publisher.setText(HtmlCompat.fromHtml(
                    getString(R.string.publisher).concat(" ").concat(book.getPublisher()),
                    HtmlCompat.FROM_HTML_MODE_COMPACT
            ));
        } else publisher.setText("");
        if (book.getPublishedDate() != null) {
            publishDate.setText(HtmlCompat.fromHtml(
                    getString(R.string.publish_date).concat(" ").concat(book.getPublishedDate()),
                    HtmlCompat.FROM_HTML_MODE_COMPACT
            ));
        } else publishDate.setText("");
        if (book.getISBN() != null) {
            ISBN.setText(HtmlCompat.fromHtml(
                    getString(R.string.ISBN).concat(" ").concat(book.getISBN()),
                    HtmlCompat.FROM_HTML_MODE_COMPACT
            ));
        } else ISBN.setText("");
        if (book.getUserId() != null) {
            owner.setText(HtmlCompat.fromHtml(
                    getString(R.string.owner).concat(" ").concat(book.getUserId()),
                    HtmlCompat.FROM_HTML_MODE_COMPACT
            ));
        } else owner.setText("");
        if (book.getDescription() != null) {
            description.setText(book.getDescription());
        } else {
            TextView descriptionTitle = findViewById(R.id.description_title);
            descriptionTitle.setVisibility(View.GONE);
        }
        Picasso.get().load(book.getThumbnail()).into(bookImage);
    }
}