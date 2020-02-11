package com.example.diary;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import com.example.adding_product.SearchProduct;
import com.example.caloric_demand.MifflinStJeor;
import com.example.edit_product.EditProduct;
import com.example.expandable_recyclerview.Meal;
import com.example.expandable_recyclerview.MealExpandableRecyclerView;
import com.example.expandable_recyclerview.Product;
import com.example.expandable_recyclerview.ProductAdapter;
import com.example.healthylife.R;
import com.example.user.User;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.List;
import static com.example.expandable_recyclerview.Meal.breakfastTitle;
import static com.example.expandable_recyclerview.Meal.dinnerTitle;
import static com.example.expandable_recyclerview.Meal.lunchTitle;
import static com.example.expandable_recyclerview.Meal.snacksTitle;
import static com.example.format_numbers.FormatNumbers.formatNumberWithDecimalPlaces;
import static com.example.format_numbers.FormatNumbers.formatNumberWithoutDecimalPlaces;

/**
 * A simple {@link Fragment} subclass.
 */
public class DietDiaryFragment extends Fragment implements View.OnClickListener {

    private RecyclerView recyclerViewBreakfast, recyclerViewLunch, recyclerViewDinner, recyclerViewSnacks;
    private Button buttonAddProductBreakfast, buttonAddProductLunch, buttonAddProductDinner, buttonAddProductSnacks;
    private TextView textViewTotalCaloriesBreakfast, textViewTotalCaloriesLunch, textViewTotalCaloriesDinner, textViewTotalCaloriesSnacks,
            textViewCaloriesConsumedAmount, textViewCaloriesToBeConsumedAmount, textViewPercentFromGDA, textViewSummaryCarbohydrates,
            textViewSummaryProtein, textViewSummaryFat, textViewTotalCaloriesGDA;

    private TableLayout tableLayout;

    private LinearLayout linearLayoutSummaryMacro;

    private PieChart pieChart;

    private NestedScrollView nestedScrollViewDietDiary;

    private MifflinStJeor mifflinStJeor = new MifflinStJeor();

    private FirebaseFirestore firebaseFirestore;
    private String currentUserUID = "";

    private Meal breakfast;
    private Meal lunch;
    private Meal dinner;
    private Meal snacks;

    private AllMealsOfTheDay allMealsOfTheDay = new AllMealsOfTheDay();

    private MealExpandableRecyclerView breakfastExpandableRecyclerView, lunchExpandableRecyclerView, dinnerExpandableRecyclerView, snacksExpandableRecyclerView;
    private List<MealExpandableRecyclerView> listBreakfast, listLunch, listDinner, listSnacks;

    private ProductAdapter breakfastAdapter, lunchAdapter, dinnerAdapter, snacksAdapter;

    private String date = "";

    private final int rowsNumber = 10;
    private final int columnsNumber = 10;

    private final String titles[] = {"Carbohydrates", "Protein", "Fat"};
    private final int piecesOfChart = 3;

    private User user = User.getInstance();

    private EnableBottomBar enableBottomBar;
    private CollapseCalendar collapseCalendar;

    public DietDiaryFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        enableBottomBar = ((MainScreen)context);
        collapseCalendar = ((MainScreen)context);
    }

    @SuppressWarnings("unchecked")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        if(getArguments() != null) {

            date = getArguments().getString("date");
            breakfast = getArguments().getParcelable("breakfast");
            lunch = getArguments().getParcelable("lunch");
            dinner = getArguments().getParcelable("dinner");
            snacks = getArguments().getParcelable("snacks");
        }

        ((MainScreen)getActivity()).setRefreshDate(new MainScreen.RefreshDate() {
            @Override
            public void refreshDate(String newDate) {

                date = newDate;
                Log.i("date", date);
            }
        });

        ((MainScreen)getActivity()).setFragmentRefreshDietDiary(new MainScreen.FragmentRefreshDietDiary() {
            @Override
            public void refresh() {

                breakfast.calculateTotalCarbohydratesOfMeal();
                breakfast.calculateTotalProteinOfMeal();
                breakfast.calculateTotalFatOfMeal();
                breakfast.calculateTotalCaloriesOfMeal();
                showBreakfastCalories();

                lunch.calculateTotalCarbohydratesOfMeal();
                lunch.calculateTotalProteinOfMeal();
                lunch.calculateTotalFatOfMeal();
                lunch.calculateTotalCaloriesOfMeal();
                showLunchCalories();

                dinner.calculateTotalCarbohydratesOfMeal();
                dinner.calculateTotalProteinOfMeal();
                dinner.calculateTotalFatOfMeal();
                dinner.calculateTotalCaloriesOfMeal();
                showDinnerCalories();

                snacks.calculateTotalCarbohydratesOfMeal();
                snacks.calculateTotalProteinOfMeal();
                snacks.calculateTotalFatOfMeal();
                snacks.calculateTotalCaloriesOfMeal();
                showSnacksCalories();

                summaryCalories();

                calculateMacronutrientsOfAllMeals();
                calculatePercentMacronutrientsOfAllMeals();

                showMacronutrientsSummaryAndBuildPieceChart();

                breakfastAdapter.notifyDataSetChanged();
                lunchAdapter.notifyDataSetChanged();
                dinnerAdapter.notifyDataSetChanged();
                snacksAdapter.notifyDataSetChanged();

                enableBottomBar.enable(true);
            }
        });

        return inflater.inflate(R.layout.fragment_diet_diary, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        currentUserUID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        firebaseFirestore = FirebaseFirestore.getInstance();

        recyclerViewBreakfast = view.findViewById(R.id.recyclerViewBreakfast);
        recyclerViewLunch = view.findViewById(R.id.recyclerViewLunch);
        recyclerViewDinner = view.findViewById(R.id.recyclerViewDinner);
        recyclerViewSnacks = view.findViewById(R.id.recyclerSnacks);

        recyclerViewBreakfast.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerViewLunch.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerViewDinner.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerViewSnacks.setLayoutManager(new LinearLayoutManager(getActivity()));

        buttonAddProductBreakfast = view.findViewById(R.id.buttonAddProductBreakfast);
        buttonAddProductLunch = view.findViewById(R.id.buttonAddProductLunch);
        buttonAddProductDinner = view.findViewById(R.id.buttonAddProductDinner);
        buttonAddProductSnacks = view.findViewById(R.id.buttonAddSnacks);

        textViewTotalCaloriesBreakfast = view.findViewById(R.id.textViewTotalCaloriesBreakfast);
        textViewTotalCaloriesLunch = view.findViewById(R.id.textViewTotalCaloriesLunch);
        textViewTotalCaloriesDinner = view.findViewById(R.id.textViewTotalCaloriesDinner);
        textViewTotalCaloriesSnacks = view.findViewById(R.id.textViewTotalCaloriesSnacks);
        textViewCaloriesConsumedAmount = view.findViewById(R.id.textViewCaloriesConsumedAmount);
        textViewCaloriesToBeConsumedAmount = view.findViewById(R.id.textViewCaloriesToBeConsumedAmount);
        textViewPercentFromGDA = view.findViewById(R.id.textViewPercentFromGDA);
        textViewSummaryCarbohydrates = view.findViewById(R.id.textViewSummaryCarbohydrates);
        textViewSummaryProtein = view.findViewById(R.id.textViewSummaryProtein);
        textViewSummaryFat = view.findViewById(R.id.textViewSummaryFat);
        textViewTotalCaloriesGDA = view.findViewById(R.id.textViewTotalCaloriesGDA);

        linearLayoutSummaryMacro = view.findViewById(R.id.linearLayoutSummaryMacro);

        tableLayout = view.findViewById(R.id.tableLayout);
        tableLayout.setRotationX(180f);

        pieChart = view.findViewById(R.id.pieChart);

        nestedScrollViewDietDiary = view.findViewById(R.id.nestedScrollViewDietDiary);
        ViewCompat.setNestedScrollingEnabled(nestedScrollViewDietDiary, false);
        nestedScrollViewDietDiary.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {

                if(getCollapseCalendar() != null) {

                    collapseCalendar.collapse();
                }
            }
        });

        buttonAddProductBreakfast.setOnClickListener(this);
        buttonAddProductLunch.setOnClickListener(this);
        buttonAddProductDinner.setOnClickListener(this);
        buttonAddProductSnacks.setOnClickListener(this);

        recyclerViewBreakfast.setNestedScrollingEnabled(false);
        recyclerViewLunch.setNestedScrollingEnabled(false);
        recyclerViewDinner.setNestedScrollingEnabled(false);
        recyclerViewSnacks.setNestedScrollingEnabled(false);

        listBreakfast = new ArrayList<>();
        breakfastExpandableRecyclerView = new MealExpandableRecyclerView("", breakfast.getTotalProductsOfMeal());
        listBreakfast.add(breakfastExpandableRecyclerView);
        breakfastAdapter = new ProductAdapter(listBreakfast);
        recyclerViewBreakfast.setAdapter(breakfastAdapter);

        listLunch = new ArrayList<>();
        lunchExpandableRecyclerView = new MealExpandableRecyclerView("", lunch.getTotalProductsOfMeal());
        listLunch.add(lunchExpandableRecyclerView);
        lunchAdapter = new ProductAdapter(listLunch);
        recyclerViewLunch.setAdapter(lunchAdapter);

        listDinner = new ArrayList<>();
        dinnerExpandableRecyclerView = new MealExpandableRecyclerView("", dinner.getTotalProductsOfMeal());
        listDinner.add(dinnerExpandableRecyclerView);
        dinnerAdapter = new ProductAdapter(listDinner);
        recyclerViewDinner.setAdapter(dinnerAdapter);

        listSnacks = new ArrayList<>();
        snacksExpandableRecyclerView = new MealExpandableRecyclerView("", snacks.getTotalProductsOfMeal());
        listSnacks.add(snacksExpandableRecyclerView);
        snacksAdapter = new ProductAdapter(listSnacks);
        recyclerViewSnacks.setAdapter(snacksAdapter);

        showBreakfastCalories();
        showLunchCalories();
        showDinnerCalories();
        showSnacksCalories();

        summaryCalories();

        calculateMacronutrientsOfAllMeals();
        calculatePercentMacronutrientsOfAllMeals();

        setPieChart();
        showMacronutrientsSummaryAndBuildPieceChart();

        breakfastAdapter.setOnItemClickListener(new ProductAdapter.OnItemClickListener() {
            @Override
            public void OnItemClick(Product product, int position) {

                Intent intent = new Intent(getActivity(), EditProduct.class);
                intent.putExtra("product", product);
                intent.putExtra("date", date);
                intent.putExtra("meal", breakfastTitle);
                intent.putExtra("position", position);
                intent.putExtra("idProducts", breakfast.getIdProductsOfMeal());
                intent.putExtra("consumedCalories", allMealsOfTheDay.getTotalCaloriesOfAllMeals());

                startActivity(intent);
            }
        });

        lunchAdapter.setOnItemClickListener(new ProductAdapter.OnItemClickListener() {
            @Override
            public void OnItemClick(Product product, int position) {

                Intent intent = new Intent(getActivity(), EditProduct.class);
                intent.putExtra("product", product);
                intent.putExtra("date", date);
                intent.putExtra("meal", lunchTitle);
                intent.putExtra("position", position);
                intent.putExtra("idProducts", lunch.getIdProductsOfMeal());
                intent.putExtra("consumedCalories", allMealsOfTheDay.getTotalCaloriesOfAllMeals());

                startActivity(intent);
            }
        });

        dinnerAdapter.setOnItemClickListener(new ProductAdapter.OnItemClickListener() {
            @Override
            public void OnItemClick(Product product, int position) {

                Intent intent = new Intent(getActivity(), EditProduct.class);
                intent.putExtra("product", product);
                intent.putExtra("date", date);
                intent.putExtra("meal", dinnerTitle);
                intent.putExtra("position", position);
                intent.putExtra("idProducts", dinner.getIdProductsOfMeal());
                intent.putExtra("consumedCalories", allMealsOfTheDay.getTotalCaloriesOfAllMeals());

                startActivity(intent);
            }
        });

        snacksAdapter.setOnItemClickListener(new ProductAdapter.OnItemClickListener() {
            @Override
            public void OnItemClick(Product product, int position) {

                Intent intent = new Intent(getActivity(), EditProduct.class);
                intent.putExtra("product", product);
                intent.putExtra("date", date);
                intent.putExtra("meal", snacksTitle);
                intent.putExtra("position", position);
                intent.putExtra("idProducts",snacks.getIdProductsOfMeal());
                intent.putExtra("consumedCalories", allMealsOfTheDay.getTotalCaloriesOfAllMeals());

                startActivity(intent);
            }
        });
    }

    @Override
    public void onClick(View view) {

        int id = view.getId();

        date = ((MainScreen)getActivity()).getDate();

        switch(id) {

            case R.id.buttonAddProductBreakfast:

                Intent breakfastIntent = new Intent(getActivity(), SearchProduct.class);

                breakfastIntent.putExtra("date", date);
                breakfastIntent.putExtra("meal", breakfastTitle);
                breakfastIntent.putExtra("consumedCalories", allMealsOfTheDay.getTotalCaloriesOfAllMeals());

                startActivity(breakfastIntent);
                break;

            case R.id.buttonAddProductLunch:

                Intent lunchIntent = new Intent(getActivity(), SearchProduct.class);

                lunchIntent.putExtra("date", date);
                lunchIntent.putExtra("meal", lunchTitle);
                lunchIntent.putExtra("consumedCalories", allMealsOfTheDay.getTotalCaloriesOfAllMeals());

                startActivity(lunchIntent);
                break;

            case R.id.buttonAddProductDinner:

                Intent dinnerIntent = new Intent(getActivity(), SearchProduct.class);

                dinnerIntent.putExtra("date", date);
                dinnerIntent.putExtra("meal", dinnerTitle);
                dinnerIntent.putExtra("consumedCalories", allMealsOfTheDay.getTotalCaloriesOfAllMeals());

                startActivity(dinnerIntent);
                break;

            case R.id.buttonAddSnacks:

                Intent snacksIntent = new Intent(getActivity(), SearchProduct.class);

                snacksIntent.putExtra("date", date);
                snacksIntent.putExtra("meal", snacksTitle);
                snacksIntent.putExtra("consumedCalories", allMealsOfTheDay.getTotalCaloriesOfAllMeals());

                startActivity(snacksIntent);
                break;

        }
    }

    private int getCellDrawableId(int i, int j, float totalCaloriesDay, float currentCaloriesDay) {

        if(currentCaloriesDay > totalCaloriesDay) {

            return R.drawable.tab_layout_draw_too_much_calories;

        } else {

            int percent = Math.round((currentCaloriesDay/totalCaloriesDay) * 100);

            int rows = (int)percent / 10;
            int columns = (int)percent % 10;

            if(rows > i) {

                return R.drawable.tab_layout_draw_calories;

            } else if(rows == i) {

                if (columns > j) {

                    return R.drawable.tab_layout_draw_calories;
                }
            }

            return R.drawable.tab_layout_draw_no_calories;
        }
    }

    private void buildDiagram() {

        tableLayout.removeAllViews();

        for (int i = 0; i < rowsNumber; i++) {

            TableRow tableRow = new TableRow(getActivity());
            tableRow.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                    TableRow.LayoutParams.WRAP_CONTENT));

            for (int j = 0; j < columnsNumber; j++) {

                ImageView imageView = new ImageView(getActivity());

                imageView.setImageResource(this.getCellDrawableId(i, j, mifflinStJeor.getGda(),  allMealsOfTheDay.getTotalCaloriesOfAllMeals()));
                imageView.setPadding(2,2,2,2);

                tableRow.addView(imageView, j);
            }

            tableLayout.addView(tableRow, i);
        }
    }

    private void setData(int count, float numbers[]) {

        ArrayList<PieEntry> entries = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            entries.add(new PieEntry(numbers[i], titles[i % count]));
        }

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);

        ArrayList<Integer> colors = new ArrayList<>();

        for (int c : ColorTemplate.COLORFUL_COLORS)
            colors.add(c);

        dataSet.setColors(colors);

        dataSet.setUsingSliceColorAsValueLineColor(true);

        dataSet.setYValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);

        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(11f);
        data.setValueTextColor(Color.BLACK);
        pieChart.setData(data);
    }

    private void setPieChart() {

        pieChart.setUsePercentValues(true);
        pieChart.getDescription().setEnabled(false);
        pieChart.setDrawEntryLabels(!pieChart.isDrawEntryLabelsEnabled());

        pieChart.setDragDecelerationFrictionCoef(0.95f);

        pieChart.setExtraOffsets(16f, 8f, 16f, 8f);

        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleColor(getResources().getColor(R.color.dietDiary_background));

        pieChart.setTransparentCircleColor(Color.WHITE);
        pieChart.setTransparentCircleAlpha(255);

        pieChart.setHoleRadius(58f);
        pieChart.setTransparentCircleRadius(63f);

        pieChart.setRotationAngle(0);
        pieChart.setRotationEnabled(true);
        pieChart.setHighlightPerTapEnabled(true);

        pieChart.animateY(7000, Easing.EaseInOutQuad);

        Legend l = pieChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setDrawInside(false);
        l.setEnabled(true);
    }

    private void showBreakfastCalories() {

        if(breakfast.getTotalProductsOfMeal().size() == 0) {

            textViewTotalCaloriesBreakfast.setText("");

        } else {

            textViewTotalCaloriesBreakfast.setText(formatNumberWithoutDecimalPlaces(breakfast.getTotalCaloriesOfMeal()));
        }
    }

    private void showLunchCalories() {

        if(lunch.getTotalProductsOfMeal().size() == 0) {

            textViewTotalCaloriesLunch.setText("");

        } else {

            textViewTotalCaloriesLunch.setText(formatNumberWithoutDecimalPlaces(lunch.getTotalCaloriesOfMeal()));
        }
    }

    private void showDinnerCalories() {

        if(dinner.getTotalProductsOfMeal().size() == 0) {

            textViewTotalCaloriesDinner.setText("");

        } else {

            textViewTotalCaloriesDinner.setText(formatNumberWithoutDecimalPlaces(dinner.getTotalCaloriesOfMeal()));
        }
    }

    private void showSnacksCalories() {

        if(snacks.getTotalProductsOfMeal().size() == 0) {

            textViewTotalCaloriesSnacks.setText("");

        } else {

            textViewTotalCaloriesSnacks.setText(formatNumberWithoutDecimalPlaces(snacks.getTotalCaloriesOfMeal()));
        }
    }

    private void showMacronutrientsSummaryAndBuildPieceChart() {

        ViewGroup.LayoutParams layoutParams = linearLayoutSummaryMacro.getLayoutParams();
        PieChart.LayoutParams layoutParams1 = pieChart.getLayoutParams();

        if((allMealsOfTheDay.getTotalCarbohydratesOfAllMeals() == 0) && (allMealsOfTheDay.getTotalProteinOfAllMeals() == 0)
                && (allMealsOfTheDay.getTotalFatOfAllMeals() == 0)) {

            layoutParams.width = 0;
            layoutParams.height = 0;

            linearLayoutSummaryMacro.setLayoutParams(layoutParams);

            layoutParams1.width = 0;
            layoutParams1.height = 0;

            pieChart.setLayoutParams(layoutParams1);

        } else {

            layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT;
            layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;

            linearLayoutSummaryMacro.setLayoutParams(layoutParams);

            layoutParams1.width = ViewGroup.LayoutParams.MATCH_PARENT;

            float factor = getContext().getResources().getDisplayMetrics().density;
            layoutParams1.height = (int)(200 * factor);

            pieChart.setLayoutParams(layoutParams1);

            textViewSummaryCarbohydrates.setText(getResources().getString(R.string.textViewSummaryCarbohydrates,
                    formatNumberWithDecimalPlaces(allMealsOfTheDay.getTotalCarbohydratesOfAllMeals())));
            textViewSummaryProtein.setText(getResources().getString(R.string.textViewSummaryProtein,
                    formatNumberWithDecimalPlaces(allMealsOfTheDay.getTotalProteinOfAllMeals())));
            textViewSummaryFat.setText(getResources().getString(R.string.textViewSummaryFat,
                    formatNumberWithDecimalPlaces(allMealsOfTheDay.getTotalFatOfAllMeals())));

            setData(piecesOfChart, new float[]{allMealsOfTheDay.getPercentCarbohydrates(), allMealsOfTheDay.getPercentProtein(),
                    allMealsOfTheDay.getPercentFat()});
        }
    }

    private void calculateMacronutrientsOfAllMeals () {

        allMealsOfTheDay.calculateTotalCarbohydratesOfAllMeals(breakfast.getTotalCarbohydratesOfMeal(), lunch.getTotalCarbohydratesOfMeal(),
                dinner.getTotalCarbohydratesOfMeal(), snacks.getTotalCarbohydratesOfMeal());
        allMealsOfTheDay.calculateTotalProteinOfAllMeals(breakfast.getTotalProteinOfMeal(), lunch.getTotalProteinOfMeal(),
                dinner.getTotalProteinOfMeal(), snacks.getTotalProteinOfMeal());
        allMealsOfTheDay.calculateTotalFatOfAllMeal(breakfast.getTotalFatOfMeal(), lunch.getTotalFatOfMeal(),
                dinner.getTotalFatOfMeal(), snacks.getTotalFatOfMeal());
    }

    private void calculatePercentMacronutrientsOfAllMeals() {

        allMealsOfTheDay.calculatePercentOfCarbohydratesFromAllMacros();
        allMealsOfTheDay.calculatePercentOfProteinFromAllMacros();
        allMealsOfTheDay.calculatePercentOfFatFromAllMacros();
    }

    private void summaryCalories() {

        mifflinStJeor = new MifflinStJeor(user.getWeight(), user.getHeight(),
                user.getAge(), user.getGender(), user.getLevelOfActivity());
        mifflinStJeor.calculateGDA();

        calculateCaloriesOfAllMeals();
        showCaloriesSummary();
        buildDiagram();
    }

    private void calculateCaloriesOfAllMeals() {

        allMealsOfTheDay.calculateTotalCaloriesOfAllMeal(breakfast.getTotalCaloriesOfMeal(), lunch.getTotalCaloriesOfMeal(), dinner.getTotalCaloriesOfMeal(),
                snacks.getTotalCaloriesOfMeal());
        allMealsOfTheDay.calculateCaloriesToBeConsumed(mifflinStJeor.getGda(), allMealsOfTheDay.getTotalCaloriesOfAllMeals());

        allMealsOfTheDay.calculatePercentGDA(mifflinStJeor.getGda());

    }

    private void showCaloriesSummary() {

        textViewCaloriesToBeConsumedAmount.setText(formatNumberWithoutDecimalPlaces(allMealsOfTheDay.getCaloriesToBeConsumed()));
        textViewCaloriesConsumedAmount.setText(formatNumberWithoutDecimalPlaces(allMealsOfTheDay.getTotalCaloriesOfAllMeals()));
        textViewPercentFromGDA.setText(getResources().getString(R.string.textViewPercentFromGDA, formatNumberWithoutDecimalPlaces(allMealsOfTheDay.getPercentGDA())));
        textViewTotalCaloriesGDA.setText(formatNumberWithoutDecimalPlaces(mifflinStJeor.getGda()));
    }

    public interface EnableBottomBar {

        void enable(boolean b);
    }

    public EnableBottomBar getEnableBottomBar() {
        return enableBottomBar;
    }

    public interface CollapseCalendar {

        void collapse();
    }

    public CollapseCalendar getCollapseCalendar() {
        return collapseCalendar;
    }
}