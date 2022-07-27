package com.xandria.tech.util;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.maps.model.LatLng;
import com.xandria.tech.dto.Location;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * This class is responsible for handling the current user location requests,
 * if there is an issue with current user location, you know where to check
 */
public class LocationUtils {
    private final FusedLocationProviderClient mFusedLocationClient;
    private final ActivityResultLauncher<String> requestPermissionLauncher;
    private final LocationPermissionResult permissionResult;

    private LocationCallback locationCallback;

    private static final String TAG = LocationUtils.class.getName();
    private LatLng latLng;
    private final Context context;
    private Fragment fragment;

    public LocationUtils(Fragment fragment, Context context, LocationPermissionResult permissionResult){
        this.fragment = fragment;
        this.context = context;
        this.permissionResult = permissionResult;

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
        requestPermissionLauncher = fragment.registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
            if (isGranted) {
                mFusedLocationClient.getLastLocation().addOnSuccessListener(fragment.getActivity(), location -> {
                    if (location != null) {
                        latLng = new LatLng(location.getLatitude(), location.getLongitude());
                        permissionResult.onPermissionGranted(getLatLng());
                        permissionResult.onLocationResult(getCurrentLoc());
                        mFusedLocationClient.removeLocationUpdates(locationCallback);
                    }
                });
            } else {
                permissionResult.onPermissionDeclined();
            }
        });

        checkLocationInFragment();
        createLocationRequest();
    }

    private void createLocationRequest() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(Priority.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(20 * 1000);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (android.location.Location location : locationResult.getLocations()) {
                    if (location != null) {
                        latLng = new LatLng(location.getLatitude(), location.getLongitude());
                        permissionResult.onPermissionGranted(getLatLng());
                        permissionResult.onLocationResult(getCurrentLoc());
                    }
                    if (mFusedLocationClient != null) {
                        mFusedLocationClient.removeLocationUpdates(locationCallback);
                    }
                }
            }
        };
    }

    public LocationUtils(AppCompatActivity activity){
        this.context = activity;
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
        this.permissionResult = (LocationPermissionResult) activity;

        requestPermissionLauncher = activity.registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
            if (isGranted) {
                mFusedLocationClient.getLastLocation().addOnSuccessListener(activity, location -> {
                    if (location != null) {
                        latLng = new LatLng(location.getLatitude(), location.getLongitude());
                        permissionResult.onPermissionGranted(getLatLng());
                        permissionResult.onLocationResult(getCurrentLoc());
                        mFusedLocationClient.removeLocationUpdates(locationCallback);
                    }
                });
            } else {
                permissionResult.onPermissionDeclined();
            }
        });

        checkLocationInActivity();
        createLocationRequest();
    }

    private void checkLocationInFragment(){
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ){
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
        } else{
            mFusedLocationClient.getLastLocation().addOnSuccessListener(fragment.getActivity(), location -> {
                if (location != null) {
                    latLng = new LatLng(location.getLatitude(), location.getLongitude());
                    permissionResult.onPermissionGranted(getLatLng());
                    permissionResult.onLocationResult(getCurrentLoc());
                    mFusedLocationClient.removeLocationUpdates(locationCallback);
                }
            });
        }
    }

    private void checkLocationInActivity(){
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
        } else {
            // already permission granted
            mFusedLocationClient.getLastLocation().addOnSuccessListener((Activity) context, location -> {
                if (location != null) {
                    latLng = new LatLng(location.getLatitude(), location.getLongitude());
                    permissionResult.onPermissionGranted(getLatLng());
                    permissionResult.onLocationResult(getCurrentLoc());
                    mFusedLocationClient.removeLocationUpdates(locationCallback);
                }
            });
        }
    }

    public LatLng getLatLng() {
        return latLng;
    }

    /**
     * Get list of address by latitude and longitude
     * @return null or List<Address>
     */
    public List<Address> getGeocoderAddress() {
        if (latLng != null) {

            Geocoder geocoder = new Geocoder(context, Locale.getDefault());

            try {
                /**
                 * Geocoder.getFromLocation - Returns an array of Addresses
                 * that are known to describe the area immediately surrounding the given latitude and longitude.
                 */

                return geocoder.getFromLocation(getLatLng().latitude, getLatLng().longitude, 1);
            } catch (IOException e) {
                //e.printStackTrace();
                Log.e(TAG, "Impossible to connect to Geocoder", e);
            }
        }

        return null;
    }

    /**
     * Try to get AddressLine
     * @return null or addressLine
     */
    public String getAddressLine() {
        List<Address> addresses = getGeocoderAddress();

        if (addresses != null && addresses.size() > 0) {
            Address address = addresses.get(0);

            return address.getAddressLine(0);
        } else {
            return null;
        }
    }

    /**
     * Try to get City
     * @return null or city
     */
    public String getCity() {
        List<Address> addresses = getGeocoderAddress();

        if (addresses != null && addresses.size() > 0) {
            Address address = addresses.get(0);

            return address.getLocality();
        }
        else {
            return null;
        }
    }

    /**
     * Try to get Locality
     * @return null or locality
     */
    public String getLocality() {
        List<Address> addresses = getGeocoderAddress();

        if (addresses != null && addresses.size() > 0) {
            Address address = addresses.get(0);

            return address.getAdminArea();
        }
        else {
            return null;
        }
    }

    /**
     * Try to get Street
     * @return null or street
     */
    public String getStreet() {
        List<Address> addresses = getGeocoderAddress();

        if (addresses != null && addresses.size() > 0) {
            Address address = addresses.get(0);

            return address.getThoroughfare();
        }
        else {
            return null;
        }
    }

    /**
     * Try to get Postal Code
     * @return null or postalCode
     */
    public String getPostalCode() {
        List<Address> addresses = getGeocoderAddress();

        if (addresses != null && addresses.size() > 0) {
            Address address = addresses.get(0);

            return address.getPostalCode();
        } else {
            return null;
        }
    }

    /**
     * Try to get CountryName
     * @return null or postalCode
     */
    public String getCountryName(Context context) {
        List<Address> addresses = getGeocoderAddress();
        if (addresses != null && addresses.size() > 0) {
            Address address = addresses.get(0);

            return address.getCountryName();
        } else {
            return null;
        }
    }

    public Location getCurrentLoc() {
        if (getLatLng() != null) {
            Location location = new Location(
                    getAddressLine(),
                    getLatLng().longitude,
                    getLatLng().latitude
            );
            location.setLocality(getLocality());
            location.setStreetAddress(getStreet());
            location.setPinCode(getPostalCode());
            location.setCity(getCity());
            return location;
        }
        return null;
    }

    public interface LocationPermissionResult{
        void onPermissionGranted(LatLng latLng);
        void onPermissionDeclined();
        default void onLocationResult(Location location){}
    }
}
