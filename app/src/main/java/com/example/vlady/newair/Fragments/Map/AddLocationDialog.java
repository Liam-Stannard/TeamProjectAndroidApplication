package com.example.vlady.newair.Fragments.Map;

import android.content.DialogInterface;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.vlady.newair.Activity.MainActivity;
import com.example.vlady.newair.R;

/**
 * Dialog when adding a new custom User Location
 * @author Vladislav Iliev
 */
class AddLocationDialog {
    private final MainActivity activity;
    private final MapFragment mapFragment;

    AddLocationDialog(MapFragment mapFragment) {
        this.activity = (MainActivity) mapFragment.getActivity();
        this.mapFragment = mapFragment;
        View view =
                LayoutInflater.from(this.activity)
                        .inflate(R.layout.dialog_add_custom_location,
                                (ViewGroup) mapFragment.getView(),
                                false);
        AlertDialog dialog = this.setUp(view);
        dialog.show();
        this.addListeners(dialog, view);
    }

    /**
     * Builds the dialog elements
     * @param view the underlying view
     * @return the built dialog window
     */
    private AlertDialog setUp(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this.activity);
        builder.setView(view);

        builder.setTitle(this.activity.getString(R.string.add_custom_location_dialog_title));
        builder.setPositiveButton(android.R.string.ok, null);
        builder.setNegativeButton(android.R.string.cancel, null);

        return builder.create();
    }

    /**
     * Adds button listeners
     * @param dialog the dialog
     * @param view the underlying view
     */
    private void addListeners(final AlertDialog dialog, final View view) {
        dialog.getButton(
                DialogInterface.BUTTON_POSITIVE).setOnClickListener(
                        new View.OnClickListener() {
            @Override
            public void onClick(View onClick) {
                String inputText = ((TextInputEditText) view.findViewById(R.id.input))
                                .getText().toString();
                if (inputText.isEmpty()) {
                    Toast.makeText(
                            activity,
                            activity.getString(R.string.location_empty),
                            Toast.LENGTH_LONG)
                            .show();
                } else if (inputText.length() >= activity.getResources()
                        .getInteger(R.integer.name_max_length)) {
                    Toast.makeText(
                            activity,
                            activity.getString(R.string.name_too_long),
                            Toast.LENGTH_LONG)
                            .show();
                } else if (activity.getUserLocationsManager().checkLocationExists(inputText)) {
                    Toast.makeText(
                            activity,
                            String.format(
                                    activity.getString(R.string.location_already_exists_format),
                                    activity.getString(R.string.location_already_exists_toast),
                                    inputText),
                            Toast.LENGTH_LONG)
                        .show();
                } else {
                    mapFragment.onLocationAdded(inputText);
                    dialog.dismiss();
                }
            }
        });

        dialog.getButton(
                DialogInterface.BUTTON_NEGATIVE).setOnClickListener(
                        new View.OnClickListener() {
            @Override
            public void onClick(View onClick) {
                dialog.cancel();
            }
        });
    }
}