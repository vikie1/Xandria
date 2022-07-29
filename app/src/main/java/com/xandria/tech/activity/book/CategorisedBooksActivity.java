package com.xandria.tech.activity.book;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.xandria.tech.R;

public class CategorisedBooksActivity extends AppCompatActivity {
    public static final String EXTRA_MAIN_CATEGORY = "Main Category";

    private String mainCategory;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categorised_books);

        mainCategory = getIntent().getExtras().getString(EXTRA_MAIN_CATEGORY);
        if (mainCategory != null) //TODO: something
            return;
        else {
            Toast.makeText(this, "Category was not specified", Toast.LENGTH_SHORT).show();
            onBackPressed();
            finish();
        }
    }

}