package com.example.vlady.newair.Data.UserLocations;

import com.google.android.gms.maps.model.LatLng;

/**
 * Data structure for a saved user location.
 * @author Vladislav Iliev, Bradley Wilsher
 */
public class UserLocation {
    private final String name;
    private final LatLng latLng;

    public UserLocation(String name, LatLng latLng) {
        this.name = name;
        this.latLng = latLng;
    }

    /**
     * Returns the name of the user location
     * @return the name of the user location
     */
    public String getName() {
        return this.name;
    }

    /**
     * Returns the coordinates of the user location
     * @return the coordinates of the user location
     */
    public LatLng getLatLng() {
        return this.latLng;
    }
}
