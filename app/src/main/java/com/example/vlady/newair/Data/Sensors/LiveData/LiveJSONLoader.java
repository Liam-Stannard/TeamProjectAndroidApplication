package com.example.vlady.newair.Data.Sensors.LiveData;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import com.example.vlady.newair.Activity.MainActivity;
import com.example.vlady.newair.Data.Sensors.Sensor;
import com.example.vlady.newair.R;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Accessor to Urban Observatory's live data
 * @author Liam Stannard, Vladislav Iliev
 */
public class LiveJSONLoader extends AsyncTask<Void, Void, List<Sensor>> {

    public interface OnLiveSensorsDownloadListener {
        void onLiveSensorsDownloaded(List<Sensor> sensorList);
    }
    private final OnLiveSensorsDownloadListener delegate;
    // Keywords to match the Observatory's API
    private String OBSERVATORY_URL;
    private String API_KEY;
    private String VARIABLE_QUERY_FORMAT;
    private String BUFFER_QUERY_FORMAT;
    private String TWO_VARIABLE_FORMAT;
    private String IS_ACTIVE_KEY;
    private String DATA_KEY;
    private String GEOM_KEY;
    private String COORDINATES_KEY;
    private String PM10_VARIABLE;
    private String TEMP_VARIABLE;
    private String HUMID_VARIABLE;
    // No measurements over a limit
    private int MEASUREMENTS_LIMIT_UPPER;
    private String NO_JSON_OBJECT_FOUND_LOG_TITLE;
    private String NO_JSON_OBJECT_FOUND_LOG_CONTENTS;
    // Temperature and Humidity sensors require different URL builds, fetch them
    // separately with different URLs
    private List<Sensor> sensorsPm10;
    private List<Sensor> sensorsTempHumid;
    public LiveJSONLoader(MainActivity activity,
                          OnLiveSensorsDownloadListener onLiveSensorsDownloadListener) {
        this.initializeStrings(activity);
        this.initializeLists();
        this.delegate = onLiveSensorsDownloadListener;
    }

    /**
     * Initialize all URL strings
     * @param activity the activity to access resources from
     */
    private void initializeStrings(Activity activity) {
        OBSERVATORY_URL = activity.getString(R.string.live_address);
        API_KEY = activity.getString(R.string.api_key);
        VARIABLE_QUERY_FORMAT = activity.getString(R.string.variable_format);
        BUFFER_QUERY_FORMAT = activity.getString(R.string.buffer_query);
        TWO_VARIABLE_FORMAT = activity.getString(R.string.two_variables_format);

        IS_ACTIVE_KEY = activity.getString(R.string.json_is_active_key);
        DATA_KEY = activity.getString(R.string.json_data_key);
        GEOM_KEY = activity.getString(R.string.json_geom_key);
        COORDINATES_KEY = activity.getString(R.string.json_coordinates_key);

        PM10_VARIABLE = activity.getString(R.string.pm10_variable);
        TEMP_VARIABLE = activity.getString(R.string.temperature_variable);
        HUMID_VARIABLE = activity.getString(R.string.humidity_variable);
        MEASUREMENTS_LIMIT_UPPER = 
                activity.getResources().getInteger(R.integer.measurement_limit_upper);

        NO_JSON_OBJECT_FOUND_LOG_TITLE =
                activity.getString(R.string.no_json_object_found_log_title);
        NO_JSON_OBJECT_FOUND_LOG_CONTENTS =
                activity.getString(R.string.no_json_object_found_log_contents);
    }

    /**
     * Initialize all buffer lists
     */
    private void initializeLists() {
        this.sensorsPm10 = new ArrayList<>();
        this.sensorsTempHumid = new ArrayList<>();
    }

    @Override
    protected List<Sensor> doInBackground(Void... voids) {
        this.getPM10();
        this.getTemperatureAndHumidity();
        // No need to separate measurements anymore, they will be redistributed
        // at  LiveSensorLists.addSensor()
        this.sensorsPm10.addAll(this.sensorsTempHumid);
        return this.sensorsPm10;
    }

    @Override
    protected void onPostExecute(List<Sensor> sensorList) {
        this.delegate.onLiveSensorsDownloaded(sensorList);
        super.onPostExecute(sensorList);
    }

    /**
     * Return the URL contents as String
     * @param url the URL
     * @return the contents as String
     */
    private String readHTML(URL url) {
        String urlContents = "";

        try {
            BufferedReader bufferedReader =
                    new BufferedReader(new InputStreamReader(url.openStream()));
            StringBuilder stringBuilder = new StringBuilder();

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }

            urlContents = stringBuilder.toString();
            bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return urlContents;
    }

    /**
     * Fetches all live PM10 pollution data
     */
    private void getPM10() {
        this.sensorsPm10.addAll(this.getSensors(this.readHTML(this.buildPm10URL())));
    }

    /**
     * Sets up the live PM10 pollution URL
     * @return the live PM10 pollution URL
     */
    private URL buildPm10URL() {
        URL url = null;
        try {
            String urlString = OBSERVATORY_URL
                               + API_KEY
                               + String.format(VARIABLE_QUERY_FORMAT, PM10_VARIABLE);
            url = new URL(urlString);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    /**
     * Fetches all live Temperature and Humidity data
     */
    private void getTemperatureAndHumidity() {
        // Get Temp and Humid sensors only at PM10 locations, otherwise
        // the connection takes too long to load all
        for (Sensor pm10sensor : this.sensorsPm10) {
            this.sensorsTempHumid.addAll(
                    this.getSensors(
                            this.readHTML(
                                    this.buildURLTempHumid(
                                            pm10sensor.getLatLng()))));
        }
    }

    /**
     * Sets up the live Temperature and Humidity URL
     * @return the live Temperature and Humidity URL
     */
    private URL buildURLTempHumid(LatLng pm10LatLong) {
        URL url = null;
        try {
            String urlString =
                    OBSERVATORY_URL
                    + API_KEY
                    + String.format(
                            VARIABLE_QUERY_FORMAT,
                            String.format(
                                    TWO_VARIABLE_FORMAT,
                                    TEMP_VARIABLE,
                                    HUMID_VARIABLE))
                    + String.format(
                            BUFFER_QUERY_FORMAT,
                            pm10LatLong.longitude,
                            pm10LatLong.latitude,
                            5);

            url = new URL(urlString);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    /**
     * Fill the data arrays with live data
     * @param jsonString the HTML JSON string
     */
    private List<Sensor> getSensors(String jsonString) {
        List<Sensor> readSensors = new ArrayList<>();

        JSONArray sensors;
        JSONObject sensor;

        Iterator typesIterator;
        String type;

        JSONArray coordinatesArray;
        double latitude;
        double longitude;

        double measure;

        try {
            sensors = new JSONArray(jsonString);

            for (int i = 0; i < sensors.length(); i++) {
                sensor = sensors.getJSONObject(i);

                if (sensor.getBoolean(IS_ACTIVE_KEY)) {
                    typesIterator = sensor.getJSONObject(DATA_KEY).keys();

                    while (typesIterator.hasNext()) {
                        try {
                            type = (String) typesIterator.next();
                            JSONObject data =
                                    sensor.getJSONObject(DATA_KEY)
                                            .getJSONObject(type).getJSONObject(DATA_KEY);

                            measure = data.getDouble(data.keys().next());
                            if (measure < 0 || measure > MEASUREMENTS_LIMIT_UPPER) {
                                continue;
                            }

                            coordinatesArray = 
                                    sensor.getJSONObject(GEOM_KEY)
                                            .getJSONArray(COORDINATES_KEY);
                            latitude = coordinatesArray.getDouble(1);
                            longitude = coordinatesArray.getDouble(0);

                            readSensors.add(new Sensor(this.parseType(type),
                                                       new LatLng(latitude, longitude),
                                                       measure));
                        } catch (IllegalArgumentException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        } catch (JSONException e) {
            Log.e(NO_JSON_OBJECT_FOUND_LOG_TITLE, NO_JSON_OBJECT_FOUND_LOG_CONTENTS);
        }

        return readSensors;
    }

    /**
     * Get the Sensor {@link Sensor.SensorType} from a String
     * @param jsonType the String to parse
     * @return the parsed Enum
     * @throws IllegalArgumentException when cannot parse
     */
    private Sensor.SensorType parseType(String jsonType) throws IllegalArgumentException {
        String type = jsonType.toLowerCase();
        Sensor.SensorType sensorType;

        if (type.equals(PM10_VARIABLE.toLowerCase())) {
            sensorType = Sensor.SensorType.PM10;
        } else if (type.equals(TEMP_VARIABLE.toLowerCase())) {
            sensorType = Sensor.SensorType.TEMP;
        } else if (type.equals(HUMID_VARIABLE.toLowerCase())) {
            sensorType = Sensor.SensorType.HUMID;
        } else {
            throw new IllegalArgumentException();
        }

        return sensorType;
    }
}