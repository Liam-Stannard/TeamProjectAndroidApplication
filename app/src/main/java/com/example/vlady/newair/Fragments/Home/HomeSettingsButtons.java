package com.example.vlady.newair.Fragments.Home;

import android.view.View;
import android.widget.ImageButton;

import com.example.vlady.newair.R;

/**
 * Buttons at top of Home
 * @author Vladislav Iliev
 */
class HomeSettingsButtons {
    private final HomeFragment homeFragment;

    private ImageButton settingsButton;
    private ImageButton refreshButton;
    private ImageButton addButton;

    HomeSettingsButtons(HomeFragment homeFragment) {
        this.homeFragment = homeFragment;

        this.initialise(homeFragment.getView());
        this.addListeners();
    }

    /**
     * Initializes all components on start-up
     * @param view the Home view
     */
    private void initialise(View view) {
        this.settingsButton = view.findViewById(R.id.settingsButton);
        this.refreshButton = view.findViewById(R.id.refreshButton);
        this.addButton = view.findViewById(R.id.addButton);
    }

    /**
     * Adds button listeners
     */
    private void addListeners() {
        this.addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                homeFragment.onAddPressed();
            }
        });

        this.refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                homeFragment.onRefreshPressed();
            }
        });

        this.settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                homeFragment.onSettingsPressed();
            }
        });
    }
}
