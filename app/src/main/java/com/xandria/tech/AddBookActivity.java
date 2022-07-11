package com.xandria.tech;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.xandria.tech.adapter.GoogleBooksListViewAdapter;
import com.xandria.tech.constants.FirebaseRefs;
import com.xandria.tech.dto.Location;
import com.xandria.tech.model.BookRecyclerModel;
import com.xandria.tech.util.GoogleServices;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AddBookActivity extends AppCompatActivity {
    public static final String EXTRA_BOOK_REQUEST = "request";
    public static final String EXTRA_IS_MANUAL_INPUT = "manualInput";

    private TextInputEditText bookTitle, bookSubTitle, bookAuthorName, bookImageLink, bookDescription, bookPagesNo;
    private FirebaseDatabase fireDb;
    private DatabaseReference DbReference;

    GoogleBooksListViewAdapter googleBooksListViewAdapter;
    private boolean request;
    ViewSwitcher viewSwitcher;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_book);

        if (getIntent().hasExtra(EXTRA_IS_MANUAL_INPUT)
                && (boolean) getIntent().getExtras().get(EXTRA_IS_MANUAL_INPUT)
        ) {
            viewSwitcher = findViewById(R.id.google_books_manual_input_switch);
            viewSwitcher.setDisplayedChild(1);
            bookTitle = findViewById(R.id.bookTitle);
            bookSubTitle = findViewById(R.id.bookSubTitle);
            bookAuthorName = findViewById(R.id.authorName);
            bookImageLink = findViewById(R.id.bookImageLink);
            bookPagesNo = findViewById(R.id.PagesNo);
            bookDescription = findViewById(R.id.bookDescription);
            onAddBookClicked();
        } else allowSearch();

        fireDb = FirebaseDatabase.getInstance();
        if (getIntent().hasExtra(EXTRA_BOOK_REQUEST) && (boolean) getIntent().getExtras().get(EXTRA_BOOK_REQUEST)) {
            DbReference = fireDb.getReference(FirebaseRefs.BOOK_REQUESTS);
            request = true;
        }
        else DbReference = fireDb.getReference(FirebaseRefs.BOOKS);

        Button cancelButton = findViewById(R.id.addBookCancelBtn);
        cancelButton.setOnClickListener(view -> startActivity(new Intent(AddBookActivity.this, MainActivity.class)));
    }

    private void allowSearch() {
        EditText searchBar = findViewById(R.id.search_bar);
        ListView bookList = findViewById(R.id.google_books_list);
        List<BookRecyclerModel> booksList = new ArrayList<>();
        googleBooksListViewAdapter = new GoogleBooksListViewAdapter(this, booksList);
        bookList.setAdapter(googleBooksListViewAdapter);

        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                new Thread(() -> {
                    List<BookRecyclerModel> newList = null;
                    try {
                        newList = GoogleServices.searchBook(String.valueOf(s));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    List<BookRecyclerModel> finalNewList = newList;
                    runOnUiThread(() -> {
                        googleBooksListViewAdapter.clear();
                        if (finalNewList != null) {
                            googleBooksListViewAdapter.addAll(finalNewList);
                        }
                    });
                }).start();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        // get clicked book
        bookList.setOnItemClickListener((parent, view, position, id) -> {
            BookRecyclerModel googleBook = googleBooksListViewAdapter.getItem(position);
            googleBook.setUserId(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getEmail());
            addLocationToBook(googleBook);
        });
    }

    private void addLocationToBook(BookRecyclerModel book) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.location_input, null);
        builder.setTitle("Add Book Location");
        builder.setView(dialogView);
        builder.setPositiveButton("Finish", (dialog, which) -> {
            TextInputEditText streetAddress = dialogView.findViewById(R.id.street_address);
            TextInputEditText locality = dialogView.findViewById(R.id.locality);
            TextInputEditText city = dialogView.findViewById(R.id.city);
            TextInputEditText pinCode = dialogView.findViewById(R.id.pin_code);

            Location location = new Location(AddBookActivity.this,
                    String.valueOf(streetAddress.getText()),
                    String.valueOf(locality.getText()),
                    String.valueOf(city.getText()),
                    String.valueOf(pinCode.getText())
            );
            book.setLocation(location);

            saveBook(book);
        });
        builder.create().show();
    }

    private void saveBook(BookRecyclerModel book) {
        DbReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (request){ // request for existing books shouldn't go through
                    DatabaseReference reference = fireDb.getReference(FirebaseRefs.BOOKS).child(book.getBookID());
                    reference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (!snapshot.exists()){
                                DbReference.child(book.getBookID()).setValue(book);
                                Toast.makeText(AddBookActivity.this, "New Request Added..", Toast.LENGTH_SHORT).show();
                                onBackPressed();
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(AddBookActivity.this, "An error occurred", Toast.LENGTH_LONG).show();
                        }
                    });
                } else {
                    DbReference.child(book.getBookID()).setValue(book);
                    Toast.makeText(AddBookActivity.this, "Book Added..", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(AddBookActivity.this, MainActivity.class));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(AddBookActivity.this, "Failed to Add Book! Error: "+ error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    void onAddBookClicked(){
        Button addBookBtn = findViewById(R.id.addBookBtn);
        addBookBtn.setOnClickListener(view -> {
            String bookTitleStr = Objects.requireNonNull(bookTitle.getText()).toString();
            String bookSubTitleStr = Objects.requireNonNull(bookSubTitle.getText()).toString();
            String bookAuthorNameStr = Objects.requireNonNull(bookAuthorName.getText()).toString();
            String bookImageLinkStr = Objects.requireNonNull(bookImageLink.getText()).toString();
            String bookPagesNoStr = Objects.requireNonNull(bookPagesNo.getText()).toString();
            String bookDescriptionStr = Objects.requireNonNull(bookDescription.getText()).toString();
            BookRecyclerModel booksModel = new BookRecyclerModel(
                    bookTitleStr,bookSubTitleStr,bookAuthorNameStr,
                    bookDescriptionStr,bookPagesNoStr, bookImageLinkStr,
                    bookTitleStr.replaceAll("[\\-+.^:,]","_")
            );
            booksModel.setUserId(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getEmail());
            addLocationToBook(booksModel);
        });
    }
}