package com.example.diary;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.ViewCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Bundle;
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

public class ActivityKatchMcArdle extends AppCompatActivity implements View.OnClickListener {

    private TextView textViewDatePickerAppBarKatchMcArdle, textViewAveragePerDayValueKatchMcArdle, textViewSumValueKatchMcArdle;

    private Button buttonJan, buttonFeb, buttonMar, buttonApr, buttonMay, buttonJun, buttonJuly, buttonAug, buttonSep, buttonOct, buttonNov, buttonDec;

    private ImageView imageViewBackKatchMcArdle, imageViewArrowAppBarKatchMcArdle;

    private RecyclerView recyclerViewKatchMcArdle;
    private RecyclerView.LayoutManager layoutManager;
    private KatchMcArdleAdapter katchMcArdleAdapter;

    private AppBarLayout appBarLayoutKatchMcArdle;
    private RelativeLayout relativeLayoutKatchMcArdle;
    private NestedScrollView nestedScrollViewKatchMcArdle;

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
    private Map<Integer, Float> allMuscleMass = new HashMap<>();

    private int numberOfDays = 0;

    private boolean allConsumedCaloriesReceived, allMuscleMassReceived;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_katch_mc_ardle);

        textViewDatePickerAppBarKatchMcArdle = findViewById(R.id.textViewDatePickerAppBarKatchMcArdle);
        textViewAveragePerDayValueKatchMcArdle = findViewById(R.id.textViewAveragePerDayValueKatchMcArdle);
        textViewSumValueKatchMcArdle = findViewById(R.id.textViewSumValueKatchMcArdle);

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

        imageViewBackKatchMcArdle = findViewById(R.id.imageViewBackKatchMcArdle);
        imageViewArrowAppBarKatchMcArdle= findViewById(R.id.imageViewArrowAppBarKatchMcArdle);

        appBarLayoutKatchMcArdle = findViewById(R.id.appBarLayoutKatchMcArdle);
        relativeLayoutKatchMcArdle= findViewById(R.id.relativeLayoutKatchMcArdle);
        nestedScrollViewKatchMcArdle = findViewById(R.id.nestedScrollViewKatchMcArdle);
        ViewCompat.setNestedScrollingEnabled(nestedScrollViewKatchMcArdle, false);

        recyclerViewKatchMcArdle = findViewById(R.id.recyclerViewKatchMcArdle);

        relativeLayoutKatchMcArdle.setOnClickListener(this);
        imageViewBackKatchMcArdle.setOnClickListener(this);
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

        katchMcArdleAdapter = new KatchMcArdleAdapter(allConsumedCalories, this, R.layout.mifflin_st_jeor_item, namesOfDaysOfTheMonth, allMuscleMass);

        recyclerViewKatchMcArdle.setLayoutManager(layoutManager);
        recyclerViewKatchMcArdle.setAdapter(katchMcArdleAdapter);

        textViewDatePickerAppBarKatchMcArdle.setText(String.valueOf(months[month] + " " + year));

        getObservable()
                .subscribe(getObserver());

        nestedScrollViewKatchMcArdle.setOnScrollChangeListener(new View.OnScrollChangeListener() {
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

            case R.id.relativeLayoutKatchMcArdle:
                collapseAppBarLayout();
                break;

            case R.id.imageViewBackKatchMcArdle:
                onBackPressed();
                break;

            case R.id.buttonJan:

                month = 0;

                textViewDatePickerAppBarKatchMcArdle.setText(String.valueOf(months[month] + " " + year));

                receiveNumberOfDays();
                receiveNamesOfDaysOfTheMonth();
                getObservable()
                        .subscribe(getObserver());

                break;

            case R.id.buttonFeb:

                month = 1;

                textViewDatePickerAppBarKatchMcArdle.setText(String.valueOf(months[month] + " " + year));

                receiveNumberOfDays();
                receiveNamesOfDaysOfTheMonth();
                getObservable()
                        .subscribe(getObserver());

                break;

            case R.id.buttonMar:

                month = 2;

                textViewDatePickerAppBarKatchMcArdle.setText(String.valueOf(months[month] + " " + year));

                receiveNumberOfDays();
                receiveNamesOfDaysOfTheMonth();
                getObservable()
                        .subscribe(getObserver());

                break;

            case R.id.buttonApr:

                month = 3;

                textViewDatePickerAppBarKatchMcArdle.setText(String.valueOf(months[month] + " " + year));

                receiveNumberOfDays();
                receiveNamesOfDaysOfTheMonth();
                getObservable()
                        .subscribe(getObserver());

                break;

            case R.id.buttonMay:

                month = 4;

                textViewDatePickerAppBarKatchMcArdle.setText(String.valueOf(months[month] + " " + year));

                receiveNumberOfDays();
                receiveNamesOfDaysOfTheMonth();
                getObservable()
                        .subscribe(getObserver());

                break;

            case R.id.buttonJun:

                month = 5;

                textViewDatePickerAppBarKatchMcArdle.setText(String.valueOf(months[month] + " " + year));

                receiveNumberOfDays();
                receiveNamesOfDaysOfTheMonth();
                getObservable()
                        .subscribe(getObserver());

                break;

            case R.id.buttonJuly:

                month = 6;

                textViewDatePickerAppBarKatchMcArdle.setText(String.valueOf(months[month] + " " + year));

                receiveNumberOfDays();
                receiveNamesOfDaysOfTheMonth();
                getObservable()
                        .subscribe(getObserver());

                break;

            case R.id.buttonAug:

                month = 7;

                textViewDatePickerAppBarKatchMcArdle.setText(String.valueOf(months[month] + " " + year));

                receiveNumberOfDays();
                receiveNamesOfDaysOfTheMonth();
                getObservable()
                        .subscribe(getObserver());

                break;

            case R.id.buttonSep:

                month = 8;

                textViewDatePickerAppBarKatchMcArdle.setText(String.valueOf(months[month] + " " + year));

                receiveNumberOfDays();
                receiveNamesOfDaysOfTheMonth();
                getObservable()
                        .subscribe(getObserver());

                break;

            case R.id.buttonOct:

                month = 9;

                textViewDatePickerAppBarKatchMcArdle.setText(String.valueOf(months[month] + " " + year));

                receiveNumberOfDays();
                receiveNamesOfDaysOfTheMonth();
                getObservable()
                        .subscribe(getObserver());

                break;

            case R.id.buttonNov:

                month = 10;

                textViewDatePickerAppBarKatchMcArdle.setText(String.valueOf(months[month] + " " + year));

                receiveNumberOfDays();
                receiveNamesOfDaysOfTheMonth();
                getObservable()
                        .subscribe(getObserver());

                break;

            case R.id.buttonDec:

                month = 11;

                textViewDatePickerAppBarKatchMcArdle.setText(String.valueOf(months[month] + " " + year));

                receiveNumberOfDays();
                receiveNamesOfDaysOfTheMonth();
                getObservable()
                        .subscribe(getObserver());
                break;
        }
    }

    private void collapseAppBarLayout() {

        float rotation = isExpaned ? 0 : 180;

        ViewCompat.animate(imageViewArrowAppBarKatchMcArdle).rotation(rotation);

        isExpaned = !isExpaned;

        appBarLayoutKatchMcArdle.setExpanded(isExpaned, true);

    }

    private void lockDrag() {

        if (appBarLayoutKatchMcArdle.getLayoutParams() != null) {

            CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) appBarLayoutKatchMcArdle.getLayoutParams();
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
                getAllMuscleMass(emitter);
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

                katchMcArdleAdapter.notifyDataSetChanged();
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

                        textViewSumValueKatchMcArdle.setText(FormatNumbers.formatNumberWithoutDecimalPlaces(calculateSumCalories()) + " calories");
                        textViewAveragePerDayValueKatchMcArdle.setText(FormatNumbers.formatNumberWithoutDecimalPlaces(calculateAverageValuePerDay()) + " calories");

                    } catch(NumberFormatException exc) {

                        textViewSumValueKatchMcArdle.setText("0 calories");
                        textViewAveragePerDayValueKatchMcArdle.setText("0 calories");
                    }

                    callComplete(emitter);
                }
            }
        });
    }

    private void getAllMuscleMass(final ObservableEmitter emitter) {

        firebaseFirestore.collection("Users").document(user.getCurrentUserUID())
                .collection("Information of the day").document("Muscle Mass")
                .collection("Years").document(String.valueOf(year))
                .collection("Months").document(months[month])
                .collection("Days").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if(task.isSuccessful()) {

                    allMuscleMassReceived = true;

                    allMuscleMass.clear();

                    for(DocumentSnapshot snapshot : task.getResult().getDocuments()) {

                        allMuscleMass.put(Integer.parseInt(snapshot.getId()), Float.parseFloat(String.valueOf(snapshot.get("Muscle Mass On A Given Day"))));
                    }

                    callComplete(emitter);
                }
            }
        });
    }

    //Change
    private void callComplete(ObservableEmitter emitter) {

        if((allConsumedCaloriesReceived == true) && (allMuscleMassReceived == true)) {

            emitter.onComplete();

            allConsumedCaloriesReceived = false;
            allMuscleMassReceived = false;
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

            ViewCompat.animate(imageViewArrowAppBarKatchMcArdle).rotation(0).start();
            appBarLayoutKatchMcArdle.setExpanded(false, true);

            isExpaned = false;
        }
    }
}
