package com.example.vlady.newair.Fragments.Settings;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.NumberPicker;

import com.example.vlady.newair.Activity.MainActivity;
import com.example.vlady.newair.R;

/**
 * Dialog for selecting a new auto-update Timer interval.
 * @author Vladislav Iliev
 */
class TimerPickerDialog {
    private final MainActivity activity;
    private final SettingsFragment settingsFragment;
    private final SharedPreferences sharedPreferences;

    private NumberPicker pickerMinutes;
    private NumberPicker pickerSeconds;

    TimerPickerDialog(final SettingsFragment settingsFragment) {
        this.activity = (MainActivity) settingsFragment.getActivity();
        this.settingsFragment = settingsFragment;
        this.sharedPreferences =
                this.activity != null
                ? this.activity.getSharedPreferences()
                : null;

        View view = View.inflate(this.activity, R.layout.timer, null);
        AlertDialog dialog = this.build(view);
        dialog.show();
        this.addListeners(dialog);
    }

    /**
     * Builds the dialog elements
     * @param view the underlying view
     * @return the built dialog
     */
    private AlertDialog build(View view) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this.activity);
        dialog.setTitle(this.activity.getString(R.string.timer_picker_title));
        dialog.setView(view);

        this.pickerMinutes = view.findViewById(R.id.numberPickerMinutes);
        int minMinutes = 
                this.activity.getResources().getInteger(R.integer.minutes_min);
        int maxMinutes = 
                this.activity.getResources().getInteger(R.integer.minutes_max);
        int defaultMinutes = 
                this.activity.getResources().getInteger(R.integer.minutes_default);
        int savedMinutes = 
                this.sharedPreferences.getInt(
                        this.activity.getString(R.string.timer_minutes_shared_key),
                        defaultMinutes);
        pickerMinutes.setMinValue(minMinutes);
        pickerMinutes.setMaxValue(maxMinutes);
        pickerMinutes.setValue(savedMinutes);

        this.pickerSeconds = view.findViewById(R.id.numberPickerSeconds);
        int minSeconds = 
                this.activity.getResources().getInteger(R.integer.seconds_min);
        int maxSeconds = 
                this.activity.getResources().getInteger(R.integer.seconds_max);
        int defaultSeconds = 
                this.activity.getResources().getInteger(R.integer.seconds_default);
        int savedSeconds = 
                this.sharedPreferences.getInt(
                        this.activity.getString(R.string.timer_seconds_shared_key),
                        defaultSeconds);
        pickerSeconds.setMinValue(minSeconds);
        pickerSeconds.setMaxValue(maxSeconds);
        pickerSeconds.setValue(savedSeconds);

        dialog.setPositiveButton(android.R.string.ok, null);
        dialog.setNegativeButton(android.R.string.cancel, null);

        return dialog.create();
    }

    /**
     * Adds button listeners
     * @param dialog the dialog
     */
    private void addListeners(final AlertDialog dialog) {
        dialog.getButton(
                DialogInterface.BUTTON_POSITIVE)
        .setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                settingsFragment.onTimerUpdated(
                        pickerMinutes.getValue(),
                        pickerSeconds.getValue());
                dialog.dismiss();
            }
        });

        dialog.getButton(
                DialogInterface.BUTTON_NEGATIVE)
        .setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });
    }
}