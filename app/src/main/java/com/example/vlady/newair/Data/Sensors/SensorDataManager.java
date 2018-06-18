package com.example.vlady.newair.Data.Sensors;

import android.os.AsyncTask;

import com.example.vlady.newair.Activity.MainActivity;
import com.example.vlady.newair.Data.Sensors.HistoryData.HistoryData;
import com.example.vlady.newair.Data.Sensors.HistoryData.HistoryJSONLoader;
import com.example.vlady.newair.Data.Sensors.LiveData.LiveData;
import com.example.vlady.newair.Data.Sensors.LiveData.LiveJSONLoader;
import com.example.vlady.newair.R;

import java.util.Date;
import java.util.List;

import static com.example.vlady.newair.Data.Sensors.Sensor.SensorType.PM10;

/**
 * Manages sensor data. Loads and distributes data for storage.
 * @author Vladislav Iliev
 */
public class SensorDataManager implements LiveJSONLoader.OnLiveSensorsDownloadListener,
        HistoryJSONLoader.OnHistorySensorsDownloadListener {
    private final MainActivity activity;
    private LiveData liveData;
    private HistoryData historyData;

    private LiveJSONLoader liveJSONLoader;
    private HistoryJSONLoader historyJSONLoader;

    public SensorDataManager(MainActivity activity) {
        this.activity = activity;
        this.initialize();
    }

    /**
     * Returns the app's activity
     * @return the app's activity
     */
    public MainActivity getActivity() {
        return this.activity;
    }

    /**
     * Returns all downloaded live data
     * @return all downloaded live data
     */
    public LiveData getLiveData() {
        return this.liveData;
    }

    /**
     * Returns all downloaded history data
     * @return all downloaded history data
     */
    public HistoryData getHistoryData() {
        return this.historyData;
    }

    /**
     * Initializes all components upon start-up
     */
    private void initialize() {
        this.liveData = new LiveData(this);
        this.historyData = new HistoryData(this);
    }

    /**
     * Checks whether the app is currently downloading data
     * @return whether the app is currently downloading data
     */
    private boolean isLoadingSensors() {
        boolean isLoadingLive =
                this.liveJSONLoader != null
                && this.liveJSONLoader.getStatus() == AsyncTask.Status.RUNNING;
        boolean isLoadingHistory = 
                this.historyJSONLoader != null
                && this.historyJSONLoader.getStatus() == AsyncTask.Status.RUNNING;

        return (isLoadingLive || isLoadingHistory);
    }

    /**
     * Start fetching live and history data
     */
    public void loadSensors() {
        if (!this.isLoadingSensors()) {
            this.loadLiveSensors();
        }
    }

    /**
     * Start fetching live data
     */
    private void loadLiveSensors() {
        this.activity.getViewManager().showProgressBar();
        this.liveData.clearAllSensorLists();

        this.liveJSONLoader = new LiveJSONLoader(this.activity, this);
        this.liveJSONLoader.execute();
    }

    /**
     * Start fetching history data
     */
    private void loadHistorySensors() {
        int endIndex = 0;
        int invalidInteger = 
                this.activity.getResources().getInteger(R.integer.invalid_integer);
        // Only fetch data for days whose measurement is an invalid integer (-1).
        // Store the furthest date with said invalid measurement (the URL query only
        // works with a starting and an ending date).
        // The starting date is always Today.
        // Doesn't matter which polluter array is used
        for (int i = 0; i < this.historyData.getPm25().length; i++) {
            if (this.historyData.getPm25()[i] == invalidInteger) {
                endIndex = i;
            }
        }

        Date startDate = this.historyData.getDates()[0];
        Date endDate = this.historyData.getDates()[endIndex];

        this.historyJSONLoader = 
                new HistoryJSONLoader(this.activity, this, startDate, endDate);
        this.historyJSONLoader.execute();
    }

    @Override
    public void onLiveSensorsDownloaded(List<Sensor> sensorList) {
        for (Sensor sensor : sensorList) {
            this.liveData.addSensor(sensor);
        }

        // Sort the sensors displayed at Map. Need the circle colors to
        // overlap properly
        this.liveData.sortSensorList(PM10);
        this.liveData.updateData();

        this.activity.getViewManager().getHomeFragment().updateScreen();
        this.activity.getViewManager().getMapFragment().updateCircles();

        this.loadHistorySensors();
    }

    @Override
    public void onHistorySensorsDownloaded(double[][] measures) {
        int approximatesPm25Position = activity.getResources()
                        .getInteger(R.integer.json_history_approximates_pm25_position);
        int approximatesPm10Position = activity.getResources()
                        .getInteger(R.integer.json_history_approximates_pm10_position);
        int approximatesO3Position = activity.getResources()
                        .getInteger(R.integer.json_history_approximates_o3_position);

        // 'i' will be each consecutive day of the past 7 days
        for (int i = 0; i < measures.length; i++) {
            this.historyData.getPm25()[i] = measures[i][approximatesPm25Position];
            this.historyData.getPm10()[i] = measures[i][approximatesPm10Position];
            this.historyData.getO3()[i] = measures[i][approximatesO3Position];
        }

        this.activity.getViewManager().getGraphFragment().update();
        this.activity.onLoadedData();
    }

    /**
     * Sequence to be executed when history data is downloaded
     * @param savedDates the sensor dates (last week dates)
     * @param savedPm25 the history PM25 data
     * @param savedPm10 the history PM10 data
     * @param savedO3 the history O3 data
     */
    public void onHistoryDataLoaded(Date[] savedDates, double[] savedPm25, 
                                    double[] savedPm10, double[] savedO3) {

        int invalidInteger = this.activity.getResources().getInteger(R.integer.invalid_integer);
        int startIndex = invalidInteger;

        // 'i' will be each consecutive day of the past 7 days (except today,
        // because today may not be over yet).
        // Look at the dates of the current past week, and the dates of
        // the previously saved on phone past week. Need to load data only for dates
        // that overlap. As soon as a date matches, all other dates should
        // also match, so store only the starting index.
        for (int i = 1; i < this.historyData.getDates().length; i++) {
            if (this.historyData.getDates()[i].equals(savedDates[0])) {
                startIndex = i;
                break;
            }
        }

        // If a matching date has been found, transfer data for all matching dates
        if (startIndex != invalidInteger) {
            for (int i = startIndex; i < this.historyData.getDates().length; i++) {
                this.historyData.getPm25()[i] = savedPm25[i - startIndex];
                this.historyData.getPm10()[i] = savedPm10[i - startIndex];
                this.historyData.getO3()[i] = savedO3[i - startIndex];
            }
        }
    }

    /**
     * Rounds a floating-point number to its first digit
     * @param value the number
     * @return the rounded to first digit result
     */
    public static double roundToFirstDigit(double value) {
        return (double) Math.round(value * 10) / 10;
    }
}