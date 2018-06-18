package com.example.vlady.newair.Data.Sensors.LiveData;

import android.support.annotation.Nullable;

import com.example.vlady.newair.Activity.MainActivity;
import com.example.vlady.newair.Data.Sensors.Sensor;
import com.example.vlady.newair.Data.Sensors.SensorDataManager;
import com.example.vlady.newair.Data.UserLocations.UserLocation;
import com.example.vlady.newair.R;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.example.vlady.newair.Data.Sensors.Sensor.SensorType.HUMID;
import static com.example.vlady.newair.Data.Sensors.Sensor.SensorType.PM10;
import static com.example.vlady.newair.Data.Sensors.Sensor.SensorType.TEMP;

/**
 * Stores live sensor data for display at Home.
 * @author Vladislav Iliev, Liam Stannard
 */
public class LiveData {
    private MainActivity activity;
    private LiveSensorLists liveSensorLists;
    private final SensorDataManager sensorDataManager;

    // Live data to be displayed on Home carousel
    private double[] pollutionLevels;
    private double[] temperatureLevels;
    private double[] humidityLevels;

    /**
     * Number of default locations (Nearby and City)
     */
    private int initialLocationsNumber;

    public LiveData(SensorDataManager sensorDataManager) {
        this.sensorDataManager = sensorDataManager;
        this.initialize();
        this.initializeDataLists();
    }

    /**
     * Returns the live PM10 readings
     * @return the live PM10 readings
     */
    public double[] getPollutionLevels() {
        return this.pollutionLevels;
    }

    /**
     * Returns the live Temperature readings
     * @return the live Temperature readings
     */
    public double[] getTemperatureLevels() {
        return this.temperatureLevels;
    }

    /**
     * Returns the live Humidity readings
     * @return the live Humidity readings
     */
    public double[] getHumidityLevels() {
        return this.humidityLevels;
    }

    /**
     * Returns the live PM10 sensors
     * @return the live PM10 sensors
     */
    public List<Sensor> getLivePm10Sensors(){
        return Collections.unmodifiableList(this.liveSensorLists.getPm10Sensors());
    }

    /**
     * Initializes all components on start-up
     */
    private void initialize() {
        this.activity = this.sensorDataManager.getActivity();
        this.liveSensorLists = new LiveSensorLists();
        this.initialLocationsNumber = this.activity.getResources()
                                .getStringArray(R.array.initial_locations).length;
    }

    /**
     * Initializes all live data lists
     */
    public void initializeDataLists() {
        int allLocationsNumber =
                this.initialLocationsNumber
                + this.activity.getUserLocationsManager().getUserLocations().size();

        this.pollutionLevels = new double[allLocationsNumber];
        this.temperatureLevels = new double[allLocationsNumber];
        this.humidityLevels = new double[allLocationsNumber];

        // Fill all lists with a negative number (avoid null pointers when
        // using data)
        int invalidInteger =
                this.activity.getResources().getInteger(R.integer.invalid_integer);
        for (int i = 0; i < allLocationsNumber; i++) {
            this.pollutionLevels[i] = invalidInteger;
            this.temperatureLevels[i] = invalidInteger;
            this.humidityLevels[i] = invalidInteger;
        }
    }

    /**
     * Updates all displayed live sensor data
     */
    public void updateData() {
        this.updateInitialLocations();
        this.updateUserLocations();
    }

    /**
     * Calculate measurements for default locations (Nearby and City)
     */
    private void updateInitialLocations() {
        int cityPosition = 
                this.activity.getResources().getInteger(R.integer.city_home_array_position);
        int nearbyPosition = this.activity.getResources()
                        .getInteger(R.integer.nearby_home_array_position);

        this.pollutionLevels[cityPosition] = SensorDataManager.roundToFirstDigit(
                            this.liveSensorLists.approxOfList(PM10));
        this.temperatureLevels[cityPosition] = SensorDataManager.roundToFirstDigit(
                            this.liveSensorLists.approxOfList(TEMP));
        this.humidityLevels[cityPosition] = SensorDataManager.roundToFirstDigit(
                            this.liveSensorLists.approxOfList(HUMID));

        // If GPS is not available, fill with an invalid integer
        int invalidInteger =
                this.activity.getResources().getInteger(R.integer.invalid_integer);
        if (!this.activity.hasLocation()) {
            this.pollutionLevels[nearbyPosition] = invalidInteger;
            this.temperatureLevels[nearbyPosition] = invalidInteger;
            this.humidityLevels[nearbyPosition] = invalidInteger;
            this.activity.getUserLocationsManager().setGpsDistance(invalidInteger);
        } else {
            LatLng coordinates = this.activity.getCoordinates();

            Sensor closestPollutionSensor =
                    this.findClosestGpsSensor(coordinates, PM10);
            Sensor closestTemperatureSensor =
                    this.findClosestGpsSensor(coordinates, TEMP);
            Sensor closestHumiditySensor =
                    this.findClosestGpsSensor(coordinates, HUMID);

            this.pollutionLevels[nearbyPosition] =
                    closestPollutionSensor == null
                    ? invalidInteger
                    : SensorDataManager.roundToFirstDigit(closestPollutionSensor.getMeasure());
            this.temperatureLevels[nearbyPosition] =
                    closestTemperatureSensor == null
                    ? invalidInteger
                    : SensorDataManager.roundToFirstDigit(closestTemperatureSensor.getMeasure());
            this.humidityLevels[nearbyPosition] =
                    closestHumiditySensor == null
                    ? invalidInteger
                    : SensorDataManager.roundToFirstDigit(closestHumiditySensor.getMeasure());
        }
    }

