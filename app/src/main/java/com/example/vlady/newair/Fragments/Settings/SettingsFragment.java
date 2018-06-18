package com.example.vlady.newair.Fragments.Settings;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v14.preference.SwitchPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.view.View;

import com.example.vlady.newair.Activity.MainActivity;
import com.example.vlady.newair.R;

import java.util.Locale;

/**
 * Settings screen.
 * @author Vladislav Iliev
 */
public class SettingsFragment extends PreferenceFragmentCompat {
    private MainActivity activity;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.OnSharedPreferenceChangeListener preferencesListener;

    private Preference timerDialogBtn;
    private Preference deleteUserLocationsBtn;
    private Preference deleteUserLocationBtn;
    private Preference aboutDialogBtn;

    private boolean isTimerSwitchEnabled;

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        this.addPreferencesFromResource(R.xml.settings_fragment);
        this.initialise();
        this.addListeners();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.checkTimerSwitchEnabled();
        this.checkTimerIcons();
        this.checkTimerDialogButton();
    }

    @Override
    public void onResume() {
        super.onResume();
        this.sharedPreferences
                .registerOnSharedPreferenceChangeListener(this.preferencesListener);
    }

    @Override
    public void onPause() {
        this.sharedPreferences
                .unregisterOnSharedPreferenceChangeListener(this.preferencesListener);
        super.onPause();
    }

    /**
     * Initializes all components on start-up
     */
    private void initialise() {
        this.activity = (MainActivity) this.getActivity();
        this.sharedPreferences = 
                this.activity != null
                ? this.activity.getSharedPreferences()
                : null;

        this.timerDialogBtn = 
                this.findPreference(getString(R.string.timer_picker_key));
        this.deleteUserLocationsBtn = 
                this.findPreference(getString(R.string.delete_user_locations_button_key));
        this.deleteUserLocationBtn =
                this.findPreference(getString(R.string.delete_user_location_button_key));
        this.aboutDialogBtn = 
                this.findPreference(getString(R.string.about_dialog_key));
    }

    /**
     * Adds button listeners
     */
    private void addListeners() {
        this.preferencesListener = 
                new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
                                                  String key) {
                if (key.equals(getResources().getString(R.string.color_blind_switch_key))) {
                    updateColorBlind();
                } else if (key.equals(getResources().getString(R.string.timer_switch_key))) {
                    updateTimerSwitchEnabled();
                    checkTimerIcons();
                    checkTimerDialogButton();
                }
            }
        };
        this.sharedPreferences
                .registerOnSharedPreferenceChangeListener(preferencesListener);
        this.timerDialogBtn
                .setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                new TimerPickerDialog(SettingsFragment.this);
                return true;
            }
        });

        this.deleteUserLocationsBtn.setOnPreferenceClickListener(
                new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                activity.getUserLocationsManager().removeAllUserLocations();
                onLocationDeleted();
                return true;
            }
        });

        this.deleteUserLocationBtn.setOnPreferenceClickListener(
                new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                new RemoveLocationDialog(SettingsFragment.this);
                return true;
            }
        });

        this.aboutDialogBtn.setOnPreferenceClickListener(
                new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                new AboutDialog(SettingsFragment.this);
                return true;
            }
        });
    }

    /**
     * Triggered on toggling colour blind switch
     */
    private void updateColorBlind() {
        boolean isColorBlind = 
                ((SwitchPreference) findPreference(
                        getString(R.string.color_blind_switch_key))).isChecked();

        this.activity.getViewManager().getHomeFragment().setColorBlind(isColorBlind);
        this.activity.getViewManager().getHomeFragment().updateBackgroundPalette();

        this.activity.getViewManager().getMapFragment().setColorBlind(isColorBlind);
        this.activity.getViewManager().getMapFragment().updateCircles();
    }

    /**
     * Checks if the auto-download timer switch is on
     */
    private void checkTimerSwitchEnabled() {
        this.isTimerSwitchEnabled =
                sharedPreferences.getBoolean(getString(R.string.timer_switch_key),true);
    }

    /**
     * Updates the timer icons according to the timer switch
     */
    private void checkTimerIcons() {
        if (this.isTimerSwitchEnabled) {
            this.timerDialogBtn.setIcon(getResources().getDrawable(R.drawable.ic_timer));
        } else {
            this.timerDialogBtn.setIcon(getResources().getDrawable(R.drawable.ic_timer_off));
        }
    }

    /**
     * Triggered when the timer switch is toggled
     */
    private void updateTimerSwitchEnabled() {
        this.isTimerSwitchEnabled =
                sharedPreferences.getBoolean(getString(R.string.timer_switch_key),true);
        this.activity.onTimerEnabledUpdated(this.isTimerSwitchEnabled);
    }

    /**
     * Check if the Delete Location buttons should be turned On or Off
     */
    private void checkDeleteButtons() {
        if (this.activity.getUserLocationsManager().getUserLocations().isEmpty()) {
            this.deleteUserLocationsBtn.setEnabled(false);
            this.deleteUserLocationBtn.setEnabled(false);
        } else {
            this.deleteUserLocationsBtn.setEnabled(true);
            this.deleteUserLocationBtn.setEnabled(true);
        }
    }

    /**
     * Checks whether the timer interval picker dialog should be turned On/Off, with
     * an updated description.
     */
    private void checkTimerDialogButton() {
        if (this.isTimerSwitchEnabled) {
            this.timerDialogBtn.setEnabled(true);
            int savedMinutes =
                    this.sharedPreferences
                            .getInt(getString(R.string.timer_minutes_shared_key),
                                    this.activity.getResources()
                                            .getInteger(R.integer.minutes_default));
            int savedSeconds =
                    this.sharedPreferences
                            .getInt(getString(R.string.timer_seconds_shared_key),
                                    this.activity.getResources()
                                            .getInteger(R.integer.seconds_default));
            this.timerDialogBtn
                    .setSummary(String.format(
                            Locale.UK,
                            getString(R.string.timer_picker_summary_format),
                            savedMinutes,
                            savedSeconds));
        } else {
            this.timerDialogBtn.setEnabled(false);
            this.timerDialogBtn.setSummary(getString(R.string.timer_picker_summary_disabled));
        }
    }

    /**
     * Toggled on deleting a user location
     */
    void onLocationDeleted() {
        this.checkDeleteButtons();
    }

    /**
     * Toggled on choosing a new timer interval
     * @param newMinutes the new minutes
     * @param newSeconds the new seconds
     */
    void onTimerUpdated(int newMinutes, int newSeconds) {
        this.sharedPreferences.edit()
                .putInt(
                        activity.getString(R.string.timer_minutes_shared_key),
                        newMinutes)
                .apply();
        this.sharedPreferences.edit()
                .putInt(
                        activity.getString(R.string.timer_seconds_shared_key),
                        newSeconds)
                .apply();
        this.checkTimerDialogButton();
        this.activity.onTimerIntervalUpdated();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if (!hidden) {
            this.checkDeleteButtons();
        }
        this.activity.getViewManager().toggleNavBar();
        super.onHiddenChanged(hidden);
    }
}