package com.example.vlady.newair.Fragments.Home;

import android.view.View;
import android.widget.TextView;

import com.example.vlady.newair.R;

/**
 * Temperature and Humidity indicators on Home
 * @author Vladislav Iliev
 */
class HomeIndicators {
    private final View homeFragmentView;

    private TextView temperatureText;
    private TextView humidityText;

    HomeIndicators(View homeFragmentView) {
        this.homeFragmentView = homeFragmentView;
        this.initialise();
    }

    /**
     * Initializes all components on start-up.
     */
    private void initialise() {
        this.temperatureText =
                this.homeFragmentView.findViewById(R.id.temperatureText);
        this.humidityText =
                this.homeFragmentView.findViewById(R.id.humidityText);
    }

    /**
     * Updates the temperature indicator
     * @param temperatureNumber the new temperature
     */
    void setTemperature(double temperatureNumber) {
        String textToSet;
        if (temperatureNumber < 0) {
            textToSet = "?";
        } else {
            textToSet = String.valueOf(temperatureNumber);
        }
        this.temperatureText.setText(textToSet);
    }

    /**
     * Updates the humidity indicator
     * @param humidityNumber the new humidity
     */
    void setHumidity(double humidityNumber) {
        String textToSet;
        if (humidityNumber < 0) {
            textToSet = "?";
        } else {
            textToSet = String.valueOf(humidityNumber);
        }
        this.humidityText.setText(textToSet);
    }
}