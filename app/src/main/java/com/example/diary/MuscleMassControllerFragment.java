package com.example.diary;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.example.healthylife.R;
import com.example.user.User;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import static com.example.format_numbers.FormatNumbers.formatNumberWithOneDecimalPlace;

/**
 * A simple {@link Fragment} subclass.
 */
public class MuscleMassControllerFragment extends Fragment implements View.OnClickListener, DialogFragmentTargetMuscleMass.RefreshTargetMuscleMass,
        DialogFragmentCurrentMuscleMass.RefreshCurrentMuscleMass {

    private TextView textViewCurrentMuscleMassValue, textViewInitialMuscleMassValue, textViewTargetMuscleMassValue;
    private User user = User.getInstance();

    private LineChart lineChart;

    private ArrayList<String> datesMuscleMassController = new ArrayList<>();
    private ArrayList<Float> allMuscleMass = new ArrayList<>();

    private String date = "";

    private List<Entry> entries;

    private LineDataSet lineDataSet;
    private LineData lineData;

    private XAxis xAxis;
    private YAxis yAxis;
    private YAxis yAxis1;

    private LimitLine limitLineInitialMuscleMass;
    private LimitLine limitLineTargetMuscleMass;

    private RecyclerView recyclerView;
    private WeightControllerAdapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;


    public MuscleMassControllerFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        if (getArguments() != null) {

            datesMuscleMassController = (ArrayList<String>) getArguments().getSerializable("datesMuscleMassController");
            allMuscleMass = (ArrayList<Float>) getArguments().getSerializable("allMuscleMass");

        }

        date = ((MainScreen)getActivity()).getDate();

        return inflater.inflate(R.layout.fragment_muscle_mass_controller, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        textViewCurrentMuscleMassValue = view.findViewById(R.id.textViewCurrentMuscleMassValue);
        textViewInitialMuscleMassValue = view.findViewById(R.id.textViewInitialMuscleMassValue);
        textViewTargetMuscleMassValue = view.findViewById(R.id.textViewTargetMuscleMassValue);

        lineChart = view.findViewById(R.id.lineChart);

        recyclerView = view.findViewById(R.id.recyclerViewMuscleMassController);

        textViewCurrentMuscleMassValue.setOnClickListener(this);
        textViewTargetMuscleMassValue.setOnClickListener(this);

        layoutManager = new LinearLayoutManager(getActivity());
        mAdapter = new WeightControllerAdapter(datesMuscleMassController, allMuscleMass, getActivity(), R.layout.weight_controller_item);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(mAdapter);

        showInformation();
        buildChart();
    }


    @Override
    public void onClick(View view) {

        int id = view.getId();

        if(id == R.id.textViewCurrentMuscleMassValue) {

            DialogFragmentCurrentMuscleMass dialogFragmentCurrentMuscleMass = new DialogFragmentCurrentMuscleMass();

            Bundle bundle = new Bundle();
            bundle.putFloat("currentMuscleMass", allMuscleMass.get(allMuscleMass.size() - 1));

            dialogFragmentCurrentMuscleMass.setArguments(bundle);
            dialogFragmentCurrentMuscleMass.setTargetFragment(this, 0);
            dialogFragmentCurrentMuscleMass.show(getFragmentManager().beginTransaction(), "Current Muscle Mass");

        } else if(id == R.id.textViewTargetMuscleMassValue) {

            DialogFragmentTargetMuscleMass dialogFragmentTargetMuscleMass = new DialogFragmentTargetMuscleMass();

            Bundle bundle = new Bundle();
            bundle.putFloat("targetMuscleMass", user.getMuscleMass());

            dialogFragmentTargetMuscleMass.setArguments(bundle);
            dialogFragmentTargetMuscleMass.setTargetFragment(this, 1);
            dialogFragmentTargetMuscleMass.show(getFragmentManager().beginTransaction(), "Target Muscle Mass");
        }
    }

    private String changeFormatDate(String date) {

        String values [] = date.split(" ");

        String newDataFormat = values[1].replace("January", "JAN").replace("February", "FEB")
                .replace("March", "MAR").replace("April", "APR")
                .replace("May", "MAY").replace("June", "JUN")
                .replace("July", "JUL").replace("August", "AUG")
                .replace("September", "SEP").replace("October", "OCT")
                .replace("November", "NOV").replace("December", "DEC")+ " " + values[2];

        return newDataFormat;
    }

    private float getMaxValue(float targetMuscleMass) {

        ArrayList<Float> allNumbers = new ArrayList<>();

        for(Float number : allMuscleMass) {

            allNumbers.add(number);
        }

        if(user.getTargetMuscleMass() != 0) {

            allNumbers.add(targetMuscleMass);
        }

        Collections.sort(allNumbers);
        Collections.reverse(allNumbers);

        return allNumbers.get(0) + 5;
    }

    private float getMinValue(float targetMuscleMass) {

        ArrayList<Float> allNumbers = new ArrayList<>();

        for(Float number : allMuscleMass) {

            allNumbers.add(number);
        }

        if(user.getTargetMuscleMass() != 0) {

            allNumbers.add(targetMuscleMass);
        }

        Collections.sort(allNumbers);

        return allNumbers.get(0) - 5;
    }

    @Override
    public void refreshTargetMuscleMass(float targetMuscleMass) {

        user.setTargetMuscleMass(targetMuscleMass);

        yAxis.removeLimitLine(limitLineTargetMuscleMass);

        limitLineTargetMuscleMass = new LimitLine(targetMuscleMass, "Target Muscle Mass");
        limitLineTargetMuscleMass.setLineWidth(2f);
        limitLineTargetMuscleMass.enableDashedLine(10f, 5f, 0f);
        limitLineTargetMuscleMass.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
        limitLineTargetMuscleMass.setTextSize(9f);

        yAxis.addLimitLine(limitLineTargetMuscleMass);

        yAxis.setAxisMaximum(getMaxValue(targetMuscleMass));
        yAxis.setAxisMinimum(getMinValue(targetMuscleMass));

        yAxis1.setAxisMaximum(getMaxValue(targetMuscleMass));
        yAxis1.setAxisMinimum(getMinValue(targetMuscleMass));

        textViewTargetMuscleMassValue.setText(formatNumberWithOneDecimalPlace(targetMuscleMass) + " kg");

        if((allMuscleMass.size() > 2) && (datesMuscleMassController.size() > 2)) {

            lineChart.zoomOut();
            lineChart.zoom(1.15f, 0, 0, 0);

        } else {

            lineChart.zoomOut();
            lineChart.zoom(0, 0, 0, 0);
        }
    }

    @Override
    public void refreshCurrentMuscleMass(float currentMuscleMass, String date) {

        user.setMuscleMass(currentMuscleMass);
        textViewCurrentMuscleMassValue.setText(formatNumberWithOneDecimalPlace(currentMuscleMass) + " kg");

        if(datesMuscleMassController.get(datesMuscleMassController.size() - 1).equals(date)) {

            allMuscleMass.set(allMuscleMass.size() - 1, currentMuscleMass);

            entries.clear();

            for(int i = 0; i < allMuscleMass.size(); i++) {

                entries.add(new Entry(i, allMuscleMass.get(i)));
            }

            if(allMuscleMass.size() == 1) {

                textViewInitialMuscleMassValue.setText(formatNumberWithOneDecimalPlace(allMuscleMass.get(0)) + " kg");

                yAxis.removeLimitLine(limitLineInitialMuscleMass);

                limitLineInitialMuscleMass = new LimitLine(allMuscleMass.get(0), "Initial Muscle Mass");
                limitLineInitialMuscleMass.setLineWidth(2f);
                limitLineInitialMuscleMass.enableDashedLine(10f, 5f, 0f);
                limitLineInitialMuscleMass.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
                limitLineInitialMuscleMass.setTextSize(9f);
                yAxis.addLimitLine(limitLineInitialMuscleMass);
            }

        } else {

            datesMuscleMassController.add(date);
            allMuscleMass.add(currentMuscleMass);

            entries.clear();

            for(int i = 0; i < allMuscleMass.size(); i++) {

                entries.add(new Entry(i, allMuscleMass.get(i)));
            }

            lineDataSet = new LineDataSet(entries, "");
            lineDataSet.setCircleColor(Color.BLUE);
            lineDataSet.setCircleRadius(4f);
            lineDataSet.setCircleHoleRadius(2f);
            lineDataSet.setColor(Color.RED);
            lineDataSet.setLineWidth(2.5f);

            lineData = new LineData(lineDataSet);
            lineData.setDrawValues(false);

            lineChart.setData(lineData);

        }

        yAxis.setAxisMaximum(getMaxValue(user.getTargetMuscleMass()));
        yAxis.setAxisMinimum(getMinValue(user.getTargetMuscleMass()));

        yAxis1.setAxisMaximum(getMaxValue(user.getTargetMuscleMass()));
        yAxis1.setAxisMinimum(getMinValue(user.getTargetMuscleMass()));

        mAdapter.notifyDataSetChanged();
        lineChart.notifyDataSetChanged();
    }

    public void showInformation() {

        if(user.getTargetMuscleMass() != 0) {

            textViewTargetMuscleMassValue.setText(formatNumberWithOneDecimalPlace(user.getTargetMuscleMass()) + " kg");
        }

        if(allMuscleMass.size() > 0) {

            textViewInitialMuscleMassValue.setText(getResources().getString(R.string.textViewInitialWeightValue,
                    formatNumberWithOneDecimalPlace(allMuscleMass.get(0))));

            textViewCurrentMuscleMassValue.setText(formatNumberWithOneDecimalPlace(allMuscleMass.get(allMuscleMass.size() - 1)) + " kg");
        }
    }

    public void buildChart() {

        if(allMuscleMass.size() > 0) {

            entries = new ArrayList<>();

            for(int i = 0; i < allMuscleMass.size(); i++) {

                entries.add(new Entry(i, allMuscleMass.get(i)));
            }

            lineDataSet = new LineDataSet(entries, "");
            lineDataSet.setCircleColor(Color.BLUE);
            lineDataSet.setCircleRadius(4f);
            lineDataSet.setCircleHoleRadius(2f);
            lineDataSet.setColor(Color.RED);
            lineDataSet.setLineWidth(2.5f);

            lineData = new LineData(lineDataSet);
            lineData.setDrawValues(false);

            lineChart.setData(lineData);

            ValueFormatter valueFormatter = new ValueFormatter() {
                @Override
                public String getFormattedValue(float value) {

                    if((allMuscleMass.size() == 1) && (datesMuscleMassController.size() == 1)) {

                        return changeFormatDate(datesMuscleMassController.get(0));

                    } else {

                        return changeFormatDate(datesMuscleMassController.get((int) value));
                    }
                }
            };

            xAxis = lineChart.getXAxis();
            yAxis = lineChart.getAxisLeft();
            yAxis1 = lineChart.getAxisRight();

            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxis.setGranularity(1f);
            xAxis.setValueFormatter(valueFormatter);
            xAxis.setTextSize(9);
            xAxis.setTypeface(Typeface.DEFAULT_BOLD);

            yAxis.setAxisMaximum(getMaxValue(user.getTargetMuscleMass()));
            yAxis.setAxisMinimum(getMinValue(user.getTargetMuscleMass()));

            yAxis1.setAxisMaximum(getMaxValue(user.getTargetMuscleMass()));
            yAxis1.setAxisMinimum(getMinValue(user.getTargetMuscleMass()));

            limitLineInitialMuscleMass = new LimitLine(allMuscleMass.get(0), "Initial muscle mass");
            limitLineInitialMuscleMass.setLineWidth(2f);
            limitLineInitialMuscleMass.enableDashedLine(10f, 5f, 0f);
            limitLineInitialMuscleMass.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
            limitLineInitialMuscleMass.setTextSize(9f);
            yAxis.addLimitLine(limitLineInitialMuscleMass);

            if(user.getTargetMuscleMass() != 0) {

                limitLineTargetMuscleMass = new LimitLine(user.getTargetMuscleMass(), "Target muscle mass");
                limitLineTargetMuscleMass.setLineWidth(2f);
                limitLineTargetMuscleMass.enableDashedLine(10f, 5f, 0f);
                limitLineTargetMuscleMass.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
                limitLineTargetMuscleMass.setTextSize(9f);

                yAxis.addLimitLine(limitLineTargetMuscleMass);
            }

            Description desc = new Description();
            desc.setText("");

            lineChart.setDescription(desc);
            lineChart.getLegend().setEnabled(false);
            lineChart.getAxisLeft().setDrawGridLines(true);
            lineChart.setScaleEnabled(false);

            if((allMuscleMass.size() > 2) && (datesMuscleMassController.size() > 2)) {

                lineChart.zoom(1.15f, 0, 0, 0);
            }

            List<ILineDataSet> sets = lineChart.getData()
                    .getDataSets();

            for (ILineDataSet iSet : sets) {

                LineDataSet set = (LineDataSet) iSet;
                set.setDrawValues(!set.isDrawValuesEnabled());
                set.setValueTextSize(8f);
            }
        }
    }
}
