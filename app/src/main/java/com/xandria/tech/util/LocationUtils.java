package com.xandria.tech.util;

import android.Manifest;
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
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * This class is responsible for handling the current user location requests,
 * if there is an issue with current user location, you know where to check
 */
public class LocationUtils {
    private FusedLocationProviderClient mFusedLocationClient;
    private ActivityResultLauncher<String> requestPermissionLauncher;
    private LocationPermissionResult permissionResult;

    private static final String TAG = LocationUtils.class.getName();
    private final int locationRequestCode = 1000;
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
                    }
                });
            } else {
                permissionResult.onPermissionDeclined();
            }
        });

        checkLocationInFragment();
    }
    public LocationUtils(AppCompatActivity activity){
        this.context = activity;
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
    }

    private void checkLocationInFragment(){
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ){
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
        } else{
            mFusedLocationClient.getLastLocation().addOnSuccessListener(fragment.getActivity(), location -> {
                if (location != null) {
                    latLng = new LatLng(location.getLatitude(), location.getLongitude());
                    permissionResult.onPermissionGranted(latLng);
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
    public interface LocationPermissionResult{
        void onPermissionGranted(LatLng latLng);
        void onPermissionDeclined();
    }
}
