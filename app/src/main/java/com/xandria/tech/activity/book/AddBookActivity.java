package com.xandria.tech.activity.book;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.xandria.tech.MainActivity;
import com.xandria.tech.R;
import com.xandria.tech.adapter.GoogleBooksListViewAdapter;
import com.xandria.tech.constants.FirebaseRefs;
import com.xandria.tech.dto.Location;
import com.xandria.tech.model.BookRecyclerModel;
import com.xandria.tech.util.GPSTracker;
import com.xandria.tech.util.GoogleServices;
import com.xandria.tech.util.Points;

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

    //    ViewSwitcher viewSwitcher;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_book);

        if (getIntent().hasExtra(EXTRA_IS_MANUAL_INPUT)
                && (boolean) getIntent().getExtras().get(EXTRA_IS_MANUAL_INPUT)
        ) {
//            viewSwitcher = findViewById(R.id.google_books_manual_input_switch);
//            viewSwitcher.setDisplayedChild(1);
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
        } else DbReference = fireDb.getReference(FirebaseRefs.BOOKS);

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

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().trim().isEmpty()) {
                    googleBooksListViewAdapter.clear();
                    return; // all characters are deleted so remove everything
                }
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
        });

        // get clicked book
        bookList.setOnItemClickListener((parent, view, position, id) -> {
            BookRecyclerModel googleBook = googleBooksListViewAdapter.getItem(position);
            googleBook.setUserId(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getEmail());
            if (!request) addBookValue(googleBook); //
        });
    }

    private void addLocationToBook(BookRecyclerModel book) {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.location_input_mode);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.black)));

        handleChoiceDialogueButtonClicks(dialog, book);
        dialog.show();
    }


    private void saveBook(BookRecyclerModel book) {
        if (request) { // request for existing books shouldn't go through
            DatabaseReference reference = fireDb.getReference(FirebaseRefs.BOOKS).child(book.getBookID());
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (!snapshot.exists()) {
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
            AddBookActivity.this.finish();
        }
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
            if (!request) addBookValue(booksModel);
        });
    }

    private void handleChoiceDialogueButtonClicks(Dialog dialog, BookRecyclerModel book) {
        ImageButton cancelButton = dialog.findViewById(R.id.cancel_button);
        Button useCurrent = dialog.findViewById(R.id.use_current);
        Button useNew = dialog.findViewById(R.id.use_preferred);

        cancelButton.setOnClickListener(v -> {
            dialog.dismiss();
            Toast.makeText(AddBookActivity.this, "Location is needed to save book", Toast.LENGTH_LONG).show();
        });
        useCurrent.setOnClickListener(v -> {
            Location location = getCurrentLoc();
            if (location != null) {
                book.setLocation(location);
                saveBook(book);
            }
            else Toast.makeText(AddBookActivity.this, "Location permissions are needed to proceed", Toast.LENGTH_LONG).show();
        });
        useNew.setOnClickListener(v ->{
            dialog.dismiss();
            switchToManualLocationEntry(book);
        });
    }

    private void switchToManualLocationEntry(BookRecyclerModel book) {
        Dialog dialog = new Dialog(AddBookActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.location_input);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.black)));

        TextInputEditText streetAddress = dialog.findViewById(R.id.street_address);
        TextInputEditText locality = dialog.findViewById(R.id.locality);
        TextInputEditText city = dialog.findViewById(R.id.city);
        TextInputEditText pinCode = dialog.findViewById(R.id.pin_code);
        Button orderButton = dialog.findViewById(R.id.add_location);
        ImageButton cancelButton = dialog.findViewById(R.id.cancel_button);

        cancelButton.setOnClickListener(v -> {
            dialog.dismiss();
            Toast.makeText(AddBookActivity.this, "Location is needed to save book", Toast.LENGTH_LONG).show();
        });
        orderButton.setOnClickListener(v -> {
            Location location = new Location(AddBookActivity.this,
                    String.valueOf(streetAddress.getText()),
                    String.valueOf(locality.getText()),
                    String.valueOf(city.getText()),
                    String.valueOf(pinCode.getText())
            );
            book.setLocation(location);
            saveBook(book);
        });

        dialog.show();
    }

    private Location getCurrentLoc() {
        GPSTracker gpsTracker = new GPSTracker(this);
        if(!gpsTracker.getIsGPSTrackingEnabled()) {
            gpsTracker.showSettingsAlert();
            gpsTracker.stopUsingGPS();
        } else {
            gpsTracker = new GPSTracker(this);
            Location location = new Location(
                    gpsTracker.getAddressLine(),
                    gpsTracker.getLongitude(),
                    gpsTracker.getLatitude()
            );
            location.setLocality(gpsTracker.getLocality());
            location.setStreetAddress(gpsTracker.getStreet());
            location.setPinCode(gpsTracker.getPostalCode());
            location.setCity(gpsTracker.getCity());
            return location;
        }
        return null;
    }

    private void addBookValue(BookRecyclerModel book){
        Dialog dialog = new Dialog(AddBookActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.value_input_dialogue);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.black)));

        ImageButton cancelButton = dialog.findViewById(R.id.cancel_button);
        TextView positivePart = dialog.findViewById(R.id.positive_number_input);
        TextView floatPart = dialog.findViewById(R.id.float_part_input);
        Button saveButton = dialog.findViewById(R.id.get_value_btn);

        cancelButton.setOnClickListener(v -> {
            dialog.dismiss();
            Toast.makeText(AddBookActivity.this, "Book value is needed to proceed", Toast.LENGTH_LONG).show();
        });
        saveButton.setOnClickListener(v -> {
            String positiveText = positivePart.getText().toString();
            String floatText = floatPart.getText().toString();
            if (positiveText.trim().isEmpty()) positiveText = "00";
            if (floatText.trim().isEmpty()) floatText = "00";
            long positive = Integer.parseInt(positiveText);
            int floatNumber = Integer.parseInt(floatText);
            if ((positive >= 0) || (floatNumber >= 0)){
                if (floatNumber > 99) Toast.makeText(AddBookActivity.this, "Only use 2 decimal places", Toast.LENGTH_LONG).show();
                else {
                    double value = Double.parseDouble(positive + "." + floatNumber);
                    book.setValue(Points.rupeesToPoints(value));
                    dialog.dismiss();
                    addLocationToBook(book);
                }
            } else Toast.makeText(AddBookActivity.this, "Negative digits are not allowed", Toast.LENGTH_LONG).show();
        });

        dialog.show();
    }
}