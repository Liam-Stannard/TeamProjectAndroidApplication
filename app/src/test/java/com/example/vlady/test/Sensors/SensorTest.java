package com.example.vlady.test.Sensors;
import com.example.vlady.newair.Data.Sensors.Sensor;
import com.google.android.gms.maps.model.LatLng;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class SensorTest {
    private Sensor sensor;
    private Sensor otherSensorLess;
    private Sensor otherSensorEqual;
    private Sensor otherSensorGreater;
    private Sensor nullSensor;
    private Sensor.SensorType sensorType = Sensor.SensorType.PM10;
    private LatLng latLng = new LatLng(10,10);
    private double measure = 30;;

    private final Sensor.SensorType EXPECTED_SENSORTYPE = Sensor.SensorType.PM10;
    private final LatLng EXPECTED_LATLNG= new LatLng(10,10);
    private final double EXPECTED_MEASURE = 30;
    private final double EXPECTED_COMPARISON_GREATERTHAN = 1;
    private final double EXPECTED_COMPARISON_LESSTHAN = -1;
    private final double EXPECTED_COMPARISON_EQUAL = 0;
    private final int DELTA =0;


    @Before
    public void setUp() throws Exception {
        this.sensor = new Sensor(sensorType,latLng,measure);
        otherSensorGreater = new Sensor(Sensor.SensorType.PM10,new LatLng(10,10),10);
        otherSensorLess = new Sensor(Sensor.SensorType.PM10,new LatLng(10,10),40);
        otherSensorEqual = new Sensor(sensorType,latLng,measure);
        nullSensor = null;
    }

    @Test
    public void getSensorType() {
        assertEquals(EXPECTED_SENSORTYPE,sensor.getSensorType());
    }

    @Test
    public void getLatLng() {
        assertEquals(EXPECTED_LATLNG,sensor.getLatLng());
    }

    @Test
    public void getMeasure() {
        assertEquals(EXPECTED_MEASURE,sensor.getMeasure(),DELTA);
    }

    @Test
    public void compareTo_lessThan() {
        assertEquals(EXPECTED_COMPARISON_LESSTHAN,sensor.compareTo(otherSensorLess),DELTA);

    }
    @Test
    public void compareTo_greaterThan() {

        assertEquals(EXPECTED_COMPARISON_GREATERTHAN,sensor.compareTo(otherSensorGreater),DELTA);

    }
    @Test
    public void compareTo_equal() {
        assertEquals(EXPECTED_COMPARISON_EQUAL,sensor.compareTo(otherSensorEqual),DELTA);

    }

    @Test(expected = NullPointerException.class )
    public void compareTo_err() {
        sensor.compareTo(nullSensor);
    }
}