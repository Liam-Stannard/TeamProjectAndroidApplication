package com.example.vlady.newair.Activity;

import android.os.CountDownTimer;

import com.example.vlady.newair.R;

/**
 * Manager of auto-update intervals.
 * @author Bradley Wilsher, Vladislav Iliev
 */
class Timer {
    private static final long MILLISECONDS_IN_TICK = 1000;

    private final MainActivity activity;
    private CountDownTimer countDownTimer;

    Timer(MainActivity activity) {
        this.activity = activity;
        this.resetTimer();
    }

    /**
     * Checks whether the timer is currently running
     * @return whether the timer is currently running
     */
    boolean isEnabled() {
        return this.activity.getSharedPreferences()
                .getBoolean(this.activity.getString(R.string.timer_switch_key), true);
    }

    /**
     * Set up the Timer
     * @param duration the interval in seconds
     * @return the newly initialized Timer
     */
    private CountDownTimer initializeTimer(long duration) {
        return new CountDownTimer(duration, MILLISECONDS_IN_TICK) {
            @Override
            public void onTick(long millisUntilFinished) {
                onTicked();
            }

            @Override
            public void onFinish() {
                onFinished();
            }
        };
    }

    /**
     * Starts the timer
     */
    void startTimer() {
        this.countDownTimer.start();
    }

    /**
     * Stops the timer
     */
    void stopTimer() {
        this.countDownTimer.cancel();
    }

    /**
     * Fetch the user-defined interval and re-initialize the timer
     * (without starting it)
     */
    private void resetTimer() {
        long minutes = this.getTimerMinutes(this.activity);
        long seconds = this.getTimerSeconds(this.activity);
        long duration = Timer.calculateTimerTime(minutes, seconds);
        this.countDownTimer = this.initializeTimer(duration);
    }

    /**
     * Executed when On/Off switch in Settings is modified
     * @param on if switch is turned On
     */
    void onTimerEnabledUpdated(boolean on) {
        if (on && !this.activity.isLoading()) {
            this.startTimer();
        } else {
            this.stopTimer();
        }
    }

    /**
     * Executed when timer interval is modified in Settings
     */
    void onTimerIntervalUpdated() {
        if (this.activity.isLoading()) {
            this.resetTimer();
        } else {
            this.stopTimer();
            this.resetTimer();
            this.startTimer();
        }
    }

    /**
     * Sequence executed every second during countdown
     */
    private void onTicked() {
    }

    /**
     * Sequence executed on countdown finished
     */
    private void onFinished() {
        this.activity.loadData();
    }

    /**
     * Fetches the user-defined auto-update minutes
     * @param activity the app (needed to access the shared preferences)
     * @return the minutes
     */
    private long getTimerMinutes(MainActivity activity) {
        return activity.getSharedPreferences().getInt(
                activity.getString(R.string.timer_minutes_shared_key),
                activity.getResources().getInteger(R.integer.minutes_default));
    }

    /**
     * Fetches the user-defined auto-update seconds
     * @param activity the app (needed to access the shared preferences)
     * @return the minutes
     */
    private long getTimerSeconds(MainActivity activity) {
        return activity.getSharedPreferences().getInt(
                activity.getString(R.string.timer_seconds_shared_key),
                activity.getResources().getInteger(R.integer.seconds_default));
    }

    /**
     * Calculates timer time in standard format (milliseconds)
     * @param minutes the minutes
     * @param seconds the seconds
     * @return the timer time
     */
    private static long calculateTimerTime(long minutes, long seconds) {
        return (minutes*60 + seconds)*1000;
    }
}