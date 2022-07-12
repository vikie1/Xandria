package com.xandria.tech.activity.book;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.xandria.tech.MainActivity;
import com.xandria.tech.R;
import com.xandria.tech.constants.FirebaseRefs;
import com.xandria.tech.model.BookRecyclerModel;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class EditBookActivity extends AppCompatActivity {
    private TextInputEditText bookTitle, bookSubTitle, bookAuthorName, bookImageLink, bookDescription, bookPagesNo;
    private Button deleteBookBtn;
    private DatabaseReference DbReference;
    private String bookID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_book);

        bookTitle = findViewById(R.id.editBookTitle);
        bookSubTitle = findViewById(R.id.editBookSubTitle);
        bookAuthorName = findViewById(R.id.editAuthorName);
        bookImageLink = findViewById(R.id.editBookImageLink);
        bookPagesNo = findViewById(R.id.editPagesNo);
        bookDescription = findViewById(R.id.editBookDescription);
        Button editBookBtn = findViewById(R.id.editBookBtn);
        Button cancelButton = findViewById(R.id.cancelEditBookBtn);
        deleteBookBtn = findViewById(R.id.DeleteHostingBtn);

        FirebaseDatabase fireDb = FirebaseDatabase.getInstance();
        BookRecyclerModel booksModel = getIntent().getParcelableExtra("book");
        if(booksModel !=null){
            bookTitle.setText(booksModel.getTitle());
            bookSubTitle.setText(booksModel.getSubtitle());
            bookAuthorName.setText(booksModel.getAuthors());
            bookImageLink.setText(booksModel.getThumbnail());
            bookPagesNo.setText(booksModel.getPageCount());
            bookID = booksModel.getBookID();
        }
        DbReference = fireDb.getReference(FirebaseRefs.BOOKS).child(bookID);
        editBookBtn.setOnClickListener(view -> {
            String bookTitleStr = Objects.requireNonNull(bookTitle.getText()).toString();
            String bookSubTitleStr = Objects.requireNonNull(bookSubTitle.getText()).toString();
            String bookAuthorNameStr = Objects.requireNonNull(bookAuthorName.getText()).toString();
            String bookImageLinkStr = Objects.requireNonNull(bookImageLink.getText()).toString();
            String bookPagesNoStr = Objects.requireNonNull(bookPagesNo.getText()).toString();
            String bookDescriptionStr = Objects.requireNonNull(bookDescription.getText()).toString();
            bookID = bookTitleStr;

            Map<String, Object> map = new HashMap<>();
            map.put("title", bookTitleStr);
            map.put("subtitle", bookSubTitleStr);
            map.put("authors", bookAuthorNameStr);
            map.put("thumbnail", bookImageLinkStr);
            map.put("pagecount", bookPagesNoStr);
            map.put("description", bookDescriptionStr);
            map.put("bookID", bookID);

            DbReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    DbReference.updateChildren(map);
                    startActivity(new Intent(EditBookActivity.this, MainActivity.class));

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(EditBookActivity.this, "Failed to edit Book details: "+ error, Toast.LENGTH_SHORT).show();

                }
            });
            deleteBookBtn.setOnClickListener(view1 -> deleteBook());
        });

        cancelButton.setOnClickListener(view -> startActivity(new Intent(EditBookActivity.this, MainActivity.class)));
    }
    private void deleteBook(){
        DbReference.removeValue();
    }
}