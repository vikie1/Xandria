package com.xandria.tech.dialogues;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.text.HtmlCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;
import com.xandria.tech.MainActivity;
import com.xandria.tech.R;
import com.xandria.tech.activity.book.BookDiscussionActivity;
import com.xandria.tech.activity.book.EditBookActivity;
import com.xandria.tech.model.BookRecyclerModel;

import java.util.Objects;

public class BookDetails{
    public Dialog createDialogue(Context context, BookRecyclerModel book){
        Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.book_details_layout);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(context.getResources().getColor(R.color.black)));

        populateBookView(book, dialog, context);
        handleButtonClicks(book,dialog, context);
        return dialog;
    }

    private void populateBookView(BookRecyclerModel book, Dialog dialog, Context context) {
        TextView bookTitle = dialog.findViewById(R.id.book_title);
        TextView author = dialog.findViewById(R.id.book_authors);
        TextView publisher = dialog.findViewById(R.id.book_publisher);
        TextView publishDate = dialog.findViewById(R.id.publish_date);
        TextView ISBN = dialog.findViewById(R.id.book_ISBN);
        TextView owner = dialog.findViewById(R.id.book_owner);
        TextView description = dialog.findViewById(R.id.book_description);
        ImageView bookImage = dialog.findViewById(R.id.book_image);

        bookTitle.setText(HtmlCompat.fromHtml(
                context.getString(R.string.book_title).concat(" ").concat(book.getTitle()),
                HtmlCompat.FROM_HTML_MODE_COMPACT
        ));
        if (book.getAuthors() != null) {
            author.setText(HtmlCompat.fromHtml(
                    context.getString(R.string.author_name).concat(" ").concat(book.getAuthors()),
                    HtmlCompat.FROM_HTML_MODE_COMPACT
            ));
        } else author.setText("");
        if (book.getPublisher() != null) {
            publisher.setText(HtmlCompat.fromHtml(
                    context.getString(R.string.publisher).concat(" ").concat(book.getPublisher()),
                    HtmlCompat.FROM_HTML_MODE_COMPACT
            ));
        } else publisher.setText("");
        if (book.getPublishedDate() != null) {
            publishDate.setText(HtmlCompat.fromHtml(
                    context.getString(R.string.publish_date).concat(" ").concat(book.getPublishedDate()),
                    HtmlCompat.FROM_HTML_MODE_COMPACT
            ));
        } else publishDate.setText("");
        if (book.getISBN() != null) {
            ISBN.setText(HtmlCompat.fromHtml(
                    context.getString(R.string.ISBN).concat(" ").concat(book.getISBN()),
                    HtmlCompat.FROM_HTML_MODE_COMPACT
            ));
        } else ISBN.setText("");
        if (book.getUserId() != null) {
            owner.setText(HtmlCompat.fromHtml(
                    context.getString(R.string.owner).concat(" ").concat(book.getUserId()),
                    HtmlCompat.FROM_HTML_MODE_COMPACT
            ));
        } else owner.setText("");
        if (book.getDescription() != null) {
            description.setText(book.getDescription());
        } else {
            TextView descriptionTitle = dialog.findViewById(R.id.description_title);
            descriptionTitle.setVisibility(View.GONE);
        }
        Picasso.get().load(book.getThumbnail()).into(bookImage);
    }

    private void handleButtonClicks(BookRecyclerModel book, Dialog dialog, Context context) {
        Button bsViewPreviewsBtn = dialog.findViewById(R.id.viewPreviewBtn);
        Button bsEditBookBtn = dialog.findViewById(R.id.EditBookBtn);
        Button orderButton = dialog.findViewById(R.id.order_button);
        Button backToMain = dialog.findViewById(R.id.orderToMainPage);
        Button joinDiscussion = dialog.findViewById(R.id.joinDiscussion);

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
                context.startActivity(i);
            });
        } else bsViewPreviewsBtn.setVisibility(View.GONE); // if there's no preview link, the button shouldn't be there

        bsEditBookBtn.setOnClickListener(view12 -> {
            Intent i = new Intent(context, EditBookActivity.class);
            i.putExtra("book", book);
            context.startActivity(i);
        });

        orderButton.setOnClickListener(v -> new CreateOrder(context, book));

        backToMain.setOnClickListener(view -> {
            context.startActivity(new Intent(context, MainActivity.class));
        });

        joinDiscussion.setOnClickListener(view -> {
            Intent intent = new Intent(context, BookDiscussionActivity.class);
            intent.putExtra(BookDiscussionActivity.EXTRA_BOOK_ID, book.getBookID());
            intent.putExtra(BookDiscussionActivity.EXTRA_BOOK_TITLE, book.getTitle());
            context.startActivity(intent);
        });
    }
}
