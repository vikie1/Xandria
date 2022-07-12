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
import com.xandria.tech.activity.order.ReturnedOrderActivity;
import com.xandria.tech.adapter.ReturnedOrdersListViewAdapter;
import com.xandria.tech.constants.FirebaseRefs;
import com.xandria.tech.model.ReturnOrdersModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ReturnedOrdersFragment extends Fragment {
    List<ReturnOrdersModel> ordersList;
    ReturnedOrdersListViewAdapter listViewAdapter;
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
        view = inflater.inflate(R.layout.fragment_returned_orders, container, false);
        firebaseDatabaseReference = FirebaseDatabase.getInstance().getReference(FirebaseRefs.RETURNED_ORDERS);
        userId = Objects.requireNonNull(Objects.requireNonNull(
                        FirebaseAuth.getInstance().getCurrentUser()).getEmail())
                .replaceAll("[\\-+. ^:,]","_");

        // set up the the listview
        ordersList = new ArrayList<>();
        listViewAdapter = new ReturnedOrdersListViewAdapter(context, ordersList);
        ListView ordersListView = view.findViewById(R.id.returned_orders_list);
        ordersListView.setAdapter(listViewAdapter);

        // action performed when a list item is clicked
        ordersListView.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent(context, ReturnedOrderActivity.class);
            intent.putExtra(ReturnedOrderActivity.EXTRA_RETURN_ORDER, listViewAdapter.getItem(position));
            startActivity(intent);
        });

        getReturnedOrdersFromDb();
        return view;
    }

    void getReturnedOrdersFromDb(){
        ordersList.clear();

        firebaseDatabaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                ReturnOrdersModel ordersModel;
                for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    if (dataSnapshot.exists() && snapshot.getKey().equals("return_" + userId)) {
                        ordersModel = dataSnapshot.getValue(ReturnOrdersModel.class);
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