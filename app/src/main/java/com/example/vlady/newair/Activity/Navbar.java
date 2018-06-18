package com.example.vlady.newair.Activity;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.example.vlady.newair.Fragments.Map.MapFragment;
import com.example.vlady.newair.R;

/**
 * Bottom navigation bar
 * @author Vladislav Iliev
 */
class Navbar {
    private final MainActivity activity;

    private BottomNavigationView navbar;
    /**
     * Screen layout above the navbar.
     * Used to save the navbar height in case it needs
     * hiding and restoring later.
     */
    private FrameLayout fragmentLayout;

    /**
     * Screen layout of the whole phone.
     */
    private RelativeLayout.LayoutParams fragmentLayoutParams;
    private int navbarHeight;

    public Navbar(MainActivity activity) {
        this.activity = activity;
        this.initialize();
        this.addListeners();
    }

    /**
     * Sets up the components upon creation
     */
    private void initialize() {
        this.navbar = this.activity.findViewById(R.id.navbar);
        this.navbar.setSelectedItemId(R.id.navigation_home);

        this.fragmentLayout = this.activity.findViewById(R.id.fragment_container);
        this.fragmentLayoutParams =
                (RelativeLayout.LayoutParams) this.fragmentLayout.getLayoutParams();
        this.navbarHeight = this.fragmentLayoutParams.bottomMargin;
    }

    /**
     * Adds the navbar button listeners
     */
    private void addListeners() {
        navbar.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        Fragment fragmentToLoad = null;
                        switch (item.getItemId()) {
                            case R.id.navigation_map:
                                //Assume the user wants Circle mode 
                                // when loading from navbar
                                activity.getViewManager().getMapFragment()
                                        .setMode(MapFragment.MapMode.CIRCLE_MODE);
                                fragmentToLoad = activity.getViewManager().getMapFragment();
                                break;

                            case R.id.navigation_home:
                                fragmentToLoad = activity.getViewManager().getHomeFragment();
                                break;

                            case R.id.navigation_graph:
                                fragmentToLoad = activity.getViewManager().getGraphFragment();
                                break;

                            default:
                                break;
                        }
                        activity.getViewManager().showFragment(fragmentToLoad);

                        return true;
                    }
                });
    }

    /**
     * Shows the navbar.
     */
    void enable() {
        this.navbar.setVisibility(View.VISIBLE);
        // Shrink the area above the navbar to make space for the navbar below it.
        this.fragmentLayoutParams.setMargins(0, 0, 0, this.navbarHeight);
        this.fragmentLayout.setLayoutParams(this.fragmentLayoutParams);
    }

    /**
     * Hides the navbar.
     */
    private void disable() {
        this.navbar.setVisibility(View.GONE);
        // Expand the area above the navbar, filling the whole screen.
        this.fragmentLayoutParams.setMargins(0, 0, 0, 0);
        this.fragmentLayout.setLayoutParams(this.fragmentLayoutParams);
    }

    /**
     * Switches the current navbar visibility to its opposite.
     */
    void toggle() {
        if (this.navbar.getVisibility() == View.VISIBLE) {
            this.disable();
        } else {
            this.enable();
        }
    }

    /**
     * Enable a navbar item by position
     * @param position the position index
     */
    void enablePosition(int position) {
        MenuItem item = this.navbar.getMenu().getItem(position);
        item.setEnabled(true);
        item.setChecked(true);
    }
    
    /**
     * Disable a navbar item by position
     * @param position the position index
     */
    void disablePosition(int position) {
        MenuItem item = this.navbar.getMenu().getItem(position);
        item.setEnabled(false);
        item.setChecked(false);
    }
}