package com.example.vlady.newair.Data.Sensors.LiveData;

import com.example.vlady.newair.Data.Sensors.Sensor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Stores all Live sensor data
 * @author Vladislav Iliev, Liam Stannard
 */
class LiveSensorLists {
    private List<Sensor> pm10Sensors;
    private List<Sensor> tempSensors;
    private List<Sensor> humidSensors;

    LiveSensorLists() {
        this.initialize();
    }

    /**
     * Returns the live PM10 data
     * @return the live PM10 data
     */
    List<Sensor> getPm10Sensors() {
        return Collections.unmodifiableList(this.pm10Sensors);
    }

    /**
     * Returns the live Temperature data
     * @return the live Temperature data
     */
    List<Sensor> getTempSensors() {
        return Collections.unmodifiableList(this.tempSensors);
    }

    /**
     * Returns the live Humidity data
     * @return the live Humidity data
     */
    List<Sensor> getHumidSensors() {
        return Collections.unmodifiableList(this.humidSensors);
    }

    /**
     * Initialize the lists on start-up
     */
    private void initialize() {
        this.pm10Sensors = new ArrayList<>();
        this.tempSensors = new ArrayList<>();
        this.humidSensors = new ArrayList<>();
    }

    /**
     * Add a new reading
     * @param sensor the reading type
     */
    void addSensor(Sensor sensor) {
        List<Sensor> listToAddTo;

        switch (sensor.getSensorType()) {
            case PM10:
                listToAddTo = this.pm10Sensors;
                break;
            case TEMP:
                listToAddTo = this.tempSensors;
                break;
            case HUMID:
                listToAddTo = this.humidSensors;
                break;
            default:
                listToAddTo = new ArrayList<>();
                break;
        }
        listToAddTo.add(sensor);
    }

    /**
     * Sort readings
     * @param sensorType the reading type
     */
    void sortSensorList(Sensor.SensorType sensorType) {
        List<Sensor> listToSort;

        switch (sensorType) {
            case PM10:
                listToSort = this.pm10Sensors;
                break;
            case TEMP:
                listToSort = this.tempSensors;
                break;
            case HUMID:
                listToSort = this.humidSensors;
                break;
            default:
                listToSort = new ArrayList<>();
                break;
        }

        Collections.sort(listToSort);
    }

    /**
     * Find the approximate of all readings (used on Home)
     * @param sensorType the reading type
     * @return the approximate
     */
    double approxOfList(Sensor.SensorType sensorType) {
        List<Sensor> listToApproximate;
        switch (sensorType) {
            case PM10:
                listToApproximate = this.pm10Sensors;
                break;
            case TEMP:
                listToApproximate = this.tempSensors;
                break;
            case HUMID:
                listToApproximate = this.humidSensors;
                break;
            default:
                listToApproximate = null;
                break;
        }

        if (listToApproximate == null || listToApproximate.isEmpty()) {
            return -1;
        }

        double approx = 0;
        for (Sensor sensor : listToApproximate) {
            approx += sensor.getMeasure();
        }
        approx = approx / listToApproximate.size();

        return approx;
    }

    /**
     * Remove all live readings
     */
    void clearAllLists() {
        this.pm10Sensors.clear();
        this.tempSensors.clear();
        this.humidSensors.clear();
    }
}