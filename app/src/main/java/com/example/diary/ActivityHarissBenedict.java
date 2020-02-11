package com.example.diary;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.ViewCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.example.format_numbers.FormatNumbers;
import com.example.healthylife.R;
import com.example.user.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.AppBarLayout;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class ActivityHarissBenedict extends AppCompatActivity implements View.OnClickListener {

    private TextView textViewDatePickerAppBarHarissBenedict, textViewAveragePerDayValueHarissBenedict, textViewSumValueHarissBenedict;

    private Button buttonJan, buttonFeb, buttonMar, buttonApr, buttonMay, buttonJun, buttonJuly, buttonAug, buttonSep, buttonOct, buttonNov, buttonDec;

    private ImageView imageViewBackHarissBenedict, imageViewArrowAppBarHarissBenedict;

    private RecyclerView recyclerViewHarissBenedict;
    private RecyclerView.LayoutManager layoutManager;
    private HarissBenedictAdapter harissBenedictAdapter;

    private AppBarLayout appBarLayoutHarissBenedict;
    private RelativeLayout relativeLayoutHarissBenedict;
    private NestedScrollView nestedScrollViewHarissBenedict;

    private Calendar calendar;

    private FirebaseFirestore firebaseFirestore;
    private User user;

    private int month;
    private int year;

    private boolean isExpaned = false;

    private final String months[] = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
    private final String nameOfDays[] = {"", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};

    private Map<Integer, Float> allConsumedCalories = new HashMap<>();
    private ArrayList<String> namesOfDaysOfTheMonth = new ArrayList<>();
    private Map<Integer, Float> allWeight = new HashMap<>();

    private int numberOfDays = 0;

    private boolean allConsumedCaloriesReceived, allWeightReceived;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hariss_benedict);

        textViewDatePickerAppBarHarissBenedict = findViewById(R.id.textViewDatePickerAppBarHarissBenedict);
        textViewAveragePerDayValueHarissBenedict = findViewById(R.id.textViewAveragePerDayValueHarissBenedict);
        textViewSumValueHarissBenedict= findViewById(R.id.textViewSumValueHarissBenedict);

        buttonJan = findViewById(R.id.buttonJan);
        buttonFeb = findViewById(R.id.buttonFeb);
        buttonMar = findViewById(R.id.buttonMar);
        buttonApr = findViewById(R.id.buttonApr);
        buttonMay = findViewById(R.id.buttonMay);
        buttonJun = findViewById(R.id.buttonJun);
        buttonJuly = findViewById(R.id.buttonJuly);
        buttonAug = findViewById(R.id.buttonAug);
        buttonSep = findViewById(R.id.buttonSep);
        buttonOct = findViewById(R.id.buttonOct);
        buttonNov = findViewById(R.id.buttonNov);
        buttonDec = findViewById(R.id.buttonDec);

        imageViewBackHarissBenedict= findViewById(R.id.imageViewBackHarissBenedict);
        imageViewArrowAppBarHarissBenedict = findViewById(R.id.imageViewArrowAppBarHarissBenedict);

        appBarLayoutHarissBenedict= findViewById(R.id.appBarLayoutHarissBenedict);
        relativeLayoutHarissBenedict = findViewById(R.id.relativeLayoutHarissBenedict);
        nestedScrollViewHarissBenedict = findViewById(R.id.nestedScrollViewHarissBenedict);
        ViewCompat.setNestedScrollingEnabled(nestedScrollViewHarissBenedict, false);

        recyclerViewHarissBenedict = findViewById(R.id.recyclerViewHarissBenedict);

        relativeLayoutHarissBenedict.setOnClickListener(this);
        imageViewBackHarissBenedict.setOnClickListener(this);
        buttonJan.setOnClickListener(this);
        buttonFeb.setOnClickListener(this);
        buttonMar.setOnClickListener(this);
        buttonApr.setOnClickListener(this);
        buttonMay.setOnClickListener(this);
        buttonJun.setOnClickListener(this);
        buttonJuly.setOnClickListener(this);
        buttonAug.setOnClickListener(this);
        buttonSep.setOnClickListener(this);
        buttonOct.setOnClickListener(this);
        buttonNov.setOnClickListener(this);
        buttonDec.setOnClickListener(this);

        lockDrag();

        firebaseFirestore = FirebaseFirestore.getInstance();
        user = User.getInstance();

        calendar = Calendar.getInstance();

        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);

        layoutManager = new LinearLayoutManager(this);

        receiveNumberOfDays();
        receiveNamesOfDaysOfTheMonth();

        harissBenedictAdapter = new HarissBenedictAdapter(allConsumedCalories, this, R.layout.mifflin_st_jeor_item, namesOfDaysOfTheMonth, allWeight);

        recyclerViewHarissBenedict.setLayoutManager(layoutManager);
        recyclerViewHarissBenedict.setAdapter(harissBenedictAdapter);

        textViewDatePickerAppBarHarissBenedict.setText(String.valueOf(months[month] + " " + year));

        getObservable()
                .subscribe(getObserver());

        nestedScrollViewHarissBenedict.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {

                if(isExpaned) {

                    collapseMonths();
                }
            }
        });
    }

    @Override
    public void onClick(View view) {


        int id = view.getId();

        switch(id) {

            case R.id.relativeLayoutHarissBenedict:
                collapseAppBarLayout();
                break;

            case R.id.imageViewBackHarissBenedict:
                onBackPressed();
                break;

            case R.id.buttonJan:

                month = 0;

                textViewDatePickerAppBarHarissBenedict.setText(String.valueOf(months[month] + " " + year));

                receiveNumberOfDays();
                receiveNamesOfDaysOfTheMonth();
                getObservable()
                        .subscribe(getObserver());

                break;

            case R.id.buttonFeb:

                month = 1;

                textViewDatePickerAppBarHarissBenedict.setText(String.valueOf(months[month] + " " + year));

                receiveNumberOfDays();
                receiveNamesOfDaysOfTheMonth();
                getObservable()
                        .subscribe(getObserver());

                break;

            case R.id.buttonMar:

                month = 2;

                textViewDatePickerAppBarHarissBenedict.setText(String.valueOf(months[month] + " " + year));

                receiveNumberOfDays();
                receiveNamesOfDaysOfTheMonth();
                getObservable()
                        .subscribe(getObserver());

                break;

            case R.id.buttonApr:

                month = 3;

                textViewDatePickerAppBarHarissBenedict.setText(String.valueOf(months[month] + " " + year));

                receiveNumberOfDays();
                receiveNamesOfDaysOfTheMonth();
                getObservable()
                        .subscribe(getObserver());

                break;

            case R.id.buttonMay:

                month = 4;

                textViewDatePickerAppBarHarissBenedict.setText(String.valueOf(months[month] + " " + year));

                receiveNumberOfDays();
                receiveNamesOfDaysOfTheMonth();
                getObservable()
                        .subscribe(getObserver());

                break;

            case R.id.buttonJun:

                month = 5;

                textViewDatePickerAppBarHarissBenedict.setText(String.valueOf(months[month] + " " + year));

                receiveNumberOfDays();
                receiveNamesOfDaysOfTheMonth();
                getObservable()
                        .subscribe(getObserver());

                break;

            case R.id.buttonJuly:

                month = 6;

                textViewDatePickerAppBarHarissBenedict.setText(String.valueOf(months[month] + " " + year));

                receiveNumberOfDays();
                receiveNamesOfDaysOfTheMonth();
                getObservable()
                        .subscribe(getObserver());

                break;

            case R.id.buttonAug:

                month = 7;

                textViewDatePickerAppBarHarissBenedict.setText(String.valueOf(months[month] + " " + year));

                receiveNumberOfDays();
                receiveNamesOfDaysOfTheMonth();
                getObservable()
                        .subscribe(getObserver());

                break;

            case R.id.buttonSep:

                month = 8;

                textViewDatePickerAppBarHarissBenedict.setText(String.valueOf(months[month] + " " + year));

                receiveNumberOfDays();
                receiveNamesOfDaysOfTheMonth();
                getObservable()
                        .subscribe(getObserver());

                break;

            case R.id.buttonOct:

                month = 9;

                textViewDatePickerAppBarHarissBenedict.setText(String.valueOf(months[month] + " " + year));

                receiveNumberOfDays();
                receiveNamesOfDaysOfTheMonth();
                getObservable()
                        .subscribe(getObserver());

                break;

            case R.id.buttonNov:

                month = 10;

                textViewDatePickerAppBarHarissBenedict.setText(String.valueOf(months[month] + " " + year));

                receiveNumberOfDays();
                receiveNamesOfDaysOfTheMonth();
                getObservable()
                        .subscribe(getObserver());

                break;

            case R.id.buttonDec:

                month = 11;

                textViewDatePickerAppBarHarissBenedict.setText(String.valueOf(months[month] + " " + year));

                receiveNumberOfDays();
                receiveNamesOfDaysOfTheMonth();
                getObservable()
                        .subscribe(getObserver());
                break;

        }
    }

    private void collapseAppBarLayout() {

        float rotation = isExpaned ? 0 : 180;

        ViewCompat.animate(imageViewArrowAppBarHarissBenedict).rotation(rotation);

        isExpaned = !isExpaned;

        appBarLayoutHarissBenedict.setExpanded(isExpaned, true);

    }

    private void lockDrag() {

        if (appBarLayoutHarissBenedict.getLayoutParams() != null) {

            CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) appBarLayoutHarissBenedict.getLayoutParams();
            AppBarLayout.Behavior appBarLayoutBehaviour = new AppBarLayout.Behavior();

            appBarLayoutBehaviour.setDragCallback(new AppBarLayout.Behavior.DragCallback() {
                @Override
                public boolean canDrag(@NonNull AppBarLayout appBarLayout) {
                    return false;
                }
            });

            layoutParams.setBehavior(appBarLayoutBehaviour);
        }
    }


    private void receiveNumberOfDays() {

        calendar.set(year, month, 1);
        numberOfDays = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

    }

    private void receiveNamesOfDaysOfTheMonth() {

        namesOfDaysOfTheMonth.clear();

        for(int i = 0; i < numberOfDays; i++) {

            calendar.set(year, month, i);
            namesOfDaysOfTheMonth.add(nameOfDays[calendar.get(Calendar.DAY_OF_WEEK)]);
        }
    }

    private Observable<String> getObservable() {

        return Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> emitter) throws Exception {

                getAllConsumedCalories(emitter);
                getAllWeight(emitter);
            }
        });
    }

    private Observer<String> getObserver() {

        return new Observer<String>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(String s) {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

                harissBenedictAdapter.notifyDataSetChanged();
            }
        };
    }

    public void getAllConsumedCalories(final ObservableEmitter emitter) {

        firebaseFirestore.collection("Users").document(user.getCurrentUserUID())
                .collection("Information of the day").document("Calories consumed")
                .collection("Years").document(String.valueOf(year))
                .collection("Months").document(months[month])
                .collection("Days").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if(task.isSuccessful()) {

                    allConsumedCaloriesReceived = true;

                    allConsumedCalories.clear();

                    for(DocumentSnapshot documentSnapshot : task.getResult().getDocuments()) {

                        try {

                            allConsumedCalories.put(Integer.parseInt(documentSnapshot.getId()), Float.parseFloat(String.valueOf(documentSnapshot.get("Calories"))));

                        } catch (NumberFormatException exc) {

                        }
                    }

                    try {

                        textViewSumValueHarissBenedict.setText(FormatNumbers.formatNumberWithoutDecimalPlaces(calculateSumCalories()) + " calories");
                        textViewAveragePerDayValueHarissBenedict.setText(FormatNumbers.formatNumberWithoutDecimalPlaces(calculateAverageValuePerDay()) + " calories");

                    } catch(NumberFormatException exc) {

                        textViewSumValueHarissBenedict.setText("0 calories");
                        textViewAveragePerDayValueHarissBenedict.setText("0 calories");
                    }

                    callComplete(emitter);
                }
            }
        });
    }

    private void getAllWeight(final ObservableEmitter emitter) {

        firebaseFirestore.collection("Users").document(user.getCurrentUserUID())
                .collection("Information of the day").document("Weight")
                .collection("Years").document(String.valueOf(year))
                .collection("Months").document(months[month])
                .collection("Days").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if(task.isSuccessful()) {

                    allWeightReceived = true;

                    allWeight.clear();

                    for(DocumentSnapshot snapshot : task.getResult().getDocuments()) {

                        allWeight.put(Integer.parseInt(snapshot.getId()), Float.parseFloat(String.valueOf(snapshot.get("Weight On A Given Day"))));
                    }

                    callComplete(emitter);
                }
            }
        });
    }

    private void callComplete(ObservableEmitter emitter) {

        if((allConsumedCaloriesReceived == true) && (allWeightReceived == true)) {

            emitter.onComplete();

            allConsumedCaloriesReceived = false;
            allWeightReceived = false;

            for(Integer key : allWeight.keySet()) {

                Log.i("Value: ", String.valueOf(allWeight.get(key)));
            }
        }
    }

    private float calculateSumCalories() {

        float sumCalories = 0;

        for(Integer key : allConsumedCalories.keySet()) {

            sumCalories += allConsumedCalories.get(key);
        }

        return sumCalories;
    }

    private float calculateAverageValuePerDay() {

        float averageValuePerDay = 0;

        averageValuePerDay = calculateSumCalories() / numberOfDays;

        return averageValuePerDay;
    }

    private void collapseMonths() {

        if(isExpaned) {

            ViewCompat.animate(imageViewArrowAppBarHarissBenedict).rotation(0).start();
            appBarLayoutHarissBenedict.setExpanded(false, true);

            isExpaned = false;
        }
    }
}
