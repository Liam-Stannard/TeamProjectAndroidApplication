package com.example.vlady.newair.Activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;

import com.example.vlady.newair.Data.FileManager;
import com.example.vlady.newair.Data.Sensors.SensorDataManager;
import com.example.vlady.newair.Data.UserLocations.UserLocationsManager;
import com.example.vlady.newair.R;
import com.google.android.gms.maps.model.LatLng;

/**
 * Central activity. Initializes all parts of the app on startup.
 * @author Vladislav Iliev, Ira Watt
 */
public class MainActivity extends AppCompatActivity {
    /**
     * Persistent user settings, modified in SettingsFragment.
     */
    private SharedPreferences sharedPreferences;

    /**
     * Stores and modifies user locations.
     */
    private UserLocationsManager userLocationsManager;
    /**
     * Reads and saves complex data, such as
     * saved user locations and history sensor data.
     */
    private FileManager fileManager;

    /**
     * Stores and manages sensor data.
     */
    private SensorDataManager sensorDataManager;

    /**
     * Manages visual (Fragment) transitions.
     */
    private ViewManager viewManager;

    /**
     * Manages GPS and Internet connectivity.
     */
    private ConnectivityManager connectivityManager;

    /**
     * Manages automatic updates.
     */
    private Timer timer;

    /**
     * Is true if the app was just started.
     */
    private boolean firstLoad;
    /**
     * Is the app currently loading
     */
    private boolean loading;

    /**
     * Is the app currently loading
     * @return If the app is currently loading
     */
    boolean isLoading() {
        return this.loading;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_main);

        this.initialise();
        this.fileManager.loadFiles();
        this.viewManager.cacheFragments();

        this.connectivityManager.checkConnections(true);
    }

    @Override
    protected void onPause() {
        this.fileManager.saveFiles();
        super.onPause();
    }

    /**
     * Returns the app's Shared Preferences
     * @return the app's Shared Preferences
     */
    public SharedPreferences getSharedPreferences() {
        return this.sharedPreferences;
    }

    /**
     * Returns the app's Screen management utility (see {@link ViewManager})
     * @return the app's Screen management utility
     */
    public ViewManager getViewManager() {
        return this.viewManager;
    }

    /**
     * Returns the app's user locations manager (see {@link UserLocationsManager})
     * @return Returns the app's user locations manager
     */
    public UserLocationsManager getUserLocationsManager() {
        return this.userLocationsManager;
    }

    /**
     * Returns the app's sensor data manager (see {@link SensorDataManager})
     * @return the app's sensor data manager
     */
    public SensorDataManager getSensorDataManager() {
        return this.sensorDataManager;
    }

    /**
     * Sets up the app upon boot-up.
     */
    private void initialise() {
        this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        this.userLocationsManager = new UserLocationsManager(this);
        this.sensorDataManager = new SensorDataManager(this);

        this.fileManager = new FileManager(this);
        this.viewManager = new ViewManager(this);

        this.connectivityManager = new ConnectivityManager(this);
        this.timer = new Timer(this);

        this.firstLoad = true;
        this.loading = false;
    }

    /**
     * Sequence on closure of GPS and Internet check windows
     */
    void onConnectivityDialogsGone() {
        if (this.firstLoad) {
            this.viewManager.showInitialFragment();
            this.connectivityManager.getLocation();
        }
        this.viewManager.showProgressBar();
        this.loadData();
    }

    /**
     * Start fetching sensor data.
     */
    public void loadData() {
        if (!this.connectivityManager.isConnected()) {
            this.connectivityManager.checkConnections(false);
        } else {
            this.loading = true;
            this.timer.stopTimer();
            this.sensorDataManager.loadSensors();
        }
    }

    /**
     * Data is downloaded and displayed. Perform safety checks on Internet (what if network
     * went bad during download), restart auto-download timer.
     */
    public void onLoadedData() {
        if (!this.connectivityManager.isConnected()) {
            this.connectivityManager.checkConnections(false);
        } else {
            this.loading = false;
            this.viewManager.hideProgressBar();

            if (this.firstLoad) {
                this.viewManager.hideWelcomeAssets();
                this.firstLoad = false;
            }

            if (this.timer.isEnabled()) {
                this.timer.startTimer();
            }
        }
    }

    /**
     * Is GPS enabled.
     * @return if GPS is enabled
     */
    public boolean hasLocation() {
        return this.connectivityManager.hasLocation();
    }

    /**
     * Fetches and returns GPS the coordinates.
     * @return the GPS coordinates
     */
    public LatLng getCoordinates() {
        return this.connectivityManager.getCoordinates();
    }

    /**
     * Executed when Timer On/Off switch is modified in Settings
     * @param on if the switch was turned On
     */
    public void onTimerEnabledUpdated(boolean on) {
        this.timer.onTimerEnabledUpdated(on);
    }

    /**
     * Executed when the Timer auto-update interval was modified in Settings
     * (the Timer fetches the new interval directly from its methods)
     */
    public void onTimerIntervalUpdated() {
        this.timer.onTimerIntervalUpdated();
    }
}