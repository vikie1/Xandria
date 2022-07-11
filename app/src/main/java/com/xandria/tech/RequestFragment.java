package com.xandria.tech;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.xandria.tech.adapter.BookRecyclerAdapter;
import com.xandria.tech.constants.FirebaseRefs;
import com.xandria.tech.model.BookRecyclerModel;

import java.util.ArrayList;

public class RequestFragment extends Fragment implements BookRecyclerAdapter.BookClickInterface{
    private DatabaseReference DbReference;
    private ArrayList<BookRecyclerModel> bookRecyclerModelArrayList;
    private BookRecyclerAdapter bookRecyclerAdapter;
    RecyclerView bookRecycler;

    private Context context;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view1 = inflater.inflate(R.layout.fragment_request, container, false);

        bookRecycler = view1.findViewById(R.id.bookRecycler);
        FloatingActionButton newRequestFab = view1.findViewById(R.id.create_request_button);
        FirebaseDatabase fireDb = FirebaseDatabase.getInstance();
        DbReference = fireDb.getReference(FirebaseRefs.BOOK_REQUESTS);
        bookRecyclerModelArrayList = new ArrayList<>();
        bookRecyclerAdapter = new BookRecyclerAdapter(bookRecyclerModelArrayList, context, this);
        bookRecycler.setLayoutManager(new GridLayoutManager(context, 2));
        bookRecycler.setAdapter(bookRecyclerAdapter);

        // will just use the add book activity for the book request
        // and use an extra parameter to show its not an actual book but a requested one
        newRequestFab.setOnClickListener(view -> {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(context); // allow users to select either the Google books input option or the manual input option
            alertDialog.setTitle("Select book input option");
            alertDialog.setItems(new CharSequence[]{"Google Books", "Manual Input"},
                    (dialog, which) -> {
                        Intent intent = new Intent(context, AddBookActivity.class);
                        intent.putExtra(AddBookActivity.EXTRA_IS_MANUAL_INPUT, which != 0);
                        startActivity(intent);
                    }
            );
            alertDialog.create().show();
        });

        getAllBooks();
        // Inflate the layout for this fragment
        return view1;
    }

    private void getAllBooks() {
        bookRecyclerModelArrayList.clear();
        DbReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                bookRecyclerModelArrayList.add(snapshot.getValue(BookRecyclerModel.class));
                bookRecyclerAdapter.notifyItemInserted(bookRecyclerModelArrayList.size()-1);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                bookRecyclerAdapter.notifyItemChanged(bookRecyclerModelArrayList.indexOf(snapshot.getValue(BookRecyclerModel.class)));
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                bookRecyclerModelArrayList.add(snapshot.getValue(BookRecyclerModel.class));
                bookRecyclerAdapter.notifyItemRemoved(bookRecyclerModelArrayList.indexOf(snapshot.getValue(BookRecyclerModel.class)));
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                bookRecyclerAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onBookClick(int position) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context); // allow users to select either the Google books input option or the manual input option
        alertDialog.setTitle("If you have the book, add it.");
        alertDialog.setItems(new CharSequence[]{"Add the book"},
                (dialog, which) -> {
            System.out.println(which);
                    if (which == 0) saveBook(bookRecyclerModelArrayList.get(position));
                }
        );
        alertDialog.create().show();
    }

    private void saveBook(BookRecyclerModel book) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(FirebaseRefs.BOOKS);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                databaseReference.child(book.getBookID()).setValue(book);
                Toast.makeText(context, "Book Added..", Toast.LENGTH_SHORT).show();
                DbReference.child(book.getBookID()).removeValue();
                startActivity(new Intent(context, MainActivity.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context, "Failed to Add Book! Error: "+ error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}