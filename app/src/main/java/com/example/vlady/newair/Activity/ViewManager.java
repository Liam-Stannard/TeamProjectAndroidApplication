package com.example.vlady.newair.Activity;

import android.graphics.drawable.AnimationDrawable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.ImageView;

import com.example.vlady.newair.Fragments.Graph.GraphFragment;
import com.example.vlady.newair.Fragments.Home.HomeFragment;
import com.example.vlady.newair.Fragments.Map.MapFragment;
import com.example.vlady.newair.Fragments.Settings.SettingsFragment;
import com.example.vlady.newair.R;

import java.util.Stack;

/**
 * Manager of visible screens.
 * @author Vladislav Iliev, Ira Watt
 */
public class ViewManager {
    private final MainActivity activity;

    private HomeFragment homeFragment;
    private MapFragment mapFragment;
    private GraphFragment graphFragment;
    private SettingsFragment settingsFragment;
    /**
     * Keep track of last loaded fragment
     */
    private Stack<Fragment> fragmentStack;

    private Navbar navbar;

    /**
     * Items to manage the loading bar (loading cloud)
     */
    private AnimationDrawable animationDrawable;
    private ImageView imageView;

    ViewManager(MainActivity mainActivity) {
        this.activity = mainActivity;
        this.initialize();
    }

    /**
     * Returns the Map (not necessarily screen - it has two modes)
     * @return the Map
     */
    public MapFragment getMapFragment() {
        return this.mapFragment;
    }

    /**
     * Returns the Home screen
     * @return the Home screen
     */
    public HomeFragment getHomeFragment() {
        return this.homeFragment;
    }

    /**
     * Returns the Graph screen
     * @return the Graph screen
     */
    public GraphFragment getGraphFragment() {
        return this.graphFragment;
    }

    /**
     * Returns the Settings screen
     * @return the Settings screen
     */
    public SettingsFragment getSettingsFragment() {
        return this.settingsFragment;
    }

    /**
     * Sets up the manager upon start-up
     */
    private void initialize() {
        this.homeFragment = new HomeFragment();
        this.mapFragment = new MapFragment();
        this.graphFragment = new GraphFragment();
        this.settingsFragment = new SettingsFragment();

        this.fragmentStack = new Stack<Fragment>();
        this.navbar = new Navbar(this.activity);

        this.imageView = this.activity.findViewById(R.id.progress_bar);
        this.imageView.setBackgroundResource(R.drawable.ic_progress_bar_sequence);
        this.animationDrawable = (AnimationDrawable) imageView.getBackground();
    }

    /**
     * Stores all fragments in memory upon initial load
     * so they can be shown and hidden immediately.
     * Eliminates the need to constantly redraw fragments.
     */
    void cacheFragments() {
        FragmentTransaction transaction =
                this.activity.getSupportFragmentManager().beginTransaction();

        transaction.add(R.id.fragment_container, this.homeFragment);
        transaction.hide(this.homeFragment);

        transaction.add(R.id.fragment_container, this.mapFragment);
        transaction.hide(this.mapFragment);

        transaction.add(R.id.fragment_container, this.graphFragment);
        transaction.hide(this.graphFragment);

        transaction.add(R.id.fragment_container, this.settingsFragment);
        transaction.hide(this.settingsFragment);

        transaction.commit();
    }

    /**
     * Show the Home screen after program first data load
     */
    void showInitialFragment() {
        // Need to have something on the stack before showing a fragment
        // (see showFragment())
        this.fragmentStack.push(this.homeFragment);
        this.showFragment(this.homeFragment);
    }

    /**
     * Sets the currently visible screen and manages the active/dusabled
     * state of navbar items. Need to have something on
     * {@link ViewManager#fragmentStack} to know which Fragment to hide.
     * @param newFragment the instance of the new Fragment
     */
    public void showFragment(Fragment newFragment) {
        FragmentTransaction transaction =
                this.activity.getSupportFragmentManager().beginTransaction();
        // The last loaded Fragment was added on a stack (see method ending)
        Fragment lastLoadedFragment = this.fragmentStack.pop();

        // Enable the previously disabled navbar position, and disable the new one
        // If Map screen is selected, continue only if accessed through navbar
        // (If Map is accessed through Home (Add new user location), no need to fiddle
        // with navbar)
        if (newFragment instanceof HomeFragment
                || (newFragment instanceof MapFragment
                    && this.mapFragment.getMode().equals(
                            MapFragment.MapMode.CIRCLE_MODE)
                || newFragment instanceof GraphFragment)) {
            int navBarPositionToEnable = -1;
            if (lastLoadedFragment instanceof HomeFragment) {
                navBarPositionToEnable =
                        this.activity.getResources().getInteger(
                                R.integer.navbar_home_position);
            } else if (lastLoadedFragment instanceof MapFragment) {
                navBarPositionToEnable =
                        this.activity.getResources().getInteger(
                                R.integer.navbar_map_position);
            } else if (lastLoadedFragment instanceof GraphFragment) {
                navBarPositionToEnable =
                        this.activity.getResources().getInteger(
                                R.integer.navbar_graph_position);
            }
            this.navbar.enablePosition(navBarPositionToEnable);

            int navBarPositionToDisable;
            if (newFragment instanceof HomeFragment) {
                navBarPositionToDisable =
                        this.activity.getResources().getInteger(
                                R.integer.navbar_home_position);
            } else if (newFragment instanceof MapFragment) {
                navBarPositionToDisable =
                        this.activity.getResources().getInteger(
                                R.integer.navbar_map_position);
            } else {
                navBarPositionToDisable = 
                        this.activity.getResources().getInteger(
                                R.integer.navbar_graph_position);
            }
            this.navbar.disablePosition(navBarPositionToDisable);
        }

        transaction.hide(lastLoadedFragment);

        Fragment fragmentToPushToStack = newFragment;
        // If a fragment is accessed from Home (not through navbar), make home the
        // last loaded fragment
        if (newFragment instanceof SettingsFragment
                || (newFragment instanceof MapFragment
                    && this.mapFragment.getMode().equals(
                            MapFragment.MapMode.ADD_LOCATION_MODE))) {
            // If a fragment is accessed from the Home screen,
            // make the phone's Back button return to Home
            // (the navbar will be hidden)
            transaction.addToBackStack(null);
            fragmentToPushToStack = this.homeFragment;
        }
        this.fragmentStack.push(fragmentToPushToStack);

        transaction.show(newFragment).commit();
    }

    /**
     * Switches the current navbar visibility (if currently visible, to invisible, and
     * vice versa)
     */
    public void toggleNavBar() {
        this.navbar.toggle();
    }

    /**
     * Shows loading icons (loading cloud)
     */
    public void showProgressBar() {
        this.imageView.setVisibility(View.VISIBLE);
        this.animationDrawable.start();
    }

    /**
     * Hides loading icons (loading cloud)
     */
    void hideProgressBar() {
        this.imageView.setVisibility(View.GONE);
        this.animationDrawable.stop();
    }

    /**
     * Hides loading screen on startup (blue background) and enable navbar
     */
    void hideWelcomeAssets() {
        this.navbar.enable();
        this.activity.findViewById(R.id.welcome_background).setVisibility(View.GONE);
    }
}