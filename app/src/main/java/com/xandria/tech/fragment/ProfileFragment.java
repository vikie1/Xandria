package com.xandria.tech.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.text.HtmlCompat;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.razorpay.Checkout;
import com.xandria.tech.BuildConfig;
import com.xandria.tech.R;
import com.xandria.tech.activity.user.LoginActivity;
import com.xandria.tech.constants.FirebaseRefs;
import com.xandria.tech.constants.LoggedInUser;
import com.xandria.tech.model.User;
import com.xandria.tech.util.Points;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;
import java.util.Objects;

public class ProfileFragment extends Fragment {
    DatabaseReference firebaseDatabaseReference;
    String userId;

    View view;
    Context context;
    double points;
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
        Checkout.preload(context); // for performance
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_profile, container, false);

        firebaseDatabaseReference = FirebaseDatabase.getInstance().getReference(FirebaseRefs.USERS);
        userId = Objects.requireNonNull(Objects.requireNonNull(
                        FirebaseAuth.getInstance().getCurrentUser()).getEmail())
                .replaceAll("[\\-+. ^:,]","_");

        Button logoutBtn = view.findViewById(R.id.logout_btn);
        logoutBtn.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(context, LoginActivity.class));
        });

        setUpBuyPointsButton(view);
        getUser();

        return  view;
    }

    private void setUpBuyPointsButton(View view) {
        Button buyPoints = view.findViewById(R.id.buy_points_btn);

        buyPoints.setOnClickListener(v -> {
            Checkout checkout = new Checkout();
            checkout.setKeyID(BuildConfig.RAZOR_PAY_API_KEY_ID);
            checkout.setImage(R.drawable.xandria);
            Dialog dialog = new Dialog(context);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(true);
            dialog.setContentView(R.layout.purchase_points_layout);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(context.getResources().getColor(R.color.black)));

            TextView amount = dialog.findViewById(R.id.total_points);
            ImageButton cancelButton = dialog.findViewById(R.id.cancel_button);
            EditText positiveText = dialog.findViewById(R.id.positive_number_input);
            EditText floatText = dialog.findViewById(R.id.float_part_input);
            Button proceed = dialog.findViewById(R.id.proceed_btn);

            cancelButton.setOnClickListener(cancel -> {
                dialog.dismiss();
                Toast.makeText(context, "Book value is needed to proceed", Toast.LENGTH_LONG).show();
            });
            proceed.setOnClickListener(pay ->{
                String positiveValue = positiveText.getText().toString();
                String floatValue = floatText.getText().toString();
                if (positiveValue.trim().isEmpty()) positiveValue = "00";
                if (floatValue.trim().isEmpty()) floatValue = "00";
                long positive = Integer.parseInt(positiveValue);
                int floatNumber = Integer.parseInt(floatValue);

                if ((positive >= 0) || (floatNumber >= 0)){
                    if (floatNumber > 99) Toast.makeText(context, "Only use 2 decimal places", Toast.LENGTH_LONG).show();
                    else {
                        double value = Double.parseDouble(positive + "." + floatNumber);
                        points = Points.rupeesToPoints(value);
                        String amountToBePaid = String
                                .format(Locale.getDefault(), "%.2f", value)
                                .replace(".", "")
                                .replace(",", ""); // get currency in subunits
                        int moneyInPaise = Integer.parseInt(amountToBePaid);
                        System.out.println("Amount:" + moneyInPaise);
                        try {
                            JSONObject options = new JSONObject();
                            options.put("name", BuildConfig.BUSINESS_NAME);
                            options.put("currency", "INR");
                            options.put("amount", moneyInPaise);//pass amount in currency subunits
                            options.put("description", "Purchase of Xandria Tokens");
                            options.put("prefill.email", FirebaseAuth.getInstance().getCurrentUser().getEmail());
                            options.put("prefill.contact", LoggedInUser.getInstance().getCurrentUser().getPhoneNumber());

                            checkout.open(requireActivity(), options);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(context, "An error occurred in processing payment", Toast.LENGTH_LONG).show();
                        }
                        dialog.dismiss();
                    }
                } else Toast.makeText(context, "Negative digits are not allowed", Toast.LENGTH_LONG).show();
            });

            TextWatcher textWatcher = new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    String positiveValue = positiveText.getText().toString();
                    String floatValue = floatText.getText().toString();
                    if (positiveValue.trim().isEmpty()) positiveValue = "00";
                    if (floatValue.trim().isEmpty()) floatValue = "00";
                    long positive = Integer.parseInt(positiveValue);
                    int floatNumber = Integer.parseInt(floatValue);

                    if ((positive >= 0) || (floatNumber >= 0)){
                        if (floatNumber > 99) Toast.makeText(context, "Only use 2 decimal places", Toast.LENGTH_LONG).show();
                        else {
                            double value = Double.parseDouble(positive + "." + floatNumber);
                            amount.setText(HtmlCompat.fromHtml(
                                    context.getString(R.string.points).concat(" ").concat(String.valueOf(Points.rupeesToPoints(value))),
                                    HtmlCompat.FROM_HTML_MODE_COMPACT
                            ));
                        }
                    } else Toast.makeText(context, "Negative digits are not allowed", Toast.LENGTH_LONG).show();
                }
            };

            positiveText.addTextChangedListener(textWatcher);
            floatText.addTextChangedListener(textWatcher);
            dialog.show();
        });
    }

    private void getUser() {
        firebaseDatabaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if (snapshot.getKey().equals(userId)) {
                    populateUser(Objects.requireNonNull(snapshot.getValue(User.class)));
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if (snapshot.getKey().equals(userId)) {
                    populateUser(Objects.requireNonNull(snapshot.getValue(User.class)));
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                if (snapshot.getKey().equals(userId)) {
                    populateUser(Objects.requireNonNull(snapshot.getValue(User.class)));
                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if (snapshot.getKey().equals(userId)) {
                    populateUser(Objects.requireNonNull(snapshot.getValue(User.class)));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context, "An error occurred while retrieving the details", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void populateUser(User value) {
        TextView usernameText = view.findViewById(R.id.display_username);
        TextView emailText = view.findViewById(R.id.display_email);
        TextView phoneText = view.findViewById(R.id.display_phone);
        TextView pointsView = view.findViewById(R.id.display_points);

        usernameText.setText(HtmlCompat.fromHtml(
                getString(R.string.name).concat(" ").concat(value.getName()),
                HtmlCompat.FROM_HTML_MODE_COMPACT
        ));
        emailText.setText(HtmlCompat.fromHtml(
                getString(R.string.email).concat(" ").concat(value.getEmail()),
                HtmlCompat.FROM_HTML_MODE_COMPACT
        ));
        phoneText.setText(HtmlCompat.fromHtml(
                getString(R.string.phone).concat(" ").concat(value.getPhoneNumber()),
                HtmlCompat.FROM_HTML_MODE_COMPACT
        ));
        pointsView.setText(HtmlCompat.fromHtml(
                getString(R.string.points).concat(" ").concat(String.valueOf(value.getPoints())),
                HtmlCompat.FROM_HTML_MODE_COMPACT
        ));
    }

    public void handlePaymentComplete(boolean success){
        if (!success) return;
        // award the points on successful payment
        User user = LoggedInUser.getInstance().getCurrentUser();
        DatabaseReference userDBRef = FirebaseDatabase
                .getInstance()
                .getReference(FirebaseRefs.USERS);
        userDBRef.child(user.getUserId()).child("points").setValue(
                user.getPoints() + points
        );
    }
}