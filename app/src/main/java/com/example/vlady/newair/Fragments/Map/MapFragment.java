package com.example.vlady.newair.Fragments.Map;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.vlady.newair.Activity.MainActivity;
import com.example.vlady.newair.Data.UserLocations.UserLocation;
import com.example.vlady.newair.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

import java.util.LinkedList;
import java.util.List;

/**
 * Map screen.
 * @author Vladislav Iliev
 */
public class MapFragment extends Fragment implements OnMapReadyCallback {
    /**
     * Map has two modes:
     * 1. Circle mode - when accessed through navbar
     * 2. Add location mode - when accessed through Add Location
     * button on Home screen
     */
    public enum MapMode {
        CIRCLE_MODE, ADD_LOCATION_MODE
    }

    private MainActivity activity;
    private MapView mapView;
    private GoogleMap googleMap;
    private CircleMode circleMode;
    private AddLocationMode addLocationMode;
    private MapMode mode;

    private View legendBackground;
    private List<View> legendBoxes;
    private List<View> legendTexts;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.activity = (MainActivity) this.getActivity();
        this.mode = MapMode.CIRCLE_MODE;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_map, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.initializeMap(view, savedInstanceState);
        this.initializeLegend();
        this.toggleMapLegend();

        this.circleMode = new CircleMode(this);
        this.addLocationMode = new AddLocationMode(this);

        this.setColorBlind(this.activity.getSharedPreferences()
                .getBoolean(getResources().getString(R.string.color_blind_switch_key), false));
    }

    /**
     * Returns the current map mode (see {@link MapMode})
     * @return the current map mode
     */
    public MapMode getMode() {
        return this.mode;
    }

    /**
     * Sets the map mode for when next opening the map (see {@link MapMode})
     * @param mode the map mde
     */
    public void setMode(MapMode mode) {
        this.mode = mode;
    }

    /**
     * Sets the colour-blind flag
     * @param isColorBlind if colour-blind should be on
     */
    public void setColorBlind(boolean isColorBlind) {
        this.circleMode.setColorBlind(isColorBlind);
        this.setMapLegendColors(isColorBlind);
    }

    /**
     * Refreshes the location markers for Add Location Mode
     */
    public void refreshMarkers() {
        if (this.googleMap != null) {
            this.addLocationMode.refreshMarkers(this.googleMap);
        }
    }

    /**
     * Redraws all circles for Circle Mode
     */
    public void updateCircles() {
        if (this.googleMap != null) {
            this.circleMode.updateCircles(this.googleMap);
        }
    }

    /**
     * Initializes all map components on start-up
     * @param view the underlying view
     * @param savedInstanceState previously saved app state
     */
    private void initializeMap(View view, Bundle savedInstanceState) {
        this.mapView = view.findViewById(R.id.mapView);
        this.mapView.onCreate(savedInstanceState);
        this.mapView.onResume();

        try {
            MapsInitializer.initialize(this.activity.getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        this.mapView.getMapAsync(this);
    }

    /**
     * Initializes all legend view elements
     */
    private void initializeLegend() {
        this.legendBackground = this.activity.findViewById(R.id.legendBackground);

        this.legendBoxes = new LinkedList<>();
        this.legendBoxes.add(this.activity.findViewById(R.id.legendBlue));
        this.legendBoxes.add(this.activity.findViewById(R.id.legendGreenLight));
        this.legendBoxes.add(this.activity.findViewById(R.id.legendGreen));
        this.legendBoxes.add(this.activity.findViewById(R.id.legendGreenDark));
        this.legendBoxes.add(this.activity.findViewById(R.id.legendOrangeLight));
        this.legendBoxes.add(this.activity.findViewById(R.id.legendOrange));
        this.legendBoxes.add(this.activity.findViewById(R.id.legendOrangeDark));
        this.legendBoxes.add(this.activity.findViewById(R.id.legendRedLight));
        this.legendBoxes.add(this.activity.findViewById(R.id.legendRed));
        this.legendBoxes.add(this.activity.findViewById(R.id.legendRedDark));
        this.legendBoxes.add(this.activity.findViewById(R.id.legendPurple));

        this.legendTexts = new LinkedList<>();
        this.legendTexts.add(this.activity.findViewById(R.id.legendTextBlue));
        this.legendTexts.add(this.activity.findViewById(R.id.legendTextGreenLight));
        this.legendTexts.add(this.activity.findViewById(R.id.legendTextGreen));
        this.legendTexts.add(this.activity.findViewById(R.id.legendTextGreenDark));
        this.legendTexts.add(this.activity.findViewById(R.id.legendTextOrangeLight));
        this.legendTexts.add(this.activity.findViewById(R.id.legendTextOrange));
        this.legendTexts.add(this.activity.findViewById(R.id.legendTextOrangeDark));
        this.legendTexts.add(this.activity.findViewById(R.id.legendTextRedLight));
        this.legendTexts.add(this.activity.findViewById(R.id.legendTextRed));
        this.legendTexts.add(this.activity.findViewById(R.id.legendTextRedDark));
        this.legendTexts.add(this.activity.findViewById(R.id.legendTextPurple));
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        MapsInitializer.initialize(this.activity.getApplicationContext());
        this.googleMap = googleMap;
        this.moveCamera();
    }

    /**
     * Moves the camera to Newcastle
     */
    private void moveCamera() {
        Double newcastleLatitude = 
                Double.valueOf(getResources().getString(R.string.default_latitude));
        Double newcastleLongitude =
                Double.valueOf(getResources().getString(R.string.default_longitude));
        LatLng newcastlePosition = new LatLng(newcastleLatitude, newcastleLongitude);
        int mapZoom = this.activity.getResources().getInteger(R.integer.map_zoom);
        CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(newcastlePosition).zoom(mapZoom).build();
        this.googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    /**
     * Executed when a location is added in Add Location Mode
     * @param locationName the location name
     */
    void onLocationAdded(String locationName) {
        LatLng newLatLng = this.addLocationMode.onLocationAdded(locationName);
        this.activity.getUserLocationsManager()
                .addUserLocation(new UserLocation(locationName, newLatLng));

        FragmentManager fragmentManager = this.activity.getSupportFragmentManager();
        fragmentManager.popBackStack();
        fragmentManager.beginTransaction().hide(this).commit();
    }

    /**
     * Executed on pressing Refresh
     */
    void onRefreshPressed() {
        this.activity.loadData();
    }

    /**
     * Executed on pressing the Legend button
     */
    void onLegendPressed()
    {
        this.toggleMapLegend();
    }

    /**
     * Refreshes the legend colours.
     * @param isColorBlind If the colours should be colour-blind.
     */
    void setMapLegendColors(boolean isColorBlind) {
        int[] colors = this.activity.getResources().getIntArray(R.array.colors);
        int[] colorsColorBlind =
                this.activity.getResources().getIntArray(R.array.colors_colorblind);

        if (!isColorBlind) {
            for (View box : this.legendBoxes) {
                box.setBackgroundColor(colors[this.legendBoxes.indexOf(box)]);
            }
        } else {
            for (View box : this.legendBoxes) {
                box.setBackgroundColor(colorsColorBlind[this.legendBoxes.indexOf(box)]);
            }
        }
    }

    /**
     * Toggles the map legend to its opposite state (On when currently Off, and opposite)
     */
    void toggleMapLegend() {
        boolean toTurnOn = this.legendBackground.getVisibility() != View.VISIBLE;
        if (toTurnOn) {
            this.legendBackground.setVisibility(View.VISIBLE);
            for (View box : this.legendBoxes) {
                box.setVisibility(View.VISIBLE);
            }
            for (View text : this.legendTexts) {
                text.setVisibility(View.VISIBLE);
            }
        } else {
            this.legendBackground.setVisibility(View.GONE);
            for (View box : this.legendBoxes) {
                box.setVisibility(View.GONE);
            }
            for (View text : this.legendTexts) {
                text.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if (!hidden) {
            if (this.mode.equals(MapMode.CIRCLE_MODE)) {
                this.circleMode.toggleItems(true);

                if (this.googleMap != null) {
                    this.updateCircles();
                }
            } else if (this.mode.equals(MapMode.ADD_LOCATION_MODE)) {
                this.activity.getViewManager().toggleNavBar();
                this.addLocationMode.toggleItems(true);
                this.addLocationMode.addMapListeners(this.googleMap);

                if (this.googleMap != null) {
                    this.refreshMarkers();
                }
            }
        } else {
            if (this.mode.equals(MapMode.CIRCLE_MODE)) {
                this.circleMode.toggleItems(false);
            } else if (this.mode.equals(MapMode.ADD_LOCATION_MODE)) {
                this.activity.getViewManager().toggleNavBar();
                this.addLocationMode.toggleItems(false);
                this.addLocationMode.removeMapListeners(this.googleMap);
            }
        }
        super.onHiddenChanged(hidden);
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        mapView.onPause();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        mapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
}