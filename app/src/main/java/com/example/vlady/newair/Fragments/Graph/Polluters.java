package com.example.vlady.newair.Fragments.Graph;

import android.view.View;
import android.widget.TextView;

import com.example.vlady.newair.R;

/**
 * Detailed polluter indicators under Graph
 * @author Vladislav Iliev
 */
class Polluters {
    private TextView pm25;
    private TextView pm10;
    private TextView o3;

    Polluters(GraphFragment graphFragment) {
        this.initialize(graphFragment.getView());
    }

    /**
     * Initializes all elements on start-up
     * @param view the underlying view
     */
    private void initialize(View view) {
        this.pm25 = view.findViewById(R.id.pm25);
        this.pm10 = view.findViewById(R.id.pm10);
        this.o3 = view.findViewById(R.id.o3);
    }

    /**
     * Updates the number statistics
     * @param pm25 the new PM25 level
     * @param pm10 the new PM10 level
     * @param o3 the new O3 level
     */
    void update(double pm25, double pm10, double o3) {
        String undefined = "?";
        this.pm25.setText(pm25 < 0 ? undefined : String.valueOf(pm25));
        this.pm10.setText(pm10 < 0 ? undefined : String.valueOf(pm10));
        this.o3.setText(o3 < 0 ? undefined : String.valueOf(o3));
    }
}
