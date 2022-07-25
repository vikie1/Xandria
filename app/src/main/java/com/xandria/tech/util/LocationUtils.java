package com.xandria.tech.util;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;

/**
 * This class is responsible for handling the current user location requests,
 * if there is an issue with current user location, you know where to check
 */
public class LocationUtils {
    private FusedLocationProviderClient mFusedLocationClient;
    private ActivityResultLauncher<String> requestPermissionLauncher;

    private final int locationRequestCode = 1000;
    double currentLatitude, currentLongitude;
    private Context context;
    private Fragment fragment;

    public LocationUtils(Fragment fragment, Context context){
        this.fragment = fragment;
        this.context = context;
        requestPermissionLauncher = fragment.registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
            if (isGranted) {
                mFusedLocationClient.getLastLocation().addOnSuccessListener(fragment.getActivity(), location -> {
                    if (location != null) {
                        currentLatitude = location.getLatitude();
                        currentLongitude = location.getLongitude();
                    }
                });
            } else {
                Toast.makeText(fragment.getContext(), "App won't function optimum without location permission", Toast.LENGTH_LONG).show();
            }
        });
    }
    public LocationUtils(AppCompatActivity activity){
        this.context = activity;
    }

    private void checkLocationPermissionInFragment(){
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ){
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
        } else{
            mFusedLocationClient.getLastLocation().addOnSuccessListener(fragment.getActivity(), location -> {
                if (location != null) {
                    currentLatitude = location.getLatitude();
                    currentLongitude = location.getLongitude();
                }
            });
        }
    }
}
