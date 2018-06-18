package com.example.vlady.newair.Data;

import android.util.Log;

import com.example.vlady.newair.Activity.MainActivity;
import com.example.vlady.newair.Data.Sensors.HistoryData.HistoryData;
import com.example.vlady.newair.Data.UserLocations.UserLocation;
import com.example.vlady.newair.R;
import com.google.android.gms.maps.model.LatLng;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

/**
 * Management utility for reading/writing permanent data on file storate
 * @author Vladislav Iliev, Ira Watt
 */
public class FileManager {
    private final MainActivity activity;
    private final File locationsFile;
    private final File historyDataFile;
    private SimpleDateFormat CONDENSED_DATE_FORMAT;

    public FileManager(MainActivity mainActivity) {
        this.activity = mainActivity;
        this.locationsFile = 
                new File(this.activity.getFilesDir(),
                         this.activity.getString(R.string.user_locations_file));
        this.historyDataFile =
                new File(this.activity.getFilesDir(),
                         this.activity.getString(R.string.history_data_file));

        CONDENSED_DATE_FORMAT = new SimpleDateFormat(this.activity
                                .getString(R.string.date_condensed_format), Locale.UK);
    }

    /**
     * Load all data files upon start-up. If none are found, create them.
     */
    public void loadFiles() {
        this.checkUserLocationsFile();
        this.readUserLocationsFile();

        this.checkHistoryFile();
        this.readHistoryDataFile();
    }

    /**
     * Save the current data permanently to files.
     */
    public void saveFiles() {
        this.saveUserLocations();
        this.saveHistoryData();
    }

    /**
     * If a user locations file doesn't exist, create one
     */
    private void checkUserLocationsFile() {
        try {
            if (locationsFile.createNewFile()) {
                Log.e(this.activity
                            .getString(R.string.user_locations_file_tag),
                      this.activity
                            .getString(R.string.user_locations_file_already_exists_message));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Read the stored user locations
     */
    private void readUserLocationsFile() {
        /*
         * User locations are saved like:
         *
         * UserLocation ->  name
         *              ->  latitude
         *              ->  longitude
         *
         * UserLocation ->  ...
         */
        try {
            List<UserLocation> newLocationsList = new LinkedList<>();
            ObjectInputStream objectInputStream = 
                    new ObjectInputStream(new FileInputStream(this.locationsFile));

            int namesPositionInArray = 
                    this.activity.getResources()
                            .getInteger(R.integer.user_locations_array_position_names);
            int latitudesPositionInArray = 
                    this.activity.getResources()
                            .getInteger(R.integer.user_locations_array_position_latitude);
            int longitudesPositionInArray = 
                    this.activity.getResources()
                            .getInteger(R.integer.user_locations_array_position_longitude);

            String[][] newLocations = (String[][]) objectInputStream.readObject();
            for (String[] location : newLocations) {
                String newName = location[namesPositionInArray];
                LatLng newLatLng = 
                        new LatLng(Double.valueOf(location[latitudesPositionInArray]),
                                   Double.valueOf(location[longitudesPositionInArray]));

                newLocationsList.add(new UserLocation(newName, newLatLng));
            }

            objectInputStream.close();
            this.activity.getUserLocationsManager().onUserLocationsLoaded(newLocationsList);

        } catch (IOException e) {
            Log.e(this.activity.getString(R.string.user_locations_file_tag),
                  this.activity.getString(R.string.user_locations_file_empty));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Save the current user locations to the file
     */
    private void saveUserLocations() {
        /*
         * User locations are saved like:
         *
         * UserLocation ->  name
         *              ->  latitude
         *              ->  longitude
         *
         * UserLocation ->  ...
         */
        try {
            List<UserLocation> userLocations =
                    this.activity.getUserLocationsManager().getUserLocations();

            int arrayItemsNumber = 
                    this.activity.getResources()
                            .getInteger(R.integer.user_locations_array_items_number);
            int namesPositionInArray =
                    this.activity.getResources()
                            .getInteger(R.integer.user_locations_array_position_names);
            int latitudesPositionInArray = 
                    this.activity.getResources()
                            .getInteger(R.integer.user_locations_array_position_latitude);
            int longitudesPositionInArray = 
                    this.activity.getResources()
                            .getInteger(R.integer.user_locations_array_position_longitude);

            String[][] toSave = new String[userLocations.size()][arrayItemsNumber];
            for (int i = 0; i < toSave.length; i++) {
                toSave[i][namesPositionInArray] = userLocations.get(i).getName();
                toSave[i][latitudesPositionInArray] = 
                        String.valueOf(userLocations.get(i).getLatLng().latitude);
                toSave[i][longitudesPositionInArray] =
                        String.valueOf(userLocations.get(i).getLatLng().longitude);
            }

            ObjectOutputStream objectOutputStream = 
                    new ObjectOutputStream(new FileOutputStream(this.locationsFile));
            objectOutputStream.writeObject(toSave);
            objectOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * If a history data file doesn't exist, create one
     */
    private void checkHistoryFile() {
        try {
            if (historyDataFile.createNewFile()) {
                Log.e(this.activity
                            .getString(R.string.history_data_file_tag),
                      this.activity
                            .getString(R.string.history_data_file_already_exists_message));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Read the history data file.
     */
    private void readHistoryDataFile() {
        /*
         * History data is saved like:
         *
         * Date ->  pm25
         *      ->  pm10
         *      ->  o3
         *
         * Date ->  ...
         */
        try {
            ObjectInputStream objectInputStream = 
                    new ObjectInputStream(new FileInputStream(this.historyDataFile));
            int historyDatesNumber = 
                    this.activity.getResources()
                            .getInteger(R.integer.history_dates_number);

            // Don't need the closest saved date (today may not be over yet)
            Date[] newDates = new Date[historyDatesNumber - 1];
            double[] newPm25 = new double[newDates.length];
            double[] newPm10 = new double[newDates.length];
            double[] newO3 = new double[newDates.length];

            String[][] savedHistoryDataArray = 
                    (String[][]) objectInputStream.readObject();
            int datesPositionInArray = 
                    this.activity.getResources()
                            .getInteger(R.integer.history_data_array_position_dates);
            int pm25PositionInArray = 
                    this.activity.getResources()
                            .getInteger(R.integer.history_data_array_position_pm25);
            int pm10PositionInArray = 
                    this.activity.getResources()
                            .getInteger(R.integer.history_data_array_position_pm10);
            int o3PositionInArray = 
                    this.activity.getResources()
                            .getInteger(R.integer.history_data_array_position_o3);

            for (int i = 0; i < savedHistoryDataArray.length; i++) {
                newDates[i] = CONDENSED_DATE_FORMAT
                                .parse(savedHistoryDataArray[i][datesPositionInArray]);
                newPm25[i] = Double.valueOf(savedHistoryDataArray[i][pm25PositionInArray]);
                newPm10[i] = Double.valueOf(savedHistoryDataArray[i][pm10PositionInArray]);
                newO3[i] = Double.valueOf(savedHistoryDataArray[i][o3PositionInArray]);
            }

            objectInputStream.close();
            this.activity.getSensorDataManager()
                            .onHistoryDataLoaded(newDates, newPm25, newPm10, newO3);
        } catch (IOException e) {
            Log.e(
                    this.activity.getString(R.string.history_data_file_tag),
                    this.activity.getString(R.string.history_data_file_empty));
        } catch (ParseException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Save the current history data to the file
     */
    private void saveHistoryData() {
        /*
         * History data is saved like:
         *
         * Date ->  pm25
         *      ->  pm10
         *      ->  o3
         *
         * Date ->  ...
         */
        // Don't save data if one of the days has an invalid reading (-1)
        if (!this.historyDataSafeToSave()) {
            return;
        }

        try {
            HistoryData historyData = 
                    this.activity.getSensorDataManager().getHistoryData();
            int historyDatesNumber = 
                    this.activity.getResources()
                            .getInteger(R.integer.history_dates_number);
            int arrayItemsNumber = 
                    this.activity.getResources()
                            .getInteger(R.integer.history_data_array_items_number);

            String[][] toSave = new String[historyDatesNumber - 1][arrayItemsNumber];
            int datesPositionInArray =
                    this.activity.getResources()
                            .getInteger(R.integer.history_data_array_position_dates);
            int pm25PositionInArray = 
                    this.activity.getResources()
                            .getInteger(R.integer.history_data_array_position_pm25);
            int pm10PositionInArray = 
                    this.activity.getResources()
                            .getInteger(R.integer.history_data_array_position_pm10);
            int o3PositionInArray = 
                    this.activity.getResources()
                            .getInteger(R.integer.history_data_array_position_o3);

            // The saved dates are 6 (because Today is not saved), so offset the
            // array position by 1
            int dayInSourceArray;
            for (int i = 0; i < toSave.length; i++) {
                dayInSourceArray = i + 1;
                toSave[i][datesPositionInArray] =
                        CONDENSED_DATE_FORMAT.format(
                                    historyData.getDates()[dayInSourceArray]);
                toSave[i][pm25PositionInArray] = 
                        String.valueOf(historyData.getPm25()[dayInSourceArray]);
                toSave[i][pm10PositionInArray] = 
                        String.valueOf(historyData.getPm10()[dayInSourceArray]);
                toSave[i][o3PositionInArray] = 
                        String.valueOf(historyData.getO3()[dayInSourceArray]);
            }

            ObjectOutputStream objectOutputStream = 
                    new ObjectOutputStream(new FileOutputStream(this.historyDataFile));
            objectOutputStream.writeObject(toSave);
            objectOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * If one of the days has an invalid reading on any polluter (-1), the data
     * should not be saved
     * @return Whether the data is safe to save
     */
    private boolean historyDataSafeToSave() {
        HistoryData historyData = this.activity.getSensorDataManager().getHistoryData();

        boolean safeToSave = true;
        int INVALID_INTEGER = 
                this.activity.getResources().getInteger(R.integer.invalid_integer);

        for (int i = 0; i < historyData.getDates().length; i++) {
            if (historyData.getPm25()[i] == INVALID_INTEGER
                    || historyData.getPm10()[i] == INVALID_INTEGER
                    || historyData.getO3()[i] == INVALID_INTEGER) {
                safeToSave = false;
                break;
            }
        }

        return safeToSave;
    }
}