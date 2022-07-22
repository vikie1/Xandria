package com.xandria.tech.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.xandria.tech.R;
import com.xandria.tech.activity.book.AddBookActivity;
import com.xandria.tech.activity.book.BookDetailsActivity;
import com.xandria.tech.activity.book.EditBookActivity;
import com.xandria.tech.activity.order.CreateOrderActivity;
import com.xandria.tech.adapter.BookRecyclerAdapter;
import com.xandria.tech.adapter.CategoriesRecyclerAdapter;
import com.xandria.tech.constants.Categories;
import com.xandria.tech.constants.FirebaseRefs;
import com.xandria.tech.dialogues.BookDetails;
import com.xandria.tech.model.BookRecyclerModel;
import com.xandria.tech.model.CategoryModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class MainFragment extends Fragment implements BookRecyclerAdapter.BookClickInterface, CategoriesRecyclerAdapter.CategoryClicked {
    private DatabaseReference DbReference;
    private ArrayList<BookRecyclerModel> bookRecyclerModelArrayList;
    private RelativeLayout bottom_sheet;
    private BookRecyclerAdapter bookRecyclerAdapter;
    private FirebaseAuth mAuth;
    private RecyclerView bookRecycler;

    private Context context;
    private View view;
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_main, container, false);

        bookRecycler = view.findViewById(R.id.bookRecycler);
        RecyclerView categoriesRecycler = view.findViewById(R.id.categories_list);
        FirebaseDatabase fireDb = FirebaseDatabase.getInstance();
        DbReference = fireDb.getReference(FirebaseRefs.BOOKS);
        bottom_sheet = view.findViewById(R.id.bottomSheet);
        mAuth = FirebaseAuth.getInstance();

        // I have commented on this part so that you can place categories as you said in the call
        // It was still far from done so feel free to start afresh
        List<String> categories = new ArrayList<>();
        CategoriesRecyclerAdapter categoriesAdapter = new CategoriesRecyclerAdapter(context, categories, this);
        categoriesRecycler.setAdapter(categoriesAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        categoriesRecycler.setLayoutManager(linearLayoutManager);
        categories.addAll(Arrays.asList(Categories.categories));

        bookRecyclerModelArrayList = new ArrayList<>();
        bookRecyclerAdapter = new BookRecyclerAdapter(bookRecyclerModelArrayList, context, this);
        bookRecycler.setLayoutManager(new GridLayoutManager(context, 2));
        bookRecycler.setAdapter(bookRecyclerAdapter);

        onFabClicked();

        getAllBooks();

        // Inflate the layout for this fragment
        return view;
    }

    private void onFabClicked() {
        FloatingActionButton addFab = view.findViewById(R.id.addFab);
        addFab.setOnClickListener(view1 -> startActivity(new Intent(context, AddBookActivity.class)));
    }

    private void getAllBooks() {
        bookRecyclerModelArrayList.clear();
        DbReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                bookRecyclerModelArrayList.add(snapshot.getValue(BookRecyclerModel.class));
                bookRecyclerAdapter.notifyItemInserted(bookRecyclerModelArrayList.size()-1);

                ((ProgressBar) view.findViewById(R.id.loadingPB)).setVisibility(View.GONE); //hide progress bar after first item loads
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
//                bookRecyclerModelArrayList.add(snapshot.getValue(BookRecyclerModel.class));
                bookRecyclerAdapter.notifyItemChanged(bookRecyclerModelArrayList.indexOf(snapshot.getValue(BookRecyclerModel.class)));
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
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
        BookDetails bookDetails = new BookDetails();
        bookDetails.createDialogue(context, bookRecyclerModelArrayList.get(position)).show();
    }

    private void createBottomDialog(BookRecyclerModel bookRecyclerModel) {
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context);
        View view = LayoutInflater.from(context).inflate(R.layout.bottom_sheet_dialog, bottom_sheet);
        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.setCancelable(false);
        bottomSheetDialog.setCanceledOnTouchOutside(true);
        bottomSheetDialog.show();

        TextView bsBookTitle = view.findViewById(R.id.bs_BookTitle);
        TextView bsBookAuthor = view.findViewById(R.id.bs_BookAuthorName);
        TextView bsBookPages = view.findViewById(R.id.bs_BookPages);
        TextView bsBookDescription = view.findViewById(R.id.bs_BookDescription);
        ImageView bsBookImage = view.findViewById(R.id.bs_BookImage);
        Button bsViewPreviewsBtn = view.findViewById(R.id.viewPreviewBtn);
        Button bsEditBookBtn = view.findViewById(R.id.EditBookBtn);
        Button orderButton = view.findViewById(R.id.order_button);

        bsBookTitle.setText(bookRecyclerModel.getTitle());
        bsBookAuthor.setText(bookRecyclerModel.getAuthors());
        bsBookPages.setText(bookRecyclerModel.getPageCount());
        bsBookDescription.setText(bookRecyclerModel.getDescription());
        Picasso.get().load(bookRecyclerModel.getThumbnail()).into(bsBookImage);

        if (Objects.equals(Objects.requireNonNull(mAuth.getCurrentUser()).getEmail(), bookRecyclerModel.getUserId())) { // you shouldn't order what you already own
            orderButton.setVisibility(View.GONE);
            bsEditBookBtn.setVisibility(View.VISIBLE);
        }
        else {
            orderButton.setVisibility(View.VISIBLE);
            bsEditBookBtn.setVisibility(View.GONE);
        }

        bsViewPreviewsBtn.setOnClickListener(view1 -> {
            Intent intent = new Intent(context, BookDetailsActivity.class);
            intent.putExtra(BookDetailsActivity.EXTRA_BOOK, bookRecyclerModel);
            startActivity(intent);
        });

        bsEditBookBtn.setOnClickListener(view12 -> {
            Intent i = new Intent(context, EditBookActivity.class);
            i.putExtra("book", bookRecyclerModel);
            startActivity(i);
        });

        orderButton.setOnClickListener(v -> {
            Intent intent = new Intent(context, CreateOrderActivity.class);
            intent.putExtra(CreateOrderActivity.EXTRA_BOOK, bookRecyclerModel);
            startActivity(intent);
        });
    }

    @Override
    public void onCategoryClick(String category) {
        pastMainCat = false;
        getFilteredBook(category); // start populating for the main category while we search for sub categories

        DatabaseReference categoryReference = FirebaseDatabase.getInstance().getReference(FirebaseRefs.CATEGORIES);
        categoryReference.child(category).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                CategoryModel categoryModel = snapshot.getValue(CategoryModel.class);
                for (String subCategory : categoryModel.getSubCategories()) {
                    getFilteredBook(subCategory);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    boolean pastMainCat = false; // if books have been found in main category then append books from subcategories to that list else create new list
    private void getFilteredBook(String category){
        ArrayList<BookRecyclerModel> bookRecyclerModels;
        if (pastMainCat) bookRecyclerModels = this.bookRecyclerModelArrayList;
        else bookRecyclerModels = new ArrayList<>();
        DbReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                    BookRecyclerModel bookRecyclerModel = dataSnapshot.getValue(BookRecyclerModel.class);
                    if(bookRecyclerModel != null &&
                            bookRecyclerModel.getCategory() != null &&
                            bookRecyclerModel.getCategory().toLowerCase().contains(category.toLowerCase())) {

                        if (!bookRecyclerModels.contains(bookRecyclerModel))
                            bookRecyclerModels.add(bookRecyclerModel);
                        System.out.println(bookRecyclerModel);
                        System.out.println(category);
                        pastMainCat = true;
                    }
                }
                BookRecyclerAdapter newBookRecyclerAdapter = new BookRecyclerAdapter(bookRecyclerModels, context, MainFragment.this);
                bookRecycler.setAdapter(newBookRecyclerAdapter);
                MainFragment.this.bookRecyclerAdapter = newBookRecyclerAdapter;
                MainFragment.this.bookRecyclerModelArrayList = bookRecyclerModels;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}