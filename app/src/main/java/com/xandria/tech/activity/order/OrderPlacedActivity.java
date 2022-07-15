package com.xandria.tech.activity.order;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.text.HtmlCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.xandria.tech.MainActivity;
import com.xandria.tech.R;
import com.xandria.tech.activity.book.BookDiscussionActivity;
import com.xandria.tech.constants.FirebaseRefs;
import com.xandria.tech.constants.LoggedInUser;
import com.xandria.tech.model.OrdersModel;
import com.xandria.tech.model.ReturnOrdersModel;
import com.xandria.tech.model.User;
import com.xandria.tech.util.DateUtils;

import java.time.LocalDateTime;

public class OrderPlacedActivity extends AppCompatActivity {
    public static final String EXTRA_ORDER = "order";
    public static final String EXTRA_IS_MY_BOOK = "True if is for current user, false if borrowed";

    OrdersModel order;
    boolean isMyBook;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_placed);

        order = getIntent().getParcelableExtra(EXTRA_ORDER);
        isMyBook = (boolean) getIntent().getExtras().get(EXTRA_IS_MY_BOOK);
        if (order != null) populateOrderView();
        handleButtonClicks();
    }

    private void populateOrderView() {
        TextView bookTitle = findViewById(R.id.orderedBookTitle);
        TextView bookCollectionLocation = findViewById(R.id.orderedBookCollectionLocation);
        TextView dateOrdered = findViewById(R.id.orderedBookDateOrdered);
        TextView orderedFrom = findViewById(R.id.orderedBookHostLocationUserId);
        TextView status = findViewById(R.id.order_status);
        ImageView bookImage = findViewById(R.id.ordered_book_image);
        Button returnBook = findViewById(R.id.btn_return_book);
        Button confirmReception = findViewById(R.id.btn_confirm_book_received);

        if (isMyBook) returnBook.setVisibility(View.GONE); // only borrowed books are to be returned
        if (isMyBook && !order.isReturned()) {
            confirmReception.setVisibility(View.GONE); // can only confirm reception if borrower has returned
            status.setText(HtmlCompat.fromHtml(
                    getString(R.string.order_status).concat(" ").concat("Completed"),
                    HtmlCompat.FROM_HTML_MODE_COMPACT
            ));
        }

        if (!isMyBook && order.isReturned()) {
            returnBook.setVisibility(View.GONE); // no need to display if book has been returned
            status.setText(HtmlCompat.fromHtml(
                    getString(R.string.return_status).concat(" ").concat("Pending"),
                    HtmlCompat.FROM_HTML_MODE_COMPACT
            ));
        }
        if (!isMyBook && !order.isBorrowConfirmed()) {
            returnBook.setVisibility(View.GONE); // Can't return if borrow isn't confirmed
            status.setText(HtmlCompat.fromHtml(
                    getString(R.string.order_status).concat(" ").concat("Awaiting confirmation"),
                    HtmlCompat.FROM_HTML_MODE_COMPACT
            ));
        }
        if (!isMyBook && order.isBorrowConfirmed() && !order.isReturned()) {
            confirmReception.setVisibility(View.GONE); // if reception is confirmed no deed to show button
            status.setText(HtmlCompat.fromHtml(
                    getString(R.string.order_status).concat(" ").concat("Completed"),
                    HtmlCompat.FROM_HTML_MODE_COMPACT
            ));
        }

        // HtmlCompat allow us to add html in strings in this case to make part of it bold
        bookTitle.setText(HtmlCompat.fromHtml(
                getString(R.string.book_title).concat(" ").concat(order.getBookTitle()),
                HtmlCompat.FROM_HTML_MODE_COMPACT
        ));
        bookCollectionLocation.setText(HtmlCompat.fromHtml(
                getString(R.string.collection_location).concat(" ").concat(order.getDropLocation().getAddress()),
                HtmlCompat.FROM_HTML_MODE_COMPACT
        ));
        orderedFrom.setText(HtmlCompat.fromHtml(
                getString(R.string.ordered_from).concat(" ").concat(order.getHostLocationUserId()),
                HtmlCompat.FROM_HTML_MODE_COMPACT
        ));
        String dateAndTime = "";

        try {
            LocalDateTime localDateTime = LocalDateTime.parse(order.getDateOrdered());
            dateAndTime = DateUtils.getDateTimeString(localDateTime);
        } catch (Exception e){
            e.printStackTrace();
        }

        if (dateAndTime.trim().equals(""))
            dateOrdered.setText(HtmlCompat.fromHtml(
                    getString(R.string.date_ordered).concat(" ").concat(order.getDateOrdered()),
                    HtmlCompat.FROM_HTML_MODE_COMPACT
            ));
        else
            dateOrdered.setText(HtmlCompat.fromHtml(
                getString(R.string.date_ordered).concat(" ").concat(dateAndTime),
                HtmlCompat.FROM_HTML_MODE_COMPACT
            ));
        Picasso.get().load(order.getBookImageUrl()).into(bookImage);
    }

    private void handleButtonClicks() {
        Button backToMain = findViewById(R.id.orderToMainPage);
        Button joinDiscussion = findViewById(R.id.joinDiscussion);
        Button returnBook = findViewById(R.id.btn_return_book);
        Button confirmReception = findViewById(R.id.btn_confirm_book_received);

        backToMain.setOnClickListener(view -> {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });

        joinDiscussion.setOnClickListener(view -> {
            Intent intent = new Intent(OrderPlacedActivity.this, BookDiscussionActivity.class);
            intent.putExtra(BookDiscussionActivity.EXTRA_BOOK_TITLE, order.getBookTitle());
            intent.putExtra(BookDiscussionActivity.EXTRA_BOOK_ID, order.getBookId());
            startActivity(intent);
        });

        if (isMyBook){
            confirmReception.setOnClickListener(View -> {
                ReturnOrdersModel returnedOrders = new ReturnOrdersModel(
                        "return_" + order.getOrderId(), // prepend return_ to order id to get return id
                        order.getOrderId(),
                        order.getHostAddress(), // should be returned to owner
                        order.getHostLocationUserId(),
                        order.getBookTitle(),
                        order.getDropLocation(),
                        order.getBuyerUserId(),
                        order.getBookId(),
                        order.getBookImageUrl()
                );

                DatabaseReference firebaseDatabaseRef = FirebaseDatabase.getInstance().getReference(FirebaseRefs.RETURNED_ORDERS);
                firebaseDatabaseRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        firebaseDatabaseRef.child(returnedOrders.getReturnId()).child(returnedOrders.getBookId()).setValue(returnedOrders);
                        Toast.makeText(OrderPlacedActivity.this, "Return Confirmed", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(OrderPlacedActivity.this, "An error occurred", Toast.LENGTH_LONG).show();
                    }
                });

                // return borrowers points
                User user = LoggedInUser.getInstance().getCurrentUser();
                DatabaseReference userDBRef = FirebaseDatabase
                        .getInstance()
                        .getReference(FirebaseRefs.USERS);
                userDBRef.child(user.getUserId()).child("points").setValue(
                        user.getPoints() + order.getBookValue()
                );

                // decrement hosts points
                String hostUserId = order.getHostLocationUserId().replaceAll("[\\-+. ^:,]","_");
                userDBRef = FirebaseDatabase
                        .getInstance()
                        .getReference(FirebaseRefs.USERS);

                userDBRef.child(hostUserId)
                        .child("points")
                        .runTransaction(new Transaction.Handler() {
                            @NonNull
                            @Override
                            public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                                Double points = currentData.getValue(Double.class);
                                if (points == null) currentData.setValue(0 - order.getBookValue());
                                else currentData.setValue(points - order.getBookValue());
                                return Transaction.success(currentData);
                            }

                            @Override
                            public void onComplete(@Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot currentData) {

                            }
                        });

                // now delete the order
                DatabaseReference firebaseDatabaseRefOrders = FirebaseDatabase.getInstance().getReference(FirebaseRefs.ORDERS);
                firebaseDatabaseRefOrders.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        firebaseDatabaseRefOrders.child(order.getOrderId()).child(order.getBookId()).removeValue();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                onBackPressed();
            });
        } else {
            confirmReception.setOnClickListener(view -> {
                DatabaseReference firebaseDatabaseRef = FirebaseDatabase.getInstance().getReference(FirebaseRefs.ORDERS);
                firebaseDatabaseRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        firebaseDatabaseRef
                                .child(order.getOrderId())
                                .child(order.getBookId())
                                .child("borrowConfirmed")
                                .setValue(true);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
                onBackPressed();
            });
        }

        returnBook.setOnClickListener(view -> {
            DatabaseReference firebaseDatabaseRef = FirebaseDatabase.getInstance().getReference(FirebaseRefs.ORDERS);
            firebaseDatabaseRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    firebaseDatabaseRef
                            .child(order.getOrderId())
                            .child(order.getBookId())
                            .child("returned")
                            .setValue(true);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            onBackPressed();
        });
    }

}