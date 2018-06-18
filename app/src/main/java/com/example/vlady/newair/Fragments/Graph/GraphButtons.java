package com.example.vlady.newair.Fragments.Graph;

import android.view.View;
import android.widget.ImageButton;

import com.example.vlady.newair.R;

/**
 * A collection of all buttons on the Graph screen
 * @author Vladislav Iliev
 */
class GraphButtons {
    private final GraphFragment graphFragment;
    private ImageButton refreshButton;

    GraphButtons(GraphFragment graphFragment) {
        this.graphFragment = graphFragment;
        this.initialize(graphFragment.getView());
        this.addListeners();
    }

    /**
     * Initializes the buttons on start-up
     * @param view the app View
     */
    private void initialize(View view) {
        this.refreshButton = view.findViewById(R.id.refreshButton);
    }

    /**
     * Adds the listeners to the buttons
     */
    private void addListeners() {
        this.refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                graphFragment.onRefreshPressed();
            }
        });
    }
}