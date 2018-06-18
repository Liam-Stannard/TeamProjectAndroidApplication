package com.example.vlady.newair.Data.Sensors;

import android.support.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;

/**
 * Data structure representing a Sensor
 * @author Vladislav Iliev
 */
public class Sensor implements Comparable<Sensor> {
    public enum SensorType {PM10, TEMP, HUMID}

    private final SensorType sensorType;
    private final LatLng latLng;
    private double measure;

    public Sensor(SensorType sensorType, LatLng latLng, double measure) {
        this.sensorType = sensorType;
        this.latLng = latLng;
        this.measure = measure;
    }

    /**
     * Returns the type of a sensor
     * @return the type of a sensor
     */
    public SensorType getSensorType() {
        return this.sensorType;
    }

    /**
     * Returns the coordinates of a sensor
     * @return the coordinates of a sensor
     */
    public LatLng getLatLng() {
        return this.latLng;
    }

    /**
     * Returns the measure of a sensor
     * @return the measure of a sensor
     */
    public double getMeasure() {
        return this.measure;
    }

    @Override
    public int compareTo(@NonNull Sensor otherSensor) {
        return Double.valueOf(this.measure).compareTo(otherSensor.getMeasure());
    }
}
