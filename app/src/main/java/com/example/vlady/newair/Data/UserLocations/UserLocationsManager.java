package com.example.vlady.newair.Data.UserLocations;

import com.example.vlady.newair.Activity.MainActivity;
import com.example.vlady.newair.R;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Manages all user locations (adds and removes them), and the distances to the cosest
 * sensors
 * @author Vladislav Iliev, Bradley Wilsher
 */
public class UserLocationsManager {
    private final MainActivity activity;

    private List<UserLocation> userLocations;
    // Distances to nearest sensor (displayed at bottom of Home)
    private Map<UserLocation, Integer> userDistances;
    private int gpsDistance;

    public UserLocationsManager(MainActivity mainActivity) {
        this.activity = mainActivity;
        this.userLocations = new LinkedList<>();
        this.userDistances = new HashMap<>();
        this.gpsDistance = this.activity.getResources().getInteger(R.integer.invalid_integer);
    }

    /**
     * Returns all saved user locations
     * @return all saved user locations
     */
    public List<UserLocation> getUserLocations() {
        return Collections.unmodifiableList(this.userLocations);
    }

    /**
     * Returns the distance to the closest GPS sensor
     * @return the distance to the closest GPS sensor
     */
    public int getGpsDistance() {
        return this.gpsDistance;
    }

    /**
     * Returns the distance between a user location and its closest pollution sensor
     * @param userLocation the user location
     * @return the distance
     */
    public int getUserDistance(UserLocation userLocation) {
        return this.userDistances.get(userLocation);
    }

    /**
     * Sets the distance between the GPS location and the closest pollution sensor
     * @param distance the distance
     */
    public void setGpsDistance(int distance) {
        this.gpsDistance = distance;
    }

    /**
     * Sets the distance between a user location and its closest pollution sensor
     * @param userLocation the user location
     */
    public void setUserDistance(UserLocation userLocation, int distance) {
        this.userDistances.put(userLocation, distance);
    }

    /**
     * Sequence to be executed when all user locations are loaded on boot-up
     * @param userLocations the user locations list
     */
    public void onUserLocationsLoaded(List<UserLocation> userLocations) {
        this.userLocations.addAll(userLocations);
        int invalidInteger = 
                this.activity.getResources()
                        .getInteger(R.integer.invalid_integer);
        // Distances to sensors are not saved on phone, fill them with
        // an integer to avoid null pointers on use
        for (UserLocation userLocation : userLocations) {
            this.userDistances.put(userLocation, invalidInteger);
        }
        this.onUserLocationsModified();
    }

    /**
     * Adds a user location and reflects the update on the app
     * @param userLocation the user location
     */
    public void addUserLocation(UserLocation userLocation) {
        this.userLocations.add(userLocation);
        // Distances are updated in this.onUserLocationsModified()
        this.userDistances.put(
                userLocation,
                this.activity.getResources().getInteger(
                        R.integer.invalid_integer));

        this.onUserLocationsModified();
        this.activity.getViewManager()
                .getHomeFragment().addUserLocation(userLocation);
    }

    /**
     * Removes a user location and reflects the update on the app
     * @param userLocation the user location
     */
    public void removeUserLocation(UserLocation userLocation) {
        this.userLocations.remove(userLocation);
        this.userDistances.remove(userLocation);

        this.onUserLocationsModified();

        this.activity.getViewManager()
                .getHomeFragment().removeUserLocation(userLocation);
        this.activity.getViewManager()
                .getMapFragment().refreshMarkers();
    }

    /**
     * Removes all user locations and reflects the update on the app
     */
    public void removeAllUserLocations() {
        this.userLocations.clear();
        this.userDistances.clear();

        this.onUserLocationsModified();

        this.activity.getViewManager().getHomeFragment().removeAllUserLocations();
        this.activity.getViewManager().getMapFragment().refreshMarkers();
    }

    /**
     * Sequence to be execute on addition/removal of a user location
     */
    private void onUserLocationsModified() {
        this.activity.getSensorDataManager().getLiveData().initializeDataLists();
        this.activity.getSensorDataManager().getLiveData().updateData();
    }

    /**
     * Checks if a user location exists by comparing names
     * @param newLocationName the name to compare
     * @return whether a location with that name already exists
     */
    public boolean checkLocationExists(String newLocationName) {
        for (String initialLocation : this.activity.getResources()
                                            .getStringArray(R.array.initial_locations)) {
            if (initialLocation.toLowerCase().equals(newLocationName.toLowerCase())) {
                return true;
            }
        }

        for (UserLocation userLocation : this.userLocations) {
            if (userLocation.getName().toLowerCase().equals(newLocationName.toLowerCase())) {
                return true;
            }
        }

        return false;
    }
}