package com.example.vlady.newair.Fragments.Map;

import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vlady.newair.Activity.MainActivity;
import com.example.vlady.newair.Data.UserLocations.UserLocation;
import com.example.vlady.newair.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Map mode for adding a new custom User Location. Accessed through pressing the
 * Add Location button at top of Home.
 * @author Vladislav Iliev
 */
class AddLocationMode {
    private MainActivity activity;
    private MapFragment mapFragment;

    private MarkerOptions markerOptions;
    private Marker marker;

    private TextView addLocationText;
    private FloatingActionButton addBtn;

    AddLocationMode(MapFragment mapFragment) {
        this.activity = (MainActivity) mapFragment.getActivity();
        this.mapFragment = mapFragment;

        this.initialize(this.mapFragment.getView());
        this.addBtnListeners();
    }

    /**
     * Initializes all elements on start-up
     * @param view the underlying view
     */
    private void initialize(View view) {
        this.addLocationText = view.findViewById(R.id.addLocationText);
        this.addBtn = view.findViewById(R.id.addLocationBtn);
        this.initializeMarker();
    }

    /**
     * Initializes the marker shown on map touch
     */
    private void initializeMarker() {
        this.markerOptions = new MarkerOptions();
        this.markerOptions
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        this.markerOptions.draggable(false);
        this.markerOptions.visible(true);
    }

    /**
     * Refreshes all location markers
     * @param googleMap the map
     */
    void refreshMarkers(GoogleMap googleMap) {
        googleMap.clear();

        MarkerOptions marker = new MarkerOptions();
        marker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN));
        for (UserLocation userLocation : this.activity.getUserLocationsManager()
                .getUserLocations()) {
            marker.position(userLocation.getLatLng());
            marker.title(userLocation.getName());
            googleMap.addMarker(marker);
        }
    }

    /**
     * Adds listeners to map touch events
     * @param googleMap the map
     */
    void addMapListeners(final GoogleMap googleMap) {
        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng touchCoordinates) {
                if (marker != null) {
                    marker.remove();
                }

                markerOptions.position(touchCoordinates);
                marker = googleMap.addMarker(markerOptions);

                addBtn.setVisibility(View.VISIBLE);
            }
        });
    }

    /**
     * Removes map listeners.
     * @param googleMap the map
     */
    void removeMapListeners(GoogleMap googleMap) {
        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng touchCoordinates) {
            }
        });
    }

    private void addBtnListeners() {
        this.addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isMarkerInNewcastle()) {
                    Toast.makeText(
                            activity,
                            activity.getString(R.string.location_outside_newcastle),
                            Toast.LENGTH_LONG)
                            .show();
                } else {
                    new AddLocationDialog(mapFragment);
                }
            }
        });
    }

    /**
     * Triggered when a location is successfully accepted to be added to the map
     * @param locationName the location name
     * @return the location coordinates
     */
    LatLng onLocationAdded(String locationName) {
        LatLng markerPosition = this.marker.getPosition();
        this.marker
                .setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN));
        this.marker.setTitle(locationName);
        this.marker = null;
        this.addBtn.setVisibility(View.GONE);

        return markerPosition;
    }

    /**
     * Checks if the map touch marker is within Newcastle. Prevents distant locations
     * from being added.
     * @return True if marker is within Newcastle.
     */
    private boolean isMarkerInNewcastle() {
        double newcastleMinLat = Double.valueOf(
                        this.activity.getString(R.string.newcastle_min_latitude));
        double newcastleMaxLat = Double.valueOf(
                        this.activity.getString(R.string.newcastle_max_latitude));
        double newcastleMinLong = Double.valueOf(
                        this.activity.getString(R.string.newcastle_min_longitude));
        double newcastleMaxLong = Double.valueOf(
                        this.activity.getString(R.string.newcastle_max_longitude));

        double markerLat = this.marker.getPosition().latitude;
        double markerLong = this.marker.getPosition().longitude;

        return (markerLat >= newcastleMinLat
                && markerLat <= newcastleMaxLat
                && markerLong >= newcastleMinLong
                && markerLong <= newcastleMaxLong);
    }

    /**
     * Toggles the Add Button and Description Text
     * @param on if should turn them On
     */
    void toggleItems(boolean on) {
        if (on) {
            this.addLocationText.setVisibility(View.VISIBLE);
        } else {
            this.addLocationText.setVisibility(View.GONE);
            this.addBtn.setVisibility(View.GONE);
        }
    }
}