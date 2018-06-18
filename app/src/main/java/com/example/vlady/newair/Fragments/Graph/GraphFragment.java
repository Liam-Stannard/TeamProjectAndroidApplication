package com.example.vlady.newair.Fragments.Graph;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.vlady.newair.Activity.MainActivity;
import com.example.vlady.newair.R;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Graph screen
 * @author Ioannis Gylaris, Vladislav Iliev
 */
public class GraphFragment extends Fragment {
    private MainActivity activity;

    private Date[] dates;
    private Polluters polluters;

    private LineChart lineChart;
    private int pollutersIndex;
    private boolean firstLoad;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.activity = (MainActivity) this.getActivity();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_graph, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.initialize(view);
        this.setUpLineChart();
    }

    /**
     * Initializes all components on start-up
     * @param view the app View
     */
    private void initialize(View view) {
        new GraphButtons(this);
        this.dates =
                this.activity.getSensorDataManager().getHistoryData().getDates();
        this.polluters = new Polluters(this);

        this.lineChart = view.findViewById(R.id.line_chart);
        this.firstLoad = true;
    }

    /**
     * Initializes the Date entries
     * @return the list of Date entries
     */
    private List<Entry> initializeEntries() {
        List<Entry> entryList = new ArrayList<>();
        int invalidInteger = this.activity.getResources().getInteger(R.integer.invalid_integer);
        for (int i = 0; i < this.dates.length; i++) {
            entryList.add(new Entry(i, invalidInteger));
        }
        return entryList;
    }

    /**
     * Initializes the Graph data set
     * @param entryList the entries for the data set
     * @return the data set
     */
    private LineDataSet initializeDataSet(List<Entry> entryList) {
        LineDataSet dataSet = new LineDataSet(
                entryList,
                getResources().getString(R.string.line_data_set_label));

        dataSet.setColor(
                getResources().getColor(R.color.navbarTextLight));
        dataSet.setCircleColor(
                getResources().getColor(R.color.colorAccent));
        dataSet.setCircleColorHole(
                getResources().getColor(R.color.colorAccent));
        dataSet.setCircleRadius(
                this.activity.getResources().getInteger(R.integer.graph_circle_radius));
        dataSet.setCircleHoleRadius(
                this.activity.getResources().getInteger(R.integer.graph_circle_hole_radius));
        dataSet.setLineWidth(
                this.activity.getResources().getInteger(R.integer.graph_line_width));
        dataSet.setHighLightColor(
                this.activity.getResources().getColor(R.color.brown_transparent));

        return dataSet;
    }

    /**
     * Merges all data sets in a list (only one data set is needed)
     * @param lineDataSet the data set
     * @return the data sets list
     */
    private List<ILineDataSet> initializeDataSetList(LineDataSet lineDataSet) {
        List<ILineDataSet> dataSetList = new ArrayList<>();
        dataSetList.add(lineDataSet);
        return dataSetList;
    }

    /**
     * Initializes the line data
     * @param dataSetList the line sets
     * @return the line data
     */
    private LineData initializeLineData(List<ILineDataSet> dataSetList) {
        LineData lineData = new LineData(dataSetList);
        lineData.setValueTextSize(
                this.activity.getResources().getInteger(R.integer.graph_text_size));
        lineData.setValueTextColor(getResources().getColor(R.color.colorPrimaryDark));
        return lineData;
    }

    /**
     * Sets up the line chart
     */
    private void setUpLineChart() {
        List<Entry> entryList = this.initializeEntries();
        LineDataSet lineDataSet = this.initializeDataSet(entryList);
        List<ILineDataSet> lineDataSetList = this.initializeDataSetList(lineDataSet);
        LineData lineData = this.initializeLineData(lineDataSetList);

        this.lineChart.setData(lineData);
        this.lineChart.getLegend().setEnabled(false);
        this.lineChart.getAxisRight().setDrawLabels(false);

        this.lineChart.setVisibleYRange(
                0,
                this.activity.getResources().getInteger(R.integer.graph_max_limit),
                lineDataSet.getAxisDependency());
        this.initializeDescription(this.lineChart);
        this.initializeXAxis(this.lineChart);

        this.pollutersIndex =
                this.lineChart.getLineData().getDataSetByIndex(0).getEntryCount() - 1;
        this.addListener();
    }

    /**
     * Adds listeners to the graph entries (circles are interactive)
     */
    private void addListener() {
        this.lineChart.setOnChartValueSelectedListener(
                new OnChartValueSelectedListener() {
                    @Override
                    public void onValueSelected(Entry e, Highlight h) {
                        valueSelected(lineChart.getLineData()
                                        .getDataSetByIndex(0).getEntryIndex(e));
                    }

                    @Override
                    public void onNothingSelected() {
                    }
                });
    }

    /**
     * Sequence to be executed when a circle is pressed
     * @param index the index of the circle
     */
    private void valueSelected(int index) {
        this.pollutersIndex = index;
        this.updatePolluters();
    }

    /**
     * Sets up the chart description
     * @param lineChart the line chart to add description to
     */
    private void initializeDescription(LineChart lineChart) {
        Description description = new Description();
        description.setTextSize(
                this.activity.getResources().getInteger(
                    R.integer.graph_description_text_size));
        description.setTextColor(getResources().getColor(R.color.colorAccent));
        description.setText(getResources().getString(R.string.graph_description));
        lineChart.setDescription(description);
    }

    /**
     * Initializes the X-Axis (the dates row)
     * @param lineChart
     */
    private void initializeXAxis(LineChart lineChart) {
        XAxis x_axis = lineChart.getXAxis();
        x_axis.setPosition(XAxis.XAxisPosition.BOTTOM);
        x_axis.setDrawGridLines(false);

        x_axis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return formatDateDate(dates[dates.length - (int) value - 1]);
            }
        });
        x_axis.setLabelRotationAngle(
                this.activity.getResources().getInteger(R.integer.graph_dates_angle));
    }

    /**
     * Converts a date to a String
     * @param date the date
     * @return the formatted String
     */
    public String formatDateDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return String.format(
                Locale.UK,
                this.activity.getString(R.string.date_format),
                calendar.get(Calendar.DAY_OF_MONTH),
                calendar.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.UK));
    }

    /**
     * Sequence to be executed on pressing the Refresh button
     */
    void onRefreshPressed() {
        this.activity.loadData();
    }

    /**
     * Updates the Graph screen
     */
    public void update() {
        this.updateGraph();
        this.updatePolluters();
    }

    /**
     * Updates the Graph
     */
    private void updateGraph() {
        ILineDataSet lineDataSet = this.lineChart.getLineData().getDataSetByIndex(0);
        double[] pm10 = this.activity.getSensorDataManager().getHistoryData().getPm10();

        if (this.firstLoad) {
            for (int i = 0; i < lineDataSet.getEntryCount() - 1; i++) {
                lineDataSet.getEntryForIndex(i).setY((float) pm10[pm10.length - 1 - i]);
            }

            this.firstLoad = false;
        }

        lineDataSet.getEntryForIndex(lineDataSet.getEntryCount() - 1).setY((float) pm10[0]);

        this.lineChart.notifyDataSetChanged();
        this.lineChart.invalidate();
    }

    /**
     * Updates the polluter statistics below the graph
     */
    private void updatePolluters() {
        int arrayIndex = this.dates.length - 1 - this.pollutersIndex;

        double pm25 = this.activity.getSensorDataManager().getHistoryData().getPm25()[arrayIndex];
        double pm10 = this.activity.getSensorDataManager().getHistoryData().getPm10()[arrayIndex];
        double o3 = this.activity.getSensorDataManager().getHistoryData().getO3()[arrayIndex];
        this.polluters.update(pm25, pm10, o3);
    }
}