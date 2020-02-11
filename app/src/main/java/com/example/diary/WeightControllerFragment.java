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
public class WeightControllerFragment extends Fragment implements View.OnClickListener, DialogFragmentTargetWeight.RefreshTargetWeight,
        DialogFragmentCurrentWeight.RefreshCurrentWeight {

    private TextView textViewCurrentWeightValue, textViewInitialWeightValue, textViewTargetWeightValue;
    private User user = User.getInstance();

    private LineChart lineChart;

    private ArrayList<String> datesWeightController = new ArrayList<>();
    private ArrayList<Float> allWeights = new ArrayList<>();

    private String date = "";

    private List<Entry> entries;

    private LineDataSet lineDataSet;
    private LineData lineData;

    private XAxis xAxis;
    private YAxis yAxis;
    private YAxis yAxis1;

    private LimitLine limitLineInitialWeight;
    private LimitLine limitLineTargetWeight;

    private RecyclerView recyclerView;
    private WeightControllerAdapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;


    public WeightControllerFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        if (getArguments() != null) {

            datesWeightController = (ArrayList<String>) getArguments().getSerializable("datesWeightController");
            allWeights = (ArrayList<Float>) getArguments().getSerializable("allWeights");

        }

        date = ((MainScreen)getActivity()).getDate();

        return inflater.inflate(R.layout.fragment_weight_controller, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        textViewCurrentWeightValue = view.findViewById(R.id.textViewCurrentWeightValue);
        textViewInitialWeightValue = view.findViewById(R.id.textViewInitialWeightValue);
        textViewTargetWeightValue = view.findViewById(R.id.textViewTargetWeightValue);

        lineChart = view.findViewById(R.id.lineChart);

        recyclerView = view.findViewById(R.id.recyclerViewWeightController);

        textViewCurrentWeightValue.setOnClickListener(this);
        textViewTargetWeightValue.setOnClickListener(this);

        layoutManager = new LinearLayoutManager(getActivity());
        mAdapter = new WeightControllerAdapter(datesWeightController, allWeights, getActivity(), R.layout.weight_controller_item);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(mAdapter);

        showInformation();
        buildChart();
    }


    @Override
    public void onClick(View view) {

        int id = view.getId();

        if(id == R.id.textViewCurrentWeightValue) {

            DialogFragmentCurrentWeight dialogFragmentCurrentWeight = new DialogFragmentCurrentWeight();

            Bundle bundle = new Bundle();
            bundle.putFloat("currentWeight", allWeights.get(allWeights.size() - 1));

            dialogFragmentCurrentWeight.setArguments(bundle);
            dialogFragmentCurrentWeight.setTargetFragment(this, 0);
            dialogFragmentCurrentWeight.show(getFragmentManager().beginTransaction(), "Current Weight");

        } else if(id == R.id.textViewTargetWeightValue) {

            DialogFragmentTargetWeight dialogFragmentTargetWeight = new DialogFragmentTargetWeight();

            Bundle bundle = new Bundle();
            bundle.putFloat("targetWeight", user.getTargetWeight());

            dialogFragmentTargetWeight.setArguments(bundle);
            dialogFragmentTargetWeight.setTargetFragment(this, 1);
            dialogFragmentTargetWeight.show(getFragmentManager().beginTransaction(), "Target Weight");
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

    private float getMaxValue(float targetWeight) {

        ArrayList<Float> allNumbers = new ArrayList<>();

        for(Float number : allWeights) {

            allNumbers.add(number);
        }

        if(user.getTargetWeight() != 0) {

            allNumbers.add(targetWeight);
        }

        Collections.sort(allNumbers);
        Collections.reverse(allNumbers);

        return allNumbers.get(0) + 5;
    }

    private float getMinValue(float targetWeight) {

        ArrayList<Float> allNumbers = new ArrayList<>();

        for(Float number : allWeights) {

            allNumbers.add(number);
        }

        if(user.getTargetWeight() != 0) {

            allNumbers.add(targetWeight);
        }

        Collections.sort(allNumbers);

        return allNumbers.get(0) - 5;
    }

    @Override
    public void refreshTargetWeight(float targetWeight) {

        user.setTargetWeight(targetWeight);

        yAxis.removeLimitLine(limitLineTargetWeight);

        limitLineTargetWeight = new LimitLine(targetWeight, "Target Weight");
        limitLineTargetWeight.setLineWidth(2f);
        limitLineTargetWeight.enableDashedLine(10f, 5f, 0f);
        limitLineTargetWeight.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
        limitLineTargetWeight.setTextSize(9f);

        yAxis.addLimitLine(limitLineTargetWeight);

        yAxis.setAxisMaximum(getMaxValue(targetWeight));
        yAxis.setAxisMinimum(getMinValue(targetWeight));

        yAxis1.setAxisMaximum(getMaxValue(targetWeight));
        yAxis1.setAxisMinimum(getMinValue(targetWeight));

        textViewTargetWeightValue.setText(formatNumberWithOneDecimalPlace(targetWeight) + " kg");

        if((allWeights.size() > 2) && (datesWeightController.size() > 2)) {

            lineChart.zoomOut();
            lineChart.zoom(1.15f, 0, 0, 0);

        } else {

            lineChart.zoomOut();
            lineChart.zoom(0, 0, 0, 0);
        }
    }

    @Override
    public void refreshCurrentWeight(float currentWeight, String date) {

        user.setWeight(currentWeight);
        textViewCurrentWeightValue.setText(formatNumberWithOneDecimalPlace(currentWeight) + " kg");

        if(datesWeightController.get(datesWeightController.size() - 1).equals(date)) {

            allWeights.set(allWeights.size() - 1, currentWeight);

            entries.clear();

            for(int i = 0; i < allWeights.size(); i++) {

                entries.add(new Entry(i, allWeights.get(i)));
            }

            if(allWeights.size() == 1) {

                textViewInitialWeightValue.setText(formatNumberWithOneDecimalPlace(allWeights.get(0)) + " kg");

                yAxis.removeLimitLine(limitLineInitialWeight);

                limitLineInitialWeight = new LimitLine(allWeights.get(0), "Initial Weight");
                limitLineInitialWeight.setLineWidth(2f);
                limitLineInitialWeight.enableDashedLine(10f, 5f, 0f);
                limitLineInitialWeight.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
                limitLineInitialWeight.setTextSize(9f);
                yAxis.addLimitLine(limitLineInitialWeight);
            }


        } else {

            datesWeightController.add(date);
            allWeights.add(currentWeight);

            entries.clear();

            for(int i = 0; i < allWeights.size(); i++) {

                entries.add(new Entry(i, allWeights.get(i)));
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

        yAxis.setAxisMaximum(getMaxValue(user.getTargetWeight()));
        yAxis.setAxisMinimum(getMinValue(user.getTargetWeight()));

        yAxis1.setAxisMaximum(getMaxValue(user.getTargetWeight()));
        yAxis1.setAxisMinimum(getMinValue(user.getTargetWeight()));

        mAdapter.notifyDataSetChanged();
        lineChart.notifyDataSetChanged();
    }

    public void showInformation() {

        if(user.getTargetWeight() != 0) {

            textViewTargetWeightValue.setText(formatNumberWithOneDecimalPlace(user.getTargetWeight()) + " kg");
        }

        if(allWeights.size() > 0) {

            textViewInitialWeightValue.setText(getResources().getString(R.string.textViewInitialWeightValue,
                    formatNumberWithOneDecimalPlace(allWeights.get(0))));

            textViewCurrentWeightValue.setText(formatNumberWithOneDecimalPlace(allWeights.get(allWeights.size() - 1)) + " kg");
        }
    }

    public void buildChart() {

        if(allWeights.size() > 0) {

            entries = new ArrayList<>();

            for(int i = 0; i < allWeights.size(); i++) {

                entries.add(new Entry(i, allWeights.get(i)));
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

                    if((allWeights.size() == 1) && (datesWeightController.size() == 1)) {

                        return changeFormatDate(datesWeightController.get(0));

                    } else {

                        return changeFormatDate(datesWeightController.get((int) value));
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

            yAxis.setAxisMaximum(getMaxValue(user.getTargetWeight()));
            yAxis.setAxisMinimum(getMinValue(user.getTargetWeight()));

            yAxis1.setAxisMaximum(getMaxValue(user.getTargetWeight()));
            yAxis1.setAxisMinimum(getMinValue(user.getTargetWeight()));

            limitLineInitialWeight = new LimitLine(allWeights.get(0), "Initial Weight");
            limitLineInitialWeight.setLineWidth(2f);
            limitLineInitialWeight.enableDashedLine(10f, 5f, 0f);
            limitLineInitialWeight.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
            limitLineInitialWeight.setTextSize(9f);
            yAxis.addLimitLine(limitLineInitialWeight);

            if(user.getTargetWeight() != 0) {

                limitLineTargetWeight = new LimitLine(user.getTargetWeight(), "Target Weight");
                limitLineTargetWeight.setLineWidth(2f);
                limitLineTargetWeight.enableDashedLine(10f, 5f, 0f);
                limitLineTargetWeight.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
                limitLineTargetWeight.setTextSize(9f);

                yAxis.addLimitLine(limitLineTargetWeight);
            }

            Description desc = new Description();
            desc.setText("");

            lineChart.setDescription(desc);
            lineChart.getLegend().setEnabled(false);
            lineChart.getAxisLeft().setDrawGridLines(true);
            lineChart.setScaleEnabled(false);

            if((allWeights.size() > 2) && (datesWeightController.size() > 2)) {

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