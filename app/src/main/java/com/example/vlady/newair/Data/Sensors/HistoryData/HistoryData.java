package com.example.vlady.newair.Data.Sensors.HistoryData;

import com.example.vlady.newair.Activity.MainActivity;
import com.example.vlady.newair.Data.Sensors.SensorDataManager;
import com.example.vlady.newair.R;

import java.util.Calendar;
import java.util.Date;

/**
 * Stores all history sensor data.
 * @author Vladislav Iliev, Liam Stannard
 */
public class HistoryData {
    private final MainActivity activity;

    /**
     * The past week, including today
     */
    private Date[] dates;
    private double[] pm25;
    private double[] pm10;
    private double[] o3;

    public HistoryData(SensorDataManager sensorDataManager) {
        this.activity = sensorDataManager.getActivity();
        this.initializeDatesList();
        this.initializeLists();
    }

    /**
     * Set a Date's time to 00:00:00.
     * Makes easier comparisons between weekdays.
     * @param date the new Date
     * @return the new Date with time set to 00:00:00
     */
    static Date dateNullifyTime(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        return calendar.getTime();
    }

    /**
     * Returns the past week dates
     * @return the past week dates
     */
    public Date[] getDates() {
        return this.dates;
    }

    /**
     * Returns the past week PM25 readings
     * @return the past week PM25 readings
     */
    public double[] getPm25() {
        return this.pm25;
    }

    /**
     * Returns the past week PM10 readings
     * @return the past week PM10 readings
     */
    public double[] getPm10() {
        return this.pm10;
    }

    /**
     * Returns the past week O3 readings
     * @return the past week O3 readings
     */
    public double[] getO3() {
        return this.o3;
    }

    /**
     * Calculate the previous week's dates
     */
    private void initializeDatesList() {
        this.dates =
                new Date[this.activity.getResources()
                        .getInteger(R.integer.history_dates_number)];

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        dates[0] = HistoryData.dateNullifyTime(calendar.getTime());
        for (int i = 1; i < this.dates.length; i++) {
            calendar.add(Calendar.DAY_OF_YEAR, -1);
            dates[i] = HistoryData.dateNullifyTime(calendar.getTime());
        }
    }

    /**
     * Initialize the pollution storage lists
     */
    private void initializeLists() {
        this.pm25 = new double[this.dates.length];
        this.pm10 = new double[this.dates.length];
        this.o3 = new double[this.dates.length];

        // Fill with invalid integers (to avoid null exception on potential
        // use of values)
        int invalidInteger =
                this.activity.getResources().getInteger(R.integer.invalid_integer);
        for (int i = 0; i < this.pm25.length; i++) {
            this.pm25[i] = invalidInteger;
            this.pm10[i] = invalidInteger;
            this.o3[i] = invalidInteger;
        }
    }
}