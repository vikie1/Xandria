package com.xandria.tech.activity.order;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.hbb20.CountryCodePicker;
import com.xandria.tech.R;
import com.xandria.tech.constants.FirebaseRefs;
import com.xandria.tech.dto.Location;
import com.xandria.tech.model.BookRecyclerModel;
import com.xandria.tech.model.OrdersModel;

import java.util.Objects;

public class CreateOrderActivity extends AppCompatActivity {
    public static final String EXTRA_BOOK = "book_order";
    String userId;
    BookRecyclerModel book;
    DatabaseReference firebaseDatabaseRef;

    CountryCodePicker ccp;
    String selectedCountryCode;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_order);

        userId = Objects.requireNonNull(Objects.requireNonNull(
                        FirebaseAuth.getInstance().getCurrentUser()).getEmail())
                .replaceAll("[\\-+. ^:,]","_");

        book = getIntent().getParcelableExtra(EXTRA_BOOK);
        if (book != null) {
            firebaseDatabaseRef = FirebaseDatabase.getInstance().getReference(FirebaseRefs.ORDERS);

            Button currentLocation = findViewById(R.id.use_current);
            currentLocation.setOnClickListener(v -> createOrder(book.getLocation()));

            Button orderButton = findViewById(R.id.create_order_button);
            TextInputEditText streetAddress = findViewById(R.id.street_address);
            TextInputEditText locality = findViewById(R.id.locality);
            TextInputEditText city = findViewById(R.id.city);
            TextInputEditText pinCode = findViewById(R.id.pin_code);
            orderButton.setOnClickListener(v -> {
                Location location = new Location(CreateOrderActivity.this,
                        String.valueOf(streetAddress.getText()),
                        String.valueOf(locality.getText()),
                        String.valueOf(city.getText()),
                        String.valueOf(pinCode.getText())
                );
                createOrder(location);
            });

            ccp = findViewById(R.id.ccp);
            ccp.setOnCountryChangeListener(() -> selectedCountryCode = ccp.getSelectedCountryCodeWithPlus());
        } else Toast.makeText(this, "A problem occurred when retrieving the book", Toast.LENGTH_LONG).show();
    }

    private void createOrder(Location dropLocation) {
        EditText phoneNumber = findViewById(R.id.phone_number_input);
        String contact = phoneNumber.getText().toString();
        if (contact.equals("")) {
            Toast.makeText(this, "Phone number must be present", Toast.LENGTH_LONG).show();
            return;
        }
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
        if (selectedCountryCode == null || selectedCountryCode.equals(""))
            selectedCountryCode = ccp.getSelectedCountryCodeWithPlus();
        newOrder.setDeliveryContact(selectedCountryCode + contact);
        newOrder.setDropLocation(dropLocation);
        firebaseDatabaseRef.child(newOrder.getOrderId()).child(newOrder.getBookId()).setValue(newOrder);
        Toast.makeText(CreateOrderActivity.this, "Order Created", Toast.LENGTH_LONG).show();
        onBackPressed();
    }

}