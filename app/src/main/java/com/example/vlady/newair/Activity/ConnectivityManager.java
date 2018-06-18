package com.example.vlady.newair.Activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;

import com.example.vlady.newair.R;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;

import static android.content.Context.LOCATION_SERVICE;

/**
 * Manager of GPS and Internet connectivity
 * @author Liam Stannard, Vladislav Iliev
 */
class ConnectivityManager implements LocationListener {
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10;
    private static final long MIN_TIME_BW_UPDATES = 1;

    private final MainActivity activity;
    private final Context context;

    private LocationManager locationManager;
    private Location location;
    // Current GPS coordinates
    private double latitude = -1;
    private double longitude = -1;

    private boolean currentlyLoading;

    ConnectivityManager(MainActivity activity) {
        this.activity = activity;
        this.context = this.activity.getApplicationContext();
        this.locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);
        this.location = null;
        this.currentlyLoading = false;
    }

    /**
     * Checks whether GPS ot Network Connectivity is enabled
     * @return true if GPS or Network is enabled
     */
    boolean hasLocation() {
        return this.isGpsEnabled() || this.isNetworkEnabled();
    }

    /**
     * Updates and returns the GPS coordinates
     * @return the updated GPS coordinates
     */
    LatLng getCoordinates() {
        this.getLocation();
        return new LatLng(this.latitude, this.longitude);
    }

    /**
     * Checks if GPS is enabled
     * @return if GPS is enabled
     */
    private boolean isGpsEnabled() {
        return this.locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    /**
     * Checks if Network is enabled
     * @return if Network is enabled
     */
    private boolean isNetworkEnabled() {
        return this.locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    /**
     * Requests Phone permission to use GPS
     */
    private void requestGpsPermission() {
        ActivityCompat.requestPermissions(
                this.activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
    }

    /**
     * Requests Network permission to use Network
     */
    private void requestNetworkPermission() {
        ActivityCompat.requestPermissions(
                this.activity, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 2);
    }

    /**
     * Updates the current user location. If none is available, requests permission
     * to obtain it.
     */
    void getLocation() {
        if(!this.currentlyLoading) {
            try {
                this.currentlyLoading = true;

                if (this.isGpsEnabled()) {
                    if (ActivityCompat.checkSelfPermission(
                            this.context,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                                != PackageManager.PERMISSION_GRANTED) {
                        this.requestGpsPermission();
                    }
                    if (this.locationManager != null) {
                        this.locationManager
                                .requestLocationUpdates(
                                        LocationManager.GPS_PROVIDER,
                                        MIN_TIME_BW_UPDATES,
                                        MIN_DISTANCE_CHANGE_FOR_UPDATES,
                                        this);
                        this.location = this.locationManager
                                .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    }
                } else if (this.isNetworkEnabled()) {
                    if (ActivityCompat.checkSelfPermission(
                            this.context,
                            Manifest.permission.ACCESS_COARSE_LOCATION)
                                != PackageManager.PERMISSION_GRANTED) {
                        this.requestNetworkPermission();
                    }
                    if (this.locationManager != null) {
                        this.locationManager.requestLocationUpdates(
                                LocationManager.NETWORK_PROVIDER,
                                MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES,
                                this);
                        this.location = this.locationManager
                                        .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    }
                }

                if (this.location != null) {
                    this.latitude = this.location.getLatitude();
                    this.longitude = this.location.getLongitude();
                }
                this.currentlyLoading = false;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * If no current user location is found, prompts with dialog to enable GPS
     * @return If a current location is present
     */
    private boolean shouldShowGpsDialog() {
        return !this.hasLocation();
    }

    /**
     * Shows a pop-up dialog to enable GPS in Phone settings
     */
    private void showGpsDialog() {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(this.activity);
        alertDialog.setTitle(this.activity.getString(R.string.location_window_title));
        alertDialog.setMessage(this.activity.getString(R.string.location_window_contents));
        alertDialog.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                context.startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                onGpsDialogButtonPressed();
            }
        });
        alertDialog.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                onGpsDialogButtonPressed();
            }
        });
        alertDialog.setCancelable(false);
        alertDialog.show();
    }

    /**
     * Sequence to execute after GPS dialog is gone (the GPS dialog is the last
     * pop-up window)
     */
    private void onGpsDialogButtonPressed() {
        this.activity.onConnectivityDialogsGone();
    }

    @Override
    public void onLocationChanged(Location loc) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    /**
     * True if connection to Urban Observatory's server can be established.
     * @return Can the connection be established
     */
    boolean isConnected() {
        String command = this.activity.getString(R.string.ping_request);
        try {
            return Runtime.getRuntime().exec(command).waitFor() == 0;
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Shows a pop-up dialog requesting Network connectivity
     * @param shouldCheckGps if a follow-up check on GPS is needed
     */
    private void showNetworkDialog(final boolean shouldCheckGps) {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(this.activity);
        alertDialog.setTitle(this.activity.getString(R.string.network_window_title));
        alertDialog.setMessage(this.activity.getString(R.string.network_window_contents));
        alertDialog.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                onNetworkDialogOkPressed(dialog, shouldCheckGps);
            }
        });
        alertDialog.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                onNetworkDialogCancelPressed(dialog);
            }
        });
        alertDialog.setCancelable(false);
        alertDialog.show();
    }

    /**
     * Sequqece to retry checking Network connectivity
     * @param dialog the Dialog pop-up window
     * @param shouldCheckGps if a follow-up GPS check is needed
     */
    private void onNetworkDialogOkPressed(DialogInterface dialog, boolean shouldCheckGps) {
        dialog.dismiss();
        this.checkConnections(shouldCheckGps);
    }

    /**
     * Sequence to execute when User refuses to enable Network connectivity
     * @param dialog the Dialog pop-up window
     */
    private void onNetworkDialogCancelPressed(DialogInterface dialog) {
        dialog.cancel();
        this.activity.finish();
    }

    /**
     * If Internet and/or GPS connectivity is not available, show corresponding
     * pop-up window. If both are available, proceed directly to post-window sequence
     * (e.g. as if the user closes both windows).
     * @param shouldCheckGps Whether GPS should be checked (usually checked only
     *                       on program start-up).
     */
    void checkConnections(boolean shouldCheckGps) {
        if (!this.isConnected()) {
            this.showNetworkDialog(shouldCheckGps);
        } else {
            if (shouldCheckGps && this.shouldShowGpsDialog()) {
                this.showGpsDialog();
            } else {
                this.activity.onConnectivityDialogsGone();
            }
        }
    }
}