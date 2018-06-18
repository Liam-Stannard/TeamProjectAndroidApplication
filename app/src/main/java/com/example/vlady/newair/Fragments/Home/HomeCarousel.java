package com.example.vlady.newair.Fragments.Home;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.vlady.newair.Data.UserLocations.UserLocation;
import com.example.vlady.newair.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import in.goodiebag.carouselpicker.CarouselPicker;

/**
 * Sliding carousel on Home screen. Includes all location-specific elements - health and
 * distance messages
 * @author Vladislav Iliev
 */
class HomeCarousel {
    private HomeFragment homeFragment;
    private View homeFragmentView;

    private CarouselPicker carousel;
    private ImageButton carouselArrowLeft;
    private ImageButton carouselArrowRight;
    private TextView currentLevel;
    private TextView healthMessage;
    private TextView distanceMessage;

    private List<String> locations;
    private List<CarouselPicker.PickerItem> carouselItems;

    private int currentPosition;

    HomeCarousel(HomeFragment homeFragment, View homeFragmentView) {
        this.initialise(homeFragment, homeFragmentView);
        this.addListeners();

        this.addInitialLocations();
    }

    /**
     * Returns the carousel
     * @return the carousel
     */
    CarouselPicker getCarousel() {
        return this.carousel;
    }

    /**
     * Returns the currently displayed carousel position
     * @return the currently displayed carousel position
     */
    int getCurrentPosition() {
        return this.currentPosition;
    }

    /**
     * Returns the carousel items
     * @return the carousel items
     */
    List<String> getLocationsList() {
        return Collections.unmodifiableList(this.locations);
    }

    /**
     * Returns all default health messages
     * @return all default health messages
     */
    String[] getHealthMessagesArray() {
        return this.homeFragmentView.getResources().getStringArray(
                R.array.health_messages);
    }

    /**
     * Sets the current visible pollution
     * @param pollutionNumber The pollution measurement
     */
    void setPollution(double pollutionNumber) {
        String textToSet;
        if (pollutionNumber < 0) {
            textToSet = "?";
        } else {
            textToSet = String.valueOf(pollutionNumber);
        }
        this.currentLevel.setText(textToSet);
    }

    /**
     * Sets the currently visible health message
     * @param healthMessage the new health message
     */
    void setHealthMessage(String healthMessage) {
        this.healthMessage.setText(healthMessage);
    }

    /**
     * Some locations don't show nearest sensor distance (e.g. City)
     * @return Whether the closest sensor distance is visible
     */
    boolean isClosestSensorMessageEnabled() {
        return this.distanceMessage.getVisibility() == View.VISIBLE;
    }

    /**
     * Enables the distance message at bottom of screen
     */
    void enableDistanceMessage() {
        this.distanceMessage.setVisibility(View.VISIBLE);
    }

    /**
     * Disables the distance message at bottom of screen
     */
    void disableDistanceMessage() {
        this.distanceMessage.setVisibility(View.INVISIBLE);
    }

    /**
     * Updates the currently displayed distance message
     * @param distance the distancein metres
     */
    void setDistanceMessage(int distance) {
        String textToSet = "?";

        if (distance >= 0) {
            textToSet = String.valueOf(distance);
        }

        this.distanceMessage.setText(
                String.format(
                        this.homeFragmentView.getResources().getString(
                                R.string.closest_sensor_distance),
                        textToSet));
    }

    /**
     * Initializes all components on start-up
     * @param homeFragment the Home screen
     * @param homeFragmentView the Home view
     */
    private void initialise(HomeFragment homeFragment, View homeFragmentView) {
        this.homeFragment = homeFragment;
        this.homeFragmentView = homeFragmentView;

        this.carousel = this.homeFragmentView.findViewById(R.id.carousel);
        this.carouselArrowLeft =
                this.homeFragmentView.findViewById(R.id.carouselArrowLeft);
        this.carouselArrowRight = 
                this.homeFragmentView.findViewById(R.id.carouselArrowRight);
        this.currentLevel = this.homeFragmentView.findViewById(R.id.carouselMessage);
        this.healthMessage = this.homeFragmentView.findViewById(R.id.healthMessage);
        this.distanceMessage = this.homeFragmentView.findViewById(R.id.distance_text);

        this.locations = new ArrayList<>();
        this.carouselItems = new LinkedList<>();
    }

    /**
     * Adds carousel and arrow listeners
     */
    private void addListeners() {
        this.addCarouselListener();
        this.addArrowListeners();
    }

    /**
     * Adds the carousel listeners
     */
    private void addCarouselListener() {
        this.carousel.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                    @Override
                    public void onPageScrolled(int position,
                                               float positionOffset,
                                               int positionOffsetPixels) {
                    }

                    @Override
                    public void onPageSelected(int position) {
                        currentPosition = position;
                        homeFragment.updateScreen();
                    }

                    @Override
                    public void onPageScrollStateChanged(int state) {
                    }
                });
    }

    /**
     * Adds the carousel arrow listeners
     */
    private void addArrowListeners() {
        this.carouselArrowLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                carousel.setCurrentItem(carousel.getCurrentItem() - 1, true);
            }
        });

        this.carouselArrowRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                carousel.setCurrentItem(carousel.getCurrentItem() + 1, true);
            }
        });
    }

    /**
     * Adds the default locations (Home and Nearby) to the carousel
     */
    private void addInitialLocations() {
        this.locations.addAll(
                Arrays.asList(this.homeFragmentView
                        .getContext().getResources().getStringArray(
                                R.array.initial_locations)));
        this.updateVisibleLocations();
    }

    /**
     * Adds a User Location to the carousel
     * @param userLocation the new User Location
     */
    void addLocation(UserLocation userLocation) {
        this.locations.add(userLocation.getName());
        this.updateVisibleLocations();
    }

    /**
     * Adds a list of User Locations to the carousel
     * @param userLocations the list of User Locations
     */
    void addLocations(List<UserLocation> userLocations) {
        for (UserLocation userLocation : userLocations) {
            this.locations.add(userLocation.getName());
        }

        this.updateVisibleLocations();
    }

    /**
     * Removes a User Location from the carousel
     * @param userLocation the User Location to remove
     */
    void removeLocation(UserLocation userLocation) {
        this.carousel.setCurrentItem(0);
        this.locations.remove(this.locations.indexOf(userLocation.getName()));
        this.updateVisibleLocations();
    }

    /**
     * Removes all User Locations from the carousel
     */
    void removeAllLocations() {
        this.carousel.setCurrentItem(0);
        this.locations.clear();
        this.addInitialLocations();
    }

    /**
     * Refreshes all currently displayed User Locations
     */
    private void updateVisibleLocations() {
        this.carouselItems.clear();
        for (String location : this.locations) {
            this.carouselItems.add(
                    new CarouselPicker.TextItem(
                            location, 
                            this.homeFragmentView
                                    .getContext().getResources().getInteger(
                                            R.integer.carousel_number_size)));
        }

        this.carousel.setAdapter(
                new CarouselAdapter(
                        this.homeFragment.getActivity(),
                        this.carouselItems,
                        0));
        this.carousel.clearOnPageChangeListeners();
        this.addCarouselListener();
    }

    /**
     * If the end of the carousel is reached, hide the corresponding arrow
     */
    void checkArrowsVisibility() {
        if (this.currentPosition == 0) {
            this.carouselArrowLeft.setVisibility(View.INVISIBLE);
        } else if (this.carouselArrowLeft.getVisibility() == View.INVISIBLE) {
            this.carouselArrowLeft.setVisibility(View.VISIBLE);
        }

        if (this.currentPosition == this.locations.size() - 1) {
            this.carouselArrowRight.setVisibility(View.INVISIBLE);
        } else if (this.carouselArrowRight.getVisibility() == View.INVISIBLE) {
            this.carouselArrowRight.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Created by pavan on 25/04/17.
     * Edited by Vladislav Iliev
     * MIT License

     Copyright (c) 2017 GoodieBag

     Permission is hereby granted, free of charge, to any person obtaining a copy
     of this software and associated documentation files (the "Software"), to deal
     in the Software without restriction, including without limitation the rights
     to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
     copies of the Software, and to permit persons to whom the Software is
     furnished to do so, subject to the following conditions:

     The above copyright notice and this permission notice shall be included in all
     copies or substantial portions of the Software.

     THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
     IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
     FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
     AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
     LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
     OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
     SOFTWARE.
     */
    private class CarouselAdapter extends PagerAdapter {
        private final Context context;
        private List<CarouselPicker.PickerItem> items;
        private int drawable;

        CarouselAdapter(Context context, 
                        List<CarouselPicker.PickerItem> items,
                        int drawable) {
            this.context = context;
            this.drawable = drawable;
            this.items = items;
            if (this.drawable == 0) {
                this.drawable = in.goodiebag.carouselpicker.R.layout.page;
            }
        }

        public int getCount() {
            return this.items.size();
        }

        @NonNull
        public Object instantiateItem(@NonNull ViewGroup container,
                                      int position) {
            View view =
                    LayoutInflater.from(this.context)
                            .inflate(this.drawable, null);
            TextView tv =
                    view.findViewById(in.goodiebag.carouselpicker.R.id.tv);
            tv.setTextColor(
                    HomeCarousel.this.homeFragmentView.getResources().getColor(
                            R.color.homeTextColor));
            CarouselPicker.PickerItem pickerItem = this.items.get(position);
            tv.setVisibility(View.VISIBLE);
            tv.setText(pickerItem.getText());
            int textSize = ((CarouselPicker.TextItem) pickerItem).getTextSize();
            if (textSize != 0) {
                tv.setTextSize(
                        (float) this.dpToPx(
                                ((CarouselPicker.TextItem) pickerItem)
                                        .getTextSize()));
            }

            view.setTag(position);
            container.addView(view);
            return view;
        }

        public void destroyItem(@NonNull ViewGroup container, int position,
                                @NonNull Object object) {
            container.removeView((View) object);

        }

        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }

        private int dpToPx(int dp) {
            DisplayMetrics displayMetrics =
                    this.context.getResources().getDisplayMetrics();
            return Math.round((float) dp * (displayMetrics.xdpi / 160.0F));
        }
    }
}