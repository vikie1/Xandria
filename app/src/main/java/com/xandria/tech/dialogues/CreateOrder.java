package com.xandria.tech.dialogues;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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
import com.xandria.tech.constants.FirebaseRefs;
import com.xandria.tech.constants.LoggedInUser;
import com.xandria.tech.dto.Location;
import com.xandria.tech.model.BookRecyclerModel;
import com.xandria.tech.model.OrdersModel;
import com.xandria.tech.model.User;

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

        addContact();
    }

    private void addContact() {
        Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.contact_dialogue_layout);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(context.getResources().getColor(R.color.black)));

        ViewSwitcher viewSwitcher = dialog.findViewById(R.id.saved_new_contact_switcher);
        if (LoggedInUser.getInstance().getCurrentUser().getPhoneNumber() != null &&
                        !LoggedInUser.getInstance().getCurrentUser().getPhoneNumber().trim().isEmpty()){
            viewSwitcher.setDisplayedChild(0);
            ((TextView)dialog.findViewById(R.id.contact_string)).setText(LoggedInUser.getInstance().getCurrentUser().getPhoneNumber());
        } else viewSwitcher.setDisplayedChild(1);

        Button useSaved = dialog.findViewById(R.id.proceed_btn);
        Button getContact = dialog.findViewById(R.id.get_contact_btn);
        TextView switcherTextView = dialog.findViewById(R.id.contact_change);

        useSaved.setOnClickListener(v -> {
            contact = LoggedInUser.getInstance().getCurrentUser().getPhoneNumber();
            dialog.dismiss();
            addLocationToBook();
        });

        getContact.setOnClickListener(v -> {
            EditText phoneNumber = dialog.findViewById(R.id.phone_number_input);
            CountryCodePicker ccp = dialog.findViewById(R.id.ccp);

            String contactWithNoCode = phoneNumber.getText().toString();
            if (!contactWithNoCode.trim().isEmpty()){
                contact = ccp.getSelectedCountryCodeWithPlus() + contactWithNoCode;
                User user = LoggedInUser.getInstance().getCurrentUser();
                DatabaseReference userDBRef = FirebaseDatabase
                        .getInstance()
                        .getReference(FirebaseRefs.USERS);
                userDBRef.child(user.getUserId()).child("phoneNumber").setValue(contact);
                dialog.dismiss();
                addLocationToBook();
            } else Toast.makeText(context, "Phone number is needed to proceed", Toast.LENGTH_LONG).show();
        });
        switcherTextView.setOnClickListener(v -> viewSwitcher.setDisplayedChild(1));
        dialog.show();
    }

    private void addLocationToBook() {
        Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.location_input_mode);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(context.getResources().getColor(R.color.black)));

        ((Button)dialog.findViewById(R.id.use_current)).setText(context.getResources().getString(R.string.pick_up));
        ((Button)dialog.findViewById(R.id.use_preferred)).setText(context.getResources().getString(R.string.delivery));

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
            Location location = book.getLocation();
            if (location != null) {
                createOrder(location, dialog);
            }
            else Toast.makeText(context, "Location is permissions are needed to proceed", Toast.LENGTH_LONG).show();
        });
        useNew.setOnClickListener(v ->{
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

        TextInputEditText streetAddress = dialog.findViewById(R.id.street_address);
        TextInputEditText locality = dialog.findViewById(R.id.locality);
        TextInputEditText city = dialog.findViewById(R.id.city);
        TextInputEditText pinCode = dialog.findViewById(R.id.pin_code);
        Button orderButton = dialog.findViewById(R.id.add_location);
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
            createOrder(location, dialog);
        });

        dialog.show();
    }

    private void createOrder(Location dropLocation, Dialog dialog) {
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
        dialog.onBackPressed();
    }
}