    /**
     * Calculate measurements for user locations
     */
    private void updateUserLocations() {
        // Fetch the saved locations
        List<UserLocation> userLocations =
                this.activity.getUserLocationsManager().getUserLocations();
        // Prepare an invalid integer in case a location cannot be calculated
        int invalidInteger = 
                this.activity.getResources().getInteger(R.integer.invalid_integer);

        for (int i = 0; i < userLocations.size(); i++) {
            Sensor closestPollutionSensor =
                    this.findClosestUserLocationSensor(userLocations.get(i), PM10);
            Sensor closestTemperatureSensor =
                    this.findClosestUserLocationSensor(userLocations.get(i), TEMP);
            Sensor closestHumiditySensor =
                    this.findClosestUserLocationSensor(userLocations.get(i), HUMID);

            // User locations' positions in the Home carousel is pushed right
            // by the default locations (Nearby and City), add the difference
            // to get the actual user location position
            int carouselPosition = this.initialLocationsNumber + i;
            // If the closest sensor is not available
            // (i.e. no sensors are found), fill with an invalid integer
            this.pollutionLevels[carouselPosition] = 
                    closestPollutionSensor == null
                    ? invalidInteger
                    : SensorDataManager.roundToFirstDigit(closestPollutionSensor.getMeasure());

            this.temperatureLevels[carouselPosition] =
                    closestTemperatureSensor == null
                    ? invalidInteger
                    : SensorDataManager.roundToFirstDigit(closestTemperatureSensor.getMeasure());

            this.humidityLevels[carouselPosition] = 
                    closestHumiditySensor == null
                    ? invalidInteger
                    : SensorDataManager.roundToFirstDigit(closestHumiditySensor.getMeasure());
        }
    }

    /**
     * Add a new live sensor reading
     * @param sensor the new reading
     */
    public void addSensor(Sensor sensor) {
        this.liveSensorLists.addSensor(sensor);
    }

    /**
     * Remove all live data
     */
    public void clearAllSensorLists() {
        this.liveSensorLists.clearAllLists();
    }

    /**
     * Might need to sort a list by measurements (e.g. Map screen - important
     * to place more polluted circles on top of lower polluted).
     * @param sensorType the sensor list type
     */
    public void sortSensorList(Sensor.SensorType sensorType) {
        this.liveSensorLists.sortSensorList(sensorType);
    }

    /**
     * Finds closest sensor to the current user location
     * @param coordinates the user coordinates
     * @param sensorType the sensor type
     * @return the Sensor
     */
    @Nullable
    private Sensor findClosestGpsSensor(LatLng coordinates, Sensor.SensorType sensorType) {
        List<Sensor> sensorList;
        switch (sensorType) {
            case PM10:
                sensorList = liveSensorLists.getPm10Sensors();
                break;
            case TEMP:
                sensorList = liveSensorLists.getTempSensors();
                break;
            case HUMID:
                sensorList = liveSensorLists.getHumidSensors();
                break;
            default:
                sensorList = new ArrayList<>();
                break;
        }
        if (sensorList.isEmpty()) {
            return null;
        }

        // Get the distance to the first sensor as a starting point
        Sensor closestSensor = sensorList.get(0);
        double minDistance =
                SphericalUtil.computeDistanceBetween(coordinates, closestSensor.getLatLng());

        // Iterate over all sensors, store if a closer one is found
        Sensor sensorBuffer;
        double distanceBuffer;
        for (int i = 1; i < sensorList.size(); i++) {
            sensorBuffer = sensorList.get(i);
            distanceBuffer =
                    SphericalUtil.computeDistanceBetween(coordinates, sensorBuffer.getLatLng());

            if (distanceBuffer < minDistance) {
                closestSensor = sensorBuffer;
                minDistance = distanceBuffer;
            }
        }

        // If the sensor is PM10, store the closest distance additionally
        // (will be displayed at bottom of Home screen)
        if (sensorType.equals(PM10)) {
            this.activity.getUserLocationsManager().setGpsDistance((int) minDistance);
        }

        return closestSensor;
    }

    /**
     * Find the closest sensor to a custom user location by pollutant type
     * @param userLocation the user location
     * @param sensorType   the pollutant type
     * @return the closest sensor
     */
    @Nullable
    private Sensor findClosestUserLocationSensor(UserLocation userLocation,
                                                 Sensor.SensorType sensorType) {
        List<Sensor> sensorList;
        switch (sensorType) {
            case PM10:
                sensorList = liveSensorLists.getPm10Sensors();
                break;
            case TEMP:
                sensorList = liveSensorLists.getTempSensors();
                break;
            case HUMID:
                sensorList = liveSensorLists.getHumidSensors();
                break;
            default:
                sensorList = new ArrayList<>();
                break;
        }
        if (sensorList.isEmpty()) {
            return null;
        }

        // Get the distance to the first sensor as a starting point
        Sensor closestSensor = sensorList.get(0);
        double minDistance = 
                SphericalUtil.computeDistanceBetween(
                        userLocation.getLatLng(),
                        closestSensor.getLatLng());

        // Iterate over all sensors, store if a closer one is found
        Sensor sensorBuffer;
        double distanceBuffer;
        for (int i = 1; i < sensorList.size(); i++) {
            sensorBuffer = sensorList.get(i);
            distanceBuffer =
                    SphericalUtil.computeDistanceBetween(
                            userLocation.getLatLng(),
                            sensorBuffer.getLatLng());

            if (distanceBuffer < minDistance) {
                closestSensor = sensorBuffer;
                minDistance = distanceBuffer;
            }
        }

        // If the sensor is PM10, store the closest distance additionally
        // (will be displayed at bottom of Home screen)
        if (sensorType.equals(PM10)) {
            this.activity.getUserLocationsManager()
                            .setUserDistance(userLocation, (int) minDistance);
        }

        return closestSensor;
    }
}