package com.example.vlady.newair.Fragments.Home;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.vlady.newair.Activity.MainActivity;
import com.example.vlady.newair.Data.Sensors.SensorDataManager;
import com.example.vlady.newair.Data.UserLocations.UserLocation;
import com.example.vlady.newair.Data.UserLocations.UserLocationsManager;
import com.example.vlady.newair.Fragments.Map.MapFragment;
import com.example.vlady.newair.R;

import java.util.List;

/**
 * Home screen
 * @author Vladislav Iliev
 */
public class HomeFragment extends Fragment {
    private MainActivity activity;

    private HomeBackground background;
    private HomeCarousel carousel;
    private HomeIndicators indicators;

    private int[] colorStartValues;
    private int[] colors;
    private int[] colorsColorblind;
    private String[] healthMessages;

    private int currentColorArrayPosition;
    private boolean isColorBlind;
    private boolean isCurrentlyGray;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.activity = (MainActivity) this.getActivity();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Context context = new ContextThemeWrapper(getActivity(), R.style.HomeFragment);
        LayoutInflater localInflater = inflater.cloneInContext(context);
        return localInflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.initialise();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        this.setColorBlind(
                    this.activity.getSharedPreferences().getBoolean(
                            this.activity.getString(
                                    R.string.color_blind_switch_key), false));
        this.addMultipleLocations(
                    this.activity.getUserLocationsManager().getUserLocations());
        this.updateScreen();
    }

    /**
     * Initializes all components on start-up.
     */
    private void initialise() {
        View view = this.getView();

        this.background = new HomeBackground(view);
        new HomeSettingsButtons(this);
        this.carousel = new HomeCarousel(this, view);
        this.indicators = new HomeIndicators(view);

        this.colorStartValues = this.background.getColorStartValues();
        this.colors = this.background.getColors();
        this.colorsColorblind = this.background.getColorsColorblind();
        this.healthMessages = this.carousel.getHealthMessagesArray();

        this.isColorBlind = false;
        this.isCurrentlyGray = false;
    }

    /**
     * Flag for enabling the colour-blind palette. Called in Settings.
     * @param isColorBlind True if colour-blind is toggled on
     */
    public void setColorBlind(boolean isColorBlind) {
        this.isColorBlind = isColorBlind;
    }

    /**
     * Sets the background color. The correct color is determined by the index of
     * the pollution on the threshold array (see {@link HomeFragment#setColorAndHealthMessage(double)})
     */
    public void updateBackgroundPalette() {
        if (!this.isCurrentlyGray) {
            int newColor = !this.isColorBlind
                    ? this.colors[this.currentColorArrayPosition]
                    : this.colorsColorblind[this.currentColorArrayPosition];
            this.background.setColor(newColor);
        }
    }

    /**
     * Add a user location to the carousel.
     * @param userLocation the new user location
     */
    public void addUserLocation(UserLocation userLocation) {
        this.carousel.addLocation(userLocation);
        // The new location's position in the carousel is always the last
        this.carousel.getCarousel().setCurrentItem(
                this.carousel.getLocationsList().size() - 1);
        this.updateScreen();
    }

    /**
     * Add a list of user locations to the carousel.
     * @param userLocations the list of user locations
     */
    public void addMultipleLocations(List<UserLocation> userLocations) {
        this.carousel.addLocations(userLocations);
        this.updateScreen();
    }

    /**
     * Remove a user location from the carousel.
     * @param userLocation the user location
     */
    public void removeUserLocation(UserLocation userLocation) {
        this.carousel.removeLocation(userLocation);
        this.updateScreen();
    }

    /**
     * Remove all user locations from the carousel.
     */
    public void removeAllUserLocations() {
        this.carousel.removeAllLocations();
        this.updateScreen();
    }

    /**
     * Updates the home screen according to the currently selected location
     * on the carousel.
     */
    public void updateScreen() {
        this.carousel.checkArrowsVisibility();

        SensorDataManager sensorDataManager = this.activity.getSensorDataManager();
        int currentCarouselPosition = this.carousel.getCurrentPosition();

        double newPollution = sensorDataManager.getLiveData()
                        .getPollutionLevels()[currentCarouselPosition];
        double newTemperature = sensorDataManager.getLiveData()
                        .getTemperatureLevels()[currentCarouselPosition];
        double newHumidity = sensorDataManager.getLiveData()
                        .getHumidityLevels()[currentCarouselPosition];

        this.setNumbers(newPollution, newTemperature, newHumidity);
        this.setColorAndHealthMessage(newPollution);
        this.setDistanceMessage(currentCarouselPosition);
    }

    /**
     * Updates all number indicators.
     * @param newPollution the new pollution level
     * @param newTemperature the new temperature
     * @param newHumidity the new humidity
     */
    private void setNumbers(double newPollution, double newTemperature,
                            double newHumidity) {
        this.carousel.setPollution(newPollution);
        this.indicators.setTemperature(newTemperature);
        this.indicators.setHumidity(newHumidity);
    }

    /**
     * Updates the background color and health message. Combine both, because they use
     * the same threshold array for scaling pollution levels.
     * @param newPollution the new pollution
     */
    private void setColorAndHealthMessage(double newPollution) {
        int newColor;
        String newHealthMessage;
        this.currentColorArrayPosition = 0;

        if (newPollution < 0) {
            newColor = this.activity.getResources().getColor(R.color.grey);
            newHealthMessage = this.activity.getString(R.string.data_not_avaiable);
            this.isCurrentlyGray = true;
        } else {
            for (int i = 1; i < this.colorStartValues.length; i++) {
                if (newPollution >= this.colorStartValues[i]) {
                    this.currentColorArrayPosition = i;
                } else {
                    break;
                }
            }
            newColor = (!this.isColorBlind
                    ? this.colors[this.currentColorArrayPosition]
                    : this.colorsColorblind[this.currentColorArrayPosition]);

            newHealthMessage = this.healthMessages[this.currentColorArrayPosition];
            this.isCurrentlyGray = false;
        }

        this.background.setColor(newColor);
        this.carousel.setHealthMessage(newHealthMessage);
    }

    /**
     * Update the distance to the nearest sensor on the bottom of the screen.
     * @param currentCarouselPosition the currently selected carousel position (the distance
     *                                value is already calculated, need to determine
     *                                for which location to get it)
     */
    private void setDistanceMessage(int currentCarouselPosition) {
        // No nearest distance for overall City levels
        if (currentCarouselPosition == this.activity.getResources()
                            .getInteger(R.integer.city_home_array_position)) {
            this.carousel.disableDistanceMessage();
        } else {
            if (!this.carousel.isClosestSensorMessageEnabled()) {
                this.carousel.enableDistanceMessage();
            }

            UserLocationsManager userLocationsManager =
                    this.activity.getUserLocationsManager();

            if (currentCarouselPosition == this.activity.getResources()
                    .getInteger(R.integer.nearby_home_array_position)) {
                this.carousel.setDistanceMessage(userLocationsManager.getGpsDistance());
            } else {
                // On the carousel, user distances are pushed to the right
                // by default locations. Need to compensate the difference
                int initialLocationsNumber =
                        this.activity.getResources().getStringArray(
                                R.array.initial_locations).length;
                this.carousel.setDistanceMessage(
                        userLocationsManager.getUserDistance(
                                userLocationsManager.getUserLocations()
                                        .get(currentCarouselPosition - initialLocationsNumber)));
            }
        }
    }

    /**
     * Triggered when user presses Add Location Btn on Home
     */
    void onAddPressed() {
        this.activity.getViewManager().getMapFragment().setMode(
                MapFragment.MapMode.ADD_LOCATION_MODE);
        this.activity.getViewManager().showFragment(
                this.activity.getViewManager().getMapFragment());
    }

    /**
     * Triggered when user presses Refresh
     */
    void onRefreshPressed() {
        this.activity.loadData();
    }

    /**
     * Triggered when user presses Settings
     */
    void onSettingsPressed() {
        this.activity.getViewManager().showFragment(
                this.activity.getViewManager().getSettingsFragment());
    }
}