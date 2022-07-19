package com.xandria.tech.dialogues;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.hbb20.CountryCodePicker;
import com.xandria.tech.R;
import com.xandria.tech.activity.order.OrderRouteActivity;
import com.xandria.tech.constants.FirebaseRefs;
import com.xandria.tech.constants.LoggedInUser;
import com.xandria.tech.dto.Location;
import com.xandria.tech.model.BookRecyclerModel;
import com.xandria.tech.model.OrdersModel;
import com.xandria.tech.model.User;
import com.xandria.tech.util.GPSTracker;

import java.util.Objects;

public class CreateOrder {
    String userId;
    BookRecyclerModel book;
    DatabaseReference firebaseDatabaseRef;
    Context context;

    String contact;

    public CreateOrder(Context context, BookRecyclerModel book){
        this.context = context;
        this.book = book;
        this.userId = Objects.requireNonNull(Objects.requireNonNull(
                        FirebaseAuth.getInstance().getCurrentUser()).getEmail())
                .replaceAll("[\\-+. ^:,]","_");
        this.firebaseDatabaseRef = FirebaseDatabase.getInstance().getReference(FirebaseRefs.ORDERS);

        addLocationToBook();
    }

    private void addContact(Dialog dialog) {
        ViewSwitcher viewSwitcher = dialog.findViewById(R.id.saved_new_contact_switcher);
        if (LoggedInUser.getInstance().getCurrentUser().getPhoneNumber() != null &&
                        !LoggedInUser.getInstance().getCurrentUser().getPhoneNumber().trim().isEmpty()){
            viewSwitcher.setDisplayedChild(0);
            ((TextView)dialog.findViewById(R.id.contact_string)).setText(LoggedInUser.getInstance().getCurrentUser().getPhoneNumber());
        } else viewSwitcher.setDisplayedChild(1);

        ((Button)dialog.findViewById(R.id.get_contact_btn)).setVisibility(View.GONE);
        TextView switcherTextView = dialog.findViewById(R.id.contact_change);

        switcherTextView.setOnClickListener(v -> viewSwitcher.setDisplayedChild(1));
        dialog.show();
    }

    private void addLocationToBook() {
        Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.create_order_dialogue_layout);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        ((Button)dialog.findViewById(R.id.use_current)).setText(context.getResources().getString(R.string.pick_up));
        ((Button)dialog.findViewById(R.id.use_preferred)).setText(context.getResources().getString(R.string.delivery));
        ((Button)dialog.findViewById(R.id.proceed_btn)).setVisibility(View.GONE);

        addContact(dialog);
        handleChoiceDialogueButtonClicks(dialog);
        dialog.show();
    }

    private void handleChoiceDialogueButtonClicks(Dialog dialog) {
        ImageButton cancelButton = dialog.findViewById(R.id.cancel_button);
        Button useCurrent = dialog.findViewById(R.id.use_current);
        Button useNew = dialog.findViewById(R.id.use_preferred);

        cancelButton.setOnClickListener(v -> {
            dialog.dismiss();
            Toast.makeText(context, "Location is needed to save order", Toast.LENGTH_LONG).show();
        });
        useCurrent.setOnClickListener(v -> {
            EditText phoneNumber = dialog.findViewById(R.id.phone_number_input);
            CountryCodePicker ccp = dialog.findViewById(R.id.ccp);

            String contactWithNoCode = phoneNumber.getText().toString();
            if (!contactWithNoCode.trim().isEmpty() && !contactWithNoCode.trim().isEmpty()) {
                contact = ccp.getSelectedCountryCodeWithPlus() + contactWithNoCode;
                User user = LoggedInUser.getInstance().getCurrentUser();
                DatabaseReference userDBRef = FirebaseDatabase
                        .getInstance()
                        .getReference(FirebaseRefs.USERS);
                userDBRef.child(user.getUserId()).child("phoneNumber").setValue(contact);
            }
            if (contact == null) {
                if (LoggedInUser.getInstance().getCurrentUser().getPhoneNumber() != null)
                    contact = LoggedInUser.getInstance().getCurrentUser().getPhoneNumber();
                else {
                    Toast.makeText(context, "Contact is needed to proceed", Toast.LENGTH_LONG).show();
                    return;
                }
            }
            Location location = book.getLocation();
            if (location != null) {
                createOrder(location);

                Toast.makeText(context, "Order has been placed see book location in map", Toast.LENGTH_LONG).show();

                // take user to location of book
                Intent intent = new Intent(context, OrderRouteActivity.class);
                intent.putExtra(OrderRouteActivity.ORDER_LOCATION, location);
                intent.putExtra(OrderRouteActivity.BOOK_ORDERED, book.getTitle());
                context.startActivity(intent);
            }
            else Toast.makeText(context, "Location is permissions are needed to proceed", Toast.LENGTH_LONG).show();
        });
        useNew.setOnClickListener(v ->{
            EditText phoneNumber = dialog.findViewById(R.id.phone_number_input);
            CountryCodePicker ccp = dialog.findViewById(R.id.ccp);

            String contactWithNoCode = phoneNumber.getText().toString();
            if (!contactWithNoCode.trim().isEmpty() && !contactWithNoCode.trim().isEmpty()) {
                contact = ccp.getSelectedCountryCodeWithPlus() + contactWithNoCode;
                User user = LoggedInUser.getInstance().getCurrentUser();
                DatabaseReference userDBRef = FirebaseDatabase
                        .getInstance()
                        .getReference(FirebaseRefs.USERS);
                userDBRef.child(user.getUserId()).child("phoneNumber").setValue(contact);
            }
            if (contact == null) {
                if (LoggedInUser.getInstance().getCurrentUser().getPhoneNumber() != null)
                    contact = LoggedInUser.getInstance().getCurrentUser().getPhoneNumber();
                else {
                    Toast.makeText(context, "Contact is needed to proceed", Toast.LENGTH_LONG).show();
                    return;
                }
            }
            dialog.dismiss();
            switchToManualLocationEntry();
        });
    }

    private void switchToManualLocationEntry() {
        Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.location_input);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(context.getResources().getColor(R.color.black)));

        ((LinearLayout) dialog.findViewById(R.id.use_current_layout)).setVisibility(View.VISIBLE);
        TextInputEditText streetAddress = dialog.findViewById(R.id.street_address);
        TextInputEditText locality = dialog.findViewById(R.id.locality);
        TextInputEditText city = dialog.findViewById(R.id.city);
        TextInputEditText pinCode = dialog.findViewById(R.id.pin_code);
        Button orderButton = dialog.findViewById(R.id.add_location);
        Button useCurrent = dialog.findViewById(R.id.use_current);
        ImageButton cancelButton = dialog.findViewById(R.id.cancel_button);

        cancelButton.setOnClickListener(v -> {
            dialog.dismiss();
            Toast.makeText(context, "Location is needed to save order", Toast.LENGTH_LONG).show();
        });
        orderButton.setOnClickListener(v -> {
            Location location = new Location(context,
                    String.valueOf(streetAddress.getText()),
                    String.valueOf(locality.getText()),
                    String.valueOf(city.getText()),
                    String.valueOf(pinCode.getText())
            );
            createOrder(location);
            dialog.onBackPressed();
        });
        useCurrent.setOnClickListener(v -> {
            Location location = getCurrentLoc();
            if (location != null) {
                createOrder(location);
                dialog.onBackPressed();
            }
            else Toast.makeText(context, "Location permissions are needed to proceed", Toast.LENGTH_LONG).show();
        });

        dialog.show();
    }

    private void createOrder(Location dropLocation) {
        OrdersModel newOrder = new OrdersModel(
                userId, // let user be the orders id
                book.getBookID(),
                Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getEmail(),
                book.getUserId(),
                book.getLocation(),
                FirebaseAuth.getInstance().getCurrentUser().getEmail(),
                book.getTitle(),
                book.getThumbnail()
        );
        if (
                LoggedInUser.getInstance().getCurrentUser().getPoints() >= book.getValue()
        ){
            // decrease borrowers points
            User user = LoggedInUser.getInstance().getCurrentUser();
            DatabaseReference userDBRef = FirebaseDatabase
                    .getInstance()
                    .getReference(FirebaseRefs.USERS);
            userDBRef.child(user.getUserId()).child("points").setValue(
                    user.getPoints() - book.getValue()
            );

            // increase book host user points
            String hostUserId = book.getUserId().replaceAll("[\\-+. ^:,]","_");
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
                    if (points == null) currentData.setValue(book.getValue());
                    else currentData.setValue(points + book.getValue());
                    return Transaction.success(currentData);
                }

                @Override
                public void onComplete(@Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot currentData) {

                }
            });
            newOrder.setBookValue(book.getValue());
            newOrder.setDeliveryContact(contact);
            newOrder.setDateOrdered();
            newOrder.setDropLocation(dropLocation);
            firebaseDatabaseRef.child(newOrder.getOrderId()).child(newOrder.getBookId()).setValue(newOrder);
            Toast.makeText(context, "Order Created", Toast.LENGTH_LONG).show();
        } else Toast.makeText(context, "You need more than " + book.getValue() + " points to complete this order", Toast.LENGTH_LONG).show();
    }

    private Location getCurrentLoc() {
        GPSTracker gpsTracker = new GPSTracker(context);
        if(!gpsTracker.getIsGPSTrackingEnabled()) {
            gpsTracker.showSettingsAlert();
            gpsTracker.stopUsingGPS();
        } else {
            gpsTracker = new GPSTracker(context);
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
}
