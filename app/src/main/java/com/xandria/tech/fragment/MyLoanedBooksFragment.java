package com.xandria.tech.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.xandria.tech.R;
import com.xandria.tech.activity.order.OrderPlacedActivity;
import com.xandria.tech.adapter.OrdersListViewAdapter;
import com.xandria.tech.constants.FirebaseRefs;
import com.xandria.tech.model.OrdersModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MyLoanedBooksFragment extends Fragment {
    List<OrdersModel> ordersList;
    OrdersListViewAdapter listViewAdapter;
    DatabaseReference firebaseDatabaseReference;
    String userId;

    View view;
    Context context;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_my_loaned_books, container, false);

        firebaseDatabaseReference = FirebaseDatabase.getInstance().getReference(FirebaseRefs.ORDERS);
        userId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getEmail();

        // set up the the listview
        ordersList = new ArrayList<>();
        listViewAdapter = new OrdersListViewAdapter(context, ordersList);
        ListView ordersListView = view.findViewById(R.id.my_ordered_books_list);
        ordersListView.setAdapter(listViewAdapter);

        // action performed when a list item is clicked
        ordersListView.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent(context, OrderPlacedActivity.class);
            intent.putExtra(OrderPlacedActivity.EXTRA_ORDER, listViewAdapter.getItem(position));
            intent.putExtra(OrderPlacedActivity.EXTRA_IS_MY_BOOK, true);
            startActivity(intent);
        });

        getOrdersFromDb();
        return view;
    }

    void getOrdersFromDb(){
        ordersList.clear();

        firebaseDatabaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                OrdersModel ordersModel;
                for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    if (dataSnapshot.exists() &&
                            Objects.requireNonNull(dataSnapshot.child("hostLocationUserId").getValue(String.class)).equals(userId)) {
                        ordersModel = dataSnapshot.getValue(OrdersModel.class);
                        listViewAdapter.add(ordersModel);
                    }
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                listViewAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                listViewAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                listViewAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}