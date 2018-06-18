package com.example.vlady.newair.Fragments.Map;

import android.graphics.Color;
import android.view.View;
import android.widget.ImageButton;

import com.example.vlady.newair.Activity.MainActivity;
import com.example.vlady.newair.Data.Sensors.Sensor;
import com.example.vlady.newair.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CircleOptions;

/**
 * Map mode for visualizing sensors. Accessed through the Navbar.
 * @author Vladislav Iliev
 */
class CircleMode {
    private MainActivity activity;
    private MapFragment mapFragment;

    private CircleOptions circleOptions;
    private int[] colors;
    private int[] colorsColorblind;
    private int[] colorStartValues;
    private boolean isColorBlind;

    private ImageButton legendButton;
    private ImageButton refreshButton;

    CircleMode(MapFragment mapFragment) {
        this.activity = (MainActivity) mapFragment.getActivity();
        this.mapFragment = mapFragment;

        this.initialize(this.mapFragment.getView());
        this.addListeners();
    }

    /**
     * Sets the colour blind flag for circle colors.
     * @param isColorBlind if the flag should be on
     */
    void setColorBlind(boolean isColorBlind) {
        this.isColorBlind = isColorBlind;
    }

    /**
     * Initializes all elements on start-up
     * @param view the underlying view
     */
    private void initialize(View view) {
        this.isColorBlind = false;

        this.colors =
                this.mapFragment.getResources().getIntArray(R.array.colors);
        this.colorsColorblind =
                this.mapFragment.getResources().getIntArray(R.array.colors_colorblind);
        this.colorStartValues =
                this.mapFragment.getResources().getIntArray(R.array.color_dividers_int);

        this.circleOptions = new CircleOptions();
        this.circleOptions
                .radius(this.mapFragment.getResources()
                        .getInteger(R.integer.circle_radius_metres));
        this.circleOptions
                .strokeWidth(this.mapFragment.getResources()
                        .getInteger(R.integer.circle_stroke_size));

        this.legendButton = view.findViewById(R.id.legendButton);
        this.refreshButton = view.findViewById(R.id.refreshButton);
    }

    /**
     * Adds button listeners
     */
    private void addListeners() {
        this.refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mapFragment.onRefreshPressed();
            }
        });
        this.legendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mapFragment.onLegendPressed();
            }
        });
    }

    /**
     * Redraws all sensor circles
     * @param googleMap the map
     */
    void updateCircles(GoogleMap googleMap) {
        googleMap.clear();
        for (Sensor sensor : this.activity.getSensorDataManager()
                .getLiveData().getLivePm10Sensors()) {
            this.circleOptions.center(sensor.getLatLng());
            this.circleOptions.fillColor(this.getCircleColor(sensor.getMeasure()));
            googleMap.addCircle(this.circleOptions);
        }
    }

    /**
     * Finds the circle color based on the sensor pollution
     * @param pollution the pollution level
     * @return the color
     */
    private int getCircleColor(double pollution) {
        int newColor;

        if (!this.isColorBlind) {
            newColor = this.colors[0];
        } else {
            newColor = this.colorsColorblind[0];
        }

        int i;
        for (i = 1; i < this.colorStartValues.length; i++) {
            if (pollution >= this.colorStartValues[i]) {
                if (!this.isColorBlind) {
                    newColor = this.colors[i];
                } else {
                    newColor = this.colorsColorblind[i];
                }
            } else {
                break;
            }
        }

        return Color.argb(
                this.activity.getResources().getInteger(R.integer.circle_opacity),
                Color.red(newColor),
                Color.green(newColor), 
                Color.blue(newColor));
    }

    /**
     * Toggles button visibility
     * @param on if should be turned on
     */
    void toggleItems(boolean on) {
        if (on) {
            this.legendButton.setVisibility(View.VISIBLE);
            this.refreshButton.setVisibility(View.VISIBLE);
        } else {
            this.legendButton.setVisibility(View.GONE);
            this.refreshButton.setVisibility(View.GONE);
        }
    }
}