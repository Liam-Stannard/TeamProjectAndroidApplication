package com.example.vlady.test.Sensors;



import org.junit.Before;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.example.vlady.newair.Activity.MainActivity;
import com.example.vlady.newair.Data.Sensors.HistoryData.HistoryData;
import com.example.vlady.newair.Data.Sensors.LiveData.LiveData;
import com.example.vlady.newair.Data.Sensors.LiveData.LiveJSONLoader;
import com.example.vlady.newair.Data.Sensors.SensorDataManager;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.*;
import static org.mockito.Mockito.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class SensorDataManagerTest {


    @Mock
    private MainActivity mainActivity;
    @Mock
    private  SensorDataManager sensorDataManager;
    @Mock
    private HistoryData historyData;
    @Mock
    private  LiveData liveData;
    @Mock
    private LiveJSONLoader liveJSONLoader;

    final int DELTA =0;
    @Before
    public void setup() throws Exception {
        sensorDataManager = mock(SensorDataManager.class);
        mainActivity = sensorDataManager.getActivity();
        historyData = mock(HistoryData.class);
        liveData = mock(LiveData.class);
        sensorDataManager.loadSensors();
    }

    @Test
    public void getActivity() {
        assertEquals(mainActivity,sensorDataManager.getActivity());
    }

    @Test
    public void getLiveData() {
        when(sensorDataManager.getLiveData()).thenReturn(liveData);
        assertEquals(liveData,sensorDataManager.getLiveData());
        verify(sensorDataManager).getLiveData();
    }

    @Test
    public void getHistoryData() {
        when(sensorDataManager.getHistoryData()).thenReturn(historyData);
        assertEquals(historyData,sensorDataManager.getHistoryData());
        verify(sensorDataManager).getHistoryData();
    }

    @Test
    public void roundToFirstDigit() {
        double startValue = 15.5;
        double expectedValue = 15.5;
        assertEquals(expectedValue,SensorDataManager.roundToFirstDigit(startValue),DELTA);
    }

}