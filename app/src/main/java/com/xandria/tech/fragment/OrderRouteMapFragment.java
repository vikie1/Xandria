package com.xandria.tech.fragment;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

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
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.xandria.tech.R;
import com.xandria.tech.activity.order.OrderRouteActivity;
import com.xandria.tech.dto.Location;
import com.xandria.tech.util.LocationUtils;

public class OrderRouteMapFragment extends Fragment implements LocationUtils.LocationPermissionResult {
    Location orderLocation;
    private String bookTitle;

    private double currentLatitude, currentLongitude;
    private GoogleMap googleMap;
    private Marker currentLocationMarker;
    private LocationUtils locationUtils;

    private Context context;
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
        locationUtils = new LocationUtils(OrderRouteMapFragment.this, context, OrderRouteMapFragment.this);
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
            OrderRouteMapFragment.this.googleMap = googleMap;

            LatLng latLng = locationUtils.getLatLng();
            if (latLng != null) {
                currentLatitude = latLng.latitude;
                currentLongitude = latLng.longitude;
                createMarkerForCurrentLoc();
            }

            createTargetMarkers(googleMap);
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        orderLocation = getArguments() != null ? getArguments().getParcelable(OrderRouteActivity.ORDER_LOCATION) : null;
        bookTitle = getArguments().getString(OrderRouteActivity.BOOK_ORDERED);
        return inflater.inflate(R.layout.fragment_order_route_map, container, false);
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

    public void createMarkerForCurrentLoc(){
        if(googleMap != null) {

            if (currentLocationMarker != null) currentLocationMarker.remove();

            LatLng currentLocation = new LatLng(currentLatitude, currentLongitude);
            currentLocationMarker = googleMap.addMarker(new MarkerOptions().position(currentLocation).title("Current location"));
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 12));
        }
    }

    private void createTargetMarkers(GoogleMap googleMap){
        if (orderLocation == null) return;
        LatLng currentLocation = new LatLng(orderLocation.getLatitude(), orderLocation.getLongitude());
        googleMap.addMarker(
                new MarkerOptions()
                        .position(currentLocation)
                        .title((bookTitle == null) ? "Target Book Location" : bookTitle)
                        .icon(
                                bitmapDescriptorFromVector(context, R.drawable.ic_baseline_approval_24)
                        )
        );
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

    @Override
    public void onPermissionGranted(LatLng latLng) {
        currentLatitude = latLng.latitude;
        currentLongitude = latLng.longitude;

        createMarkerForCurrentLoc();
    }

    @Override
    public void onPermissionDeclined() {
        Toast.makeText(context, "App won't function optimum without location permission", Toast.LENGTH_LONG).show();
    }
}