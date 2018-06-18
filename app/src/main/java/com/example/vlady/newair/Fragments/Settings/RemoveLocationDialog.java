package com.example.vlady.newair.Fragments.Settings;

import android.app.AlertDialog;
import android.content.DialogInterface;

import com.example.vlady.newair.Activity.MainActivity;
import com.example.vlady.newair.Data.UserLocations.UserLocation;
import com.example.vlady.newair.R;

import java.util.List;

/**
 * Dialog for removing a custom User Location
 * @author Vladislav Iliev
 */
class RemoveLocationDialog {
    private final MainActivity activity;
    private final SettingsFragment settingsFragment;

    RemoveLocationDialog(SettingsFragment settingsFragment) {
        this.activity = (MainActivity) settingsFragment.getActivity();
        this.settingsFragment = settingsFragment;
        AlertDialog.Builder dialogBuilder = this.build();
        this.addListeners(dialogBuilder, this.setUpDeletionList());
        dialogBuilder.show();
    }

    /**
     * Builds the dialog elements
     * @return the built dialog
     */
    private AlertDialog.Builder build() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this.activity);
        builder.setTitle(
                this.activity.getResources().getString(
                        R.string.delete_user_location_button_title));
        return builder;
    }

    /**
     * Adds all locations that can removed
     * @return the locations array
     */
    private String[] setUpDeletionList() {
        List<UserLocation> locations =
                this.activity.getUserLocationsManager().getUserLocations();
        String[] items = new String[locations.size()];

        for (int i = 0; i < locations.size(); i++) {
            items[i] = locations.get(i).getName();
        }

        return items;
    }

    /**
     * Adds button listeners
     * @param builder the dialog
     * @param items the lcations array
     */
    private void addListeners(AlertDialog.Builder builder, String[] items) {
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int position) {
                activity.getUserLocationsManager()
                        .removeUserLocation(
                                activity.getUserLocationsManager()
                                        .getUserLocations().get(position));
                settingsFragment.onLocationDeleted();
            }
        });

        builder.setNegativeButton(android.R.string.cancel, 
                                  new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
    }
}