package com.example.vlady.test.Sensors.LiveData;

import com.example.vlady.newair.Data.Sensors.LiveData.LiveData;
import com.example.vlady.newair.Data.Sensors.Sensor;
import com.example.vlady.newair.Data.Sensors.SensorDataManager;
import com.google.android.gms.maps.model.LatLng;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
public class LiveDataTest {
    @Mock
    private LiveData liveData;
    private Sensor nullSensor = new Sensor(null,new LatLng(10,10),10);
    private Sensor pm10Sensor = new Sensor(Sensor.SensorType.PM10,new LatLng(10,10),10);
    private Sensor pm10ExtraSensor = new Sensor(Sensor.SensorType.PM10,new LatLng(100,100),1);

    private Sensor tempSensor = new Sensor(Sensor.SensorType.TEMP,new LatLng(10,10),10);
    private  Sensor tempExtraSensor = new Sensor(Sensor.SensorType.TEMP,new LatLng(10,10),1);

    private Sensor humidSensor = new Sensor(Sensor.SensorType.HUMID,new LatLng(10,10),10);
    private Sensor humidExtraSensor = new Sensor(Sensor.SensorType.HUMID,new LatLng(10,10),1);
    private double[]pollutionLevels;
    private double[]tempLevels;
    private double[]humidLevels;
    private List<Sensor> livepm10Sensors;
    private final int EXPECTED_ARRAY_LENGTH = 0;
    private final int DELTA = 0;
    private final double temp_value = 10;
    private final double humid_value = 233;
    @Before
    public void setUp() throws Exception {
        liveData = mock(LiveData.class);
        pollutionLevels = new double[100];
        tempLevels = new double[100];
        humidLevels = new double[100];
        tempLevels[0]= temp_value;
        humidLevels[0]=humid_value;
        livepm10Sensors = new ArrayList<>();
        livepm10Sensors.add(pm10Sensor);
        livepm10Sensors.add(pm10ExtraSensor);
    }

    @Test
    public void getPollutionLevels() {
        when(liveData.getPollutionLevels()).thenReturn(pollutionLevels);
        assertArrayEquals(pollutionLevels,liveData.getPollutionLevels(),DELTA);
        verify(liveData).getPollutionLevels();


    }

    @Test
    public void getTemperatureLevels() {
        when(liveData.getTemperatureLevels()).thenReturn(tempLevels);
        assertArrayEquals(tempLevels,liveData.getTemperatureLevels(),DELTA);
        verify(liveData).getTemperatureLevels();


    }

    @Test
    public void getHumidityLevels() {
        when(liveData.getHumidityLevels()).thenReturn(humidLevels);
        assertArrayEquals(humidLevels,liveData.getHumidityLevels(),DELTA);
        verify(liveData).getHumidityLevels();


    }

    @Test
    public void getLivePm10Sensors() {
        when(liveData.getLivePm10Sensors()).thenReturn(livepm10Sensors);
        assertEquals(livepm10Sensors,liveData.getLivePm10Sensors());
        verify(liveData).getLivePm10Sensors();


    }


    @Test
    public void addSensor_SUCCESS_PM10() {
        liveData.clearAllSensorLists();
        liveData.addSensor(pm10Sensor);
        pollutionLevels[0] = pm10Sensor.getMeasure();
        when(liveData.getPollutionLevels()).thenReturn(pollutionLevels);
        assertEquals(pm10Sensor.getMeasure(),liveData.getPollutionLevels()[0],DELTA);
        verify(liveData).getPollutionLevels();


    }

    @Test
    public void addSensor_SUCCESS_TEMP() {
        liveData.clearAllSensorLists();
        liveData.addSensor(tempSensor);
        tempLevels[0] = tempSensor.getMeasure();
        when(liveData.getTemperatureLevels()).thenReturn(tempLevels);
        assertEquals(tempSensor.getMeasure(),liveData.getTemperatureLevels()[0],DELTA);
        verify(liveData).getTemperatureLevels();


    }

    @Test
    public void addSensor_SUCCESS_HUMID() {
        liveData.clearAllSensorLists();
        liveData.addSensor(humidSensor);
        humidLevels[0] = humidSensor.getMeasure();
        when(liveData.getHumidityLevels()).thenReturn(humidLevels);
        assertEquals(humidSensor.getMeasure(),liveData.getHumidityLevels()[0],DELTA);
        verify(liveData).getHumidityLevels();

    }



}