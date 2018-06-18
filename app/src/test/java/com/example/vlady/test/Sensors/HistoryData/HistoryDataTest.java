package com.example.vlady.test.Sensors.HistoryData;

import com.example.vlady.newair.Data.Sensors.HistoryData.HistoryData;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Calendar;
import java.util.Date;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class HistoryDataTest {
    private Date[] dates;
    private double[] pm25;
    private double[] pm10;
    private double[] o3;
    private double pm25_value = 10;
    private double pm10_value = 23;
    private double o3_value = 34;
    private Date date;
    private final int DELTA = 0;
    @Mock
    private HistoryData historyData;
    @Before
    public void setUp() throws Exception {
        historyData = mock(HistoryData.class);
        pm10 = new double[100];
        pm25 = new double[100];
        o3 = new double[100];
        dates = new Date[100];
        dates[0] = date;
        pm10[0] = pm10_value;
        pm25[0] = pm25_value;
        o3[0] = o3_value;
    }


    @Test
    public void getDates() {
        when(historyData.getDates()).thenReturn(dates);
        assertArrayEquals(dates,historyData.getDates());
        verify(historyData).getDates();


    }

    @Test
    public void getPm25() {
        when(historyData.getPm25()).thenReturn(pm25);
        assertArrayEquals(pm25,historyData.getPm25(),DELTA);
        verify(historyData).getPm25();

    }

    @Test
    public void getPm10() {
        when(historyData.getPm10()).thenReturn(pm10);
        assertArrayEquals(pm10,historyData.getPm10(),DELTA);
        verify(historyData).getPm10();

    }

    @Test
    public void getO3() {
        when(historyData.getO3()).thenReturn(o3);
        assertArrayEquals(o3,historyData.getO3(),DELTA);
        verify(historyData).getO3();

    }
}