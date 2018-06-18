package com.example.vlady.newair.Fragments.Home;

import android.support.constraint.ConstraintLayout;
import android.view.View;

import com.example.vlady.newair.R;

/**
 * Background of Home screen
 * @author Vladislav Iliev
 */
class HomeBackground {
    private final View homeFragmentView;
    private ConstraintLayout container;

    HomeBackground(View homeFragmentView) {
        this.homeFragmentView = homeFragmentView;
        this.initialise();
    }

    /**
     * Initializes all components on start-up
     */
    private void initialise() {
        this.container = this.homeFragmentView.findViewById(R.id.container);
    }

    /**
     * Returns the starting points for background colors
     * @return the starting points for background colors
     */
    int[] getColorStartValues() {
        return this.homeFragmentView
                .getContext().getResources().getIntArray(R.array.color_dividers_int);
    }

    /**
     * Returns the background colors
     * @return the background colors
     */
    int[] getColors() {
        return this.homeFragmentView
                .getContext().getResources().getIntArray(R.array.colors);
    }

    /**
     * Returns the colorblind background colors
     * @return the colorblind background colors
     */
    int[] getColorsColorblind() {
        return this.homeFragmentView
                .getContext().getResources().getIntArray(R.array.colors_colorblind);
    }

    /**
     * Sets the background color
     * @param color the background color
     */
    void setColor(int color) {
        this.container.setBackgroundColor(color);
    }
}
