package com.example.vlady.newair.Fragments.Settings;

import android.app.AlertDialog;
import android.content.DialogInterface;

import com.example.vlady.newair.Activity.MainActivity;
import com.example.vlady.newair.R;

/**
 * Dialog describing the app Authors and other referenced authors
 * @author Vladislav Iliev
 */
class AboutDialog {
    private final MainActivity activity;

    AboutDialog(SettingsFragment settingsFragment) {
        this.activity = (MainActivity) settingsFragment.getActivity();
        AlertDialog.Builder dialogBuilder = this.build();
        this.addListeners(dialogBuilder);
        dialogBuilder.show();
    }

    /**
     * Builds the dialog elements
     * @return the built dialog
     */
    private AlertDialog.Builder build() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this.activity);
        dialogBuilder.setTitle(this.activity.getString(R.string.about_dialog_title));
        dialogBuilder.setMessage(this.activity.getString(R.string.about_dialog_contents));
        return dialogBuilder;
    }

    /**
     * Adds button listeners
     * @param dialogBuilder the dialog
     */
    private void addListeners(AlertDialog.Builder dialogBuilder) {
        dialogBuilder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int i) {
                dialog.dismiss();
            }
        });
    }
}