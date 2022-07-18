package com.xandria.tech.fragment;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.xandria.tech.R;
import com.xandria.tech.constants.FirebaseRefs;
import com.xandria.tech.model.BookRecyclerModel;
import com.xandria.tech.util.GPSTracker;

public class MapsFragment extends Fragment implements LocationListener {
    double currentLatitude, currentLongitude;
    GPSTracker gpsTracker;
    private DatabaseReference databaseReference;

    Context context;
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
        gpsTracker = new GPSTracker(context);
    }

    private final OnMapReadyCallback callback = new OnMapReadyCallback() {

        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */
        @Override
        public void onMapReady(@NonNull GoogleMap googleMap) {
            if(gpsTracker.getIsGPSTrackingEnabled()){
                currentLatitude = gpsTracker.getLatitude();
                currentLongitude = gpsTracker.getLongitude();
            } else gpsTracker.showSettingsAlert();

            LatLng currentLocation = new LatLng(currentLatitude, currentLongitude);
            googleMap.addMarker(new MarkerOptions().position(currentLocation).title("Current location"));
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(currentLocation));

            databaseReference = FirebaseDatabase.getInstance().getReference(FirebaseRefs.BOOKS);

            getAllBooks(googleMap);
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_maps, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        currentLatitude = location.getLatitude();
        currentLongitude = location.getLongitude();
    }

    void createMarkers(GoogleMap googleMap, double latitude, double longitude, String title){
        LatLng currentLocation = new LatLng(latitude, longitude);
        googleMap.addMarker(
                new MarkerOptions()
                        .position(currentLocation)
                        .title(title)
                        .icon(
                                bitmapDescriptorFromVector(context, R.drawable.ic_baseline_menu_book_24)
                        )
        );
    }

    private void getAllBooks(GoogleMap googleMap) {
        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                BookRecyclerModel bookRecyclerModel = snapshot.getValue(BookRecyclerModel.class);
                if (bookRecyclerModel != null && bookRecyclerModel.getLocation() != null) {
                    createMarkers(
                            googleMap,
                            bookRecyclerModel.getLocation().getLatitude(),
                            bookRecyclerModel.getLocation().getLongitude(),
                            bookRecyclerModel.getTitle()
                    );
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                BookRecyclerModel bookRecyclerModel = snapshot.getValue(BookRecyclerModel.class);
                if (bookRecyclerModel != null) {
                    createMarkers(
                            googleMap,
                            bookRecyclerModel.getLocation().getLatitude(),
                            bookRecyclerModel.getLocation().getLongitude(),
                            bookRecyclerModel.getTitle()
                    );
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                BookRecyclerModel bookRecyclerModel = snapshot.getValue(BookRecyclerModel.class);
                if (bookRecyclerModel != null) {
                    createMarkers(
                            googleMap,
                            bookRecyclerModel.getLocation().getLatitude(),
                            bookRecyclerModel.getLocation().getLongitude(),
                            bookRecyclerModel.getTitle()
                    );
                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                BookRecyclerModel bookRecyclerModel = snapshot.getValue(BookRecyclerModel.class);
                if (bookRecyclerModel != null) {
                    createMarkers(
                            googleMap,
                            bookRecyclerModel.getLocation().getLatitude(),
                            bookRecyclerModel.getLocation().getLongitude(),
                            bookRecyclerModel.getTitle()
                    );
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        if (vectorDrawable == null) return null;
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

}