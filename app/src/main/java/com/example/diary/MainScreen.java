package com.example.diary;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.example.expandable_recyclerview.Meal;
import com.example.expandable_recyclerview.Product;
import com.example.healthylife.R;
import com.example.healthylife.StartNowActivity;
import com.example.user.User;
import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.TimeZone;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;



public class MainScreen extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener, DietDiaryFragment.EnableBottomBar,
        DietDiaryFragment.CollapseCalendar {

    private AppBarLayout appBarLayout;

    private CompactCalendarView compactCalendarView;

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    private Toolbar toolbar;

    private TextView textViewDatePickerAppBarMainScreen, textViewToolbarTittle, textViewEmailNavHeader;

    private ImageView imageViewArrowAppBarMainScreen;

    private RelativeLayout relativeLayoutDatePickerButton;

    private BottomNavigationView bottomNavigation;

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("d MMMM yyyy", Locale.ENGLISH);
    private boolean isExpaned = false;
    private FirebaseAuth mAuth;
    private String date = "";

    private FirebaseFirestore firebaseFirestore;
    private User user = User.getInstance();

    private FragmentRefreshDietDiary refreshDietDiaryFragment;

    private RefreshDate refreshDate;

    private Meal breakfast = new Meal();
    private Meal lunch = new Meal();
    private Meal dinner = new Meal();
    private Meal snacks = new Meal();

    private ArrayList<String> datesWeightController = new ArrayList<>();
    private ArrayList<Float> allWeights = new ArrayList<>();

    private ArrayList<String> datesMuscleMassController = new ArrayList<>();
    private ArrayList<Float> allMuscleMass = new ArrayList<>();

    private boolean productsReceivedBreakfast, productsReceivedLunch, productsReceivedDinner, productsReceivedSnacks, userInformationReceived,
                    weightOperationsDone, muscleMassOperationsDone;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);

        setInitialFragment();

        navigationView = findViewById(R.id.navigationView);
        View headerViewEmail = navigationView.getHeaderView(0);
        textViewEmailNavHeader = headerViewEmail.findViewById(R.id.textViewEmailNavHeader);

        appBarLayout = findViewById(R.id.appBarLayout);
        toolbar = findViewById(R.id.toolbar);
        compactCalendarView = findViewById(R.id.compactCalendarView);
        textViewDatePickerAppBarMainScreen = findViewById(R.id.textViewDatePickerAppBarMainScreen);
        textViewToolbarTittle = findViewById(R.id.textViewToolbarTittle);
        imageViewArrowAppBarMainScreen = findViewById(R.id.imageViewArrowAppBarMainScreen);
        relativeLayoutDatePickerButton = findViewById(R.id.relativeLayoutDatePickerButton);
        bottomNavigation = findViewById(R.id.bottomNavigation);
        drawerLayout = findViewById(R.id.drawerLayout);

        navigationView.setNavigationItemSelectedListener(this);
        relativeLayoutDatePickerButton.setOnClickListener(this);
        bottomNavigation.setOnNavigationItemSelectedListener(bottomNavigationListener);

        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        user.setCurrentUserUID(FirebaseAuth.getInstance().getCurrentUser().getUid());

        if(getIntent().getStringExtra("date") != null) {

            date = getIntent().getStringExtra("date");
            showDate();

        } else {

            setCurrentDate(new Date());
        }

        if((user.getWeight() == 0) || (allWeights.size() == 0)) {

            enableBottomBar(false);
        }

        getObservable()
                .subscribe(getObserver());

        lockDrag();
        setSupportActionBar(toolbar);

        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        setEmail();

        compactCalendarView.setLocale(TimeZone.getDefault(), Locale.ENGLISH);
        compactCalendarView.setShouldDrawDaysHeader(true);
        compactCalendarView.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @Override
            public void onDayClick(Date dateClicked) {

                setCurrentDate(dateClicked);

                if(getRefreshDate() != null) {

                    refreshDate.refreshDate(dateFormat.format(dateClicked));
                }

                getObservable()
                        .subscribe(getObserver());

                float rotation = isExpaned ? 0 : 180;

                ViewCompat.animate(imageViewArrowAppBarMainScreen).rotation(rotation).start();

                isExpaned = !isExpaned;
                appBarLayout.setExpanded(isExpaned, true);

            }

            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {

                setCurrentDate(firstDayOfNewMonth);

                getObservable()
                        .subscribe(getObserver());
            }
        });
    }

    private void enableBottomBar(boolean enable){
        for (int i = 0; i < bottomNavigation.getMenu().size(); i++) {
            bottomNavigation.getMenu().getItem(i).setEnabled(enable);
        }
    }

    private void getProductsFromDatabaseBreakfast(final ObservableEmitter<String> emitter) {

        breakfast.getTotalProductsOfMeal().clear();
        breakfast.getIdProductsOfMeal().clear();

        firebaseFirestore.collection("Users").document(user.getCurrentUserUID())
                .collection("Types of Meals").document("Breakfast")
                .collection("Date of Breakfast").document(date)
                .collection("List of Products")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if (task.isSuccessful()) {

                    productsReceivedBreakfast = true;

                    for (DocumentSnapshot documentSnapshot : task.getResult().getDocuments()) {

                        breakfast.getIdProductsOfMeal().add(documentSnapshot.getId());
                        breakfast.getTotalProductsOfMeal().add(documentSnapshot.toObject(Product.class));

                    }

                    callComplete(emitter);
                }
            }
        });
    }

    private void getProductsFromDatabaseLunch(final ObservableEmitter<String> emitter) {

        lunch.getTotalProductsOfMeal().clear();
        lunch.getIdProductsOfMeal().clear();

        firebaseFirestore.collection("Users").document(user.getCurrentUserUID())
                .collection("Types of Meals").document("Lunch")
                .collection("Date of Lunch").document(date)
                .collection("List of Products")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if (task.isSuccessful()) {

                    productsReceivedLunch = true;

                    for (DocumentSnapshot documentSnapshot : task.getResult().getDocuments()) {

                        lunch.getIdProductsOfMeal().add(documentSnapshot.getId());
                        lunch.getTotalProductsOfMeal().add(documentSnapshot.toObject(Product.class));
                    }

                    callComplete(emitter);
                }
            }
        });
    }

    private void getProductsFromDatabaseDinner(final ObservableEmitter<String> emitter) {

        dinner.getTotalProductsOfMeal().clear();
        dinner.getIdProductsOfMeal().clear();

        firebaseFirestore.collection("Users").document(user.getCurrentUserUID())
                .collection("Types of Meals").document("Dinner")
                .collection("Date of Dinner").document(date)
                .collection("List of Products")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if (task.isSuccessful()) {

                    productsReceivedDinner = true;

                    for (DocumentSnapshot documentSnapshot : task.getResult().getDocuments()) {

                        dinner.getIdProductsOfMeal().add(documentSnapshot.getId());
                        dinner.getTotalProductsOfMeal().add(documentSnapshot.toObject(Product.class));
                    }

                    callComplete(emitter);
                }
            }
        });
    }

    private void getProductsFromDatabaseSnacks(final ObservableEmitter<String> emitter) {

        snacks.getTotalProductsOfMeal().clear();
        snacks.getIdProductsOfMeal().clear();

        firebaseFirestore.collection("Users").document(user.getCurrentUserUID())
                .collection("Types of Meals").document("Snacks")
                .collection("Date of Snacks").document(date)
                .collection("List of Products")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if (task.isSuccessful()) {

                    productsReceivedSnacks = true;

                    for (DocumentSnapshot documentSnapshot : task.getResult().getDocuments()) {

                        snacks.getIdProductsOfMeal().add(documentSnapshot.getId());
                        snacks.getTotalProductsOfMeal().add(documentSnapshot.toObject(Product.class));
                    }

                    callComplete(emitter);
                }

            }
        });
    }

    private void getUserInformation(final ObservableEmitter<String> emitter) {

        firebaseFirestore.collection("Users").document(user.getCurrentUserUID()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if(task.isSuccessful()) {

                    userInformationReceived = true;

                    DocumentSnapshot documentSnapshot = task.getResult();

                    user.setGender(documentSnapshot.get("gender").toString());
                    user.setDateOfBirth(documentSnapshot.get("dateOfBirth").toString());
                    user.setLevelOfActivity(documentSnapshot.get("levelOfActivity").toString());
                    user.setAge(Integer.parseInt(documentSnapshot.get("age").toString()));
                    user.setHeight(Float.parseFloat(documentSnapshot.get("height").toString()));

                    user.setTargetWeight(Float.parseFloat(documentSnapshot.get("targetWeight").toString()));
                    user.setTargetMuscleMass(Float.parseFloat(documentSnapshot.get("targetMuscleMass").toString()));

                    callComplete(emitter);
                }
            }
        });
    }

    private void weightOperations(final ObservableEmitter<String> emitter) {

        firebaseFirestore.collection("Users").document(user.getCurrentUserUID()).collection("Weight Controller Date").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        ArrayList<DocumentSnapshot> documentSnapshots = new ArrayList<>();

                        allWeights.clear();
                        datesWeightController.clear();

                        if (task.isSuccessful()) {

                            for(DocumentSnapshot documentSnapshot1 : task.getResult().getDocuments()) {

                                documentSnapshots.add(documentSnapshot1);
                            }

                            Collections.sort(documentSnapshots, new Comparator<DocumentSnapshot>() {
                                @Override
                                public int compare(DocumentSnapshot o1, DocumentSnapshot o2) {

                                    try {

                                        return dateFormat.parse(o1.getId()).compareTo(dateFormat.parse(o2.getId()));

                                    } catch (ParseException e) {

                                        e.printStackTrace();
                                    }

                                    return 0;
                                }
                            });

                            for (DocumentSnapshot documentSnapshot1 : documentSnapshots) {

                                datesWeightController.add(documentSnapshot1.getId());
                                allWeights.add(new Float(String.valueOf(documentSnapshot1.get("Current Weight"))));
                            }

                            firebaseFirestore.collection("Users").document(user.getCurrentUserUID())
                                    .collection("Information of the day").document("Weight")
                                    .collection("Years").document(date.split(" ")[2])
                                    .collection("Months").document(date.split(" ")[1])
                                    .collection("Days").document(date.split(" ")[0]).get()
                                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                                            if (task.isSuccessful()) {

                                                weightOperationsDone = true;

                                                ArrayList<String> allDates = new ArrayList<>();
                                                allDates.addAll(datesWeightController);

                                                if(!allDates.contains(date)) {

                                                    allDates.add(date);
                                                }

                                                Collections.sort(allDates, new Comparator<String>() {
                                                    @Override
                                                    public int compare(String o1, String o2) {

                                                        try {

                                                            return dateFormat.parse(o1).compareTo(dateFormat.parse(o2));

                                                        } catch (ParseException e) {

                                                            e.printStackTrace();
                                                        }

                                                        return 0;
                                                    }
                                                });

                                                boolean isExist = task.getResult().exists();

                                                if(isExist) {

                                                    if(datesWeightController.contains(date)) {

                                                        user.setWeight(allWeights.get(datesWeightController.indexOf(date)));

                                                    } else {

                                                        if(allDates.indexOf(date) == 0) {

                                                            if(Float.parseFloat(String.valueOf(task.getResult().get("Weight On A Given Day"))) == allWeights.get(0)) {

                                                                user.setWeight(Float.parseFloat(String.valueOf(task.getResult().get("Weight On A Given Day"))));

                                                            } else {

                                                                user.setWeight(allWeights.get(0));

                                                                HashMap<String, Float> weightOnAGivenDay = new HashMap<>();
                                                                weightOnAGivenDay.put("Weight On A Given Day", allWeights.get(0));

                                                                firebaseFirestore.collection("Users").document(user.getCurrentUserUID())
                                                                        .collection("Information of the day").document("Weight")
                                                                        .collection("Years").document(date.split(" ")[2])
                                                                        .collection("Months").document(date.split(" ")[1])
                                                                        .collection("Days").document(date.split(" ")[0]).set(weightOnAGivenDay);
                                                            }

                                                        } else {

                                                            if(Float.parseFloat(String.valueOf(task.getResult().get("Weight On A Given Day"))) == allWeights.get(allDates.indexOf(date) - 1)) {

                                                                user.setWeight(Float.parseFloat(String.valueOf(task.getResult().get("Weight On A Given Day"))));

                                                            } else {

                                                                user.setWeight(allWeights.get(allDates.indexOf(date) - 1));

                                                                HashMap<String, Float> weightOnAGivenDay = new HashMap<>();
                                                                weightOnAGivenDay.put("Weight On A Given Day", allWeights.get(allDates.indexOf(date) - 1));

                                                                firebaseFirestore.collection("Users").document(user.getCurrentUserUID())
                                                                        .collection("Information of the day").document("Weight")
                                                                        .collection("Years").document(date.split(" ")[2])
                                                                        .collection("Months").document(date.split(" ")[1])
                                                                        .collection("Days").document(date.split(" ")[0]).set(weightOnAGivenDay);
                                                            }
                                                        }
                                                    }


                                                } else {

                                                    if(allDates.indexOf(date) == 0) {

                                                        user.setWeight(allWeights.get(0));

                                                    } else {

                                                        user.setWeight(allWeights.get(allDates.indexOf(date) - 1));
                                                    }

                                                    HashMap<String, Float> weightOnAGivenDay = new HashMap<>();
                                                    weightOnAGivenDay.put("Weight On A Given Day", user.getWeight());

                                                    firebaseFirestore.collection("Users").document(user.getCurrentUserUID())
                                                            .collection("Information of the day").document("Weight")
                                                            .collection("Years").document(date.split(" ")[2])
                                                            .collection("Months").document(date.split(" ")[1])
                                                            .collection("Days").document(date.split(" ")[0]).set(weightOnAGivenDay);

                                                }

                                                callComplete(emitter);
                                            }

                                        }
                                    });

                        }
                    }
                });
    }

    private void muscleMassOperations(final ObservableEmitter<String> emitter) {

        firebaseFirestore.collection("Users").document(user.getCurrentUserUID()).collection("Muscle Mass Controller Date").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        ArrayList<DocumentSnapshot> documentSnapshots = new ArrayList<>();

                        allMuscleMass.clear();
                        datesMuscleMassController.clear();

                        if (task.isSuccessful()) {

                            for(DocumentSnapshot documentSnapshot1 : task.getResult().getDocuments()) {

                                documentSnapshots.add(documentSnapshot1);
                            }

                            Collections.sort(documentSnapshots, new Comparator<DocumentSnapshot>() {
                                @Override
                                public int compare(DocumentSnapshot o1, DocumentSnapshot o2) {

                                    try {

                                        return dateFormat.parse(o1.getId()).compareTo(dateFormat.parse(o2.getId()));

                                    } catch (ParseException e) {

                                        e.printStackTrace();
                                    }

                                    return 0;
                                }
                            });

                            for (DocumentSnapshot documentSnapshot1 : documentSnapshots) {

                                datesMuscleMassController.add(documentSnapshot1.getId());
                                allMuscleMass.add(new Float(String.valueOf(documentSnapshot1.get("Current Muscle Mass"))));
                            }

                            firebaseFirestore.collection("Users").document(user.getCurrentUserUID())
                                    .collection("Information of the day").document("Muscle Mass")
                                    .collection("Years").document(date.split(" ")[2])
                                    .collection("Months").document(date.split(" ")[1])
                                    .collection("Days").document(date.split(" ")[0]).get()
                                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                                            if (task.isSuccessful()) {

                                                muscleMassOperationsDone = true;

                                                ArrayList<String> allDates = new ArrayList<>();
                                                allDates.addAll(datesMuscleMassController);

                                                if(!allDates.contains(date)) {

                                                    allDates.add(date);
                                                }

                                                Collections.sort(allDates, new Comparator<String>() {
                                                    @Override
                                                    public int compare(String o1, String o2) {

                                                        try {

                                                            return dateFormat.parse(o1).compareTo(dateFormat.parse(o2));

                                                        } catch (ParseException e) {

                                                            e.printStackTrace();
                                                        }

                                                        return 0;
                                                    }
                                                });

                                                boolean isExist = task.getResult().exists();

                                                if(isExist) {

                                                    if(datesMuscleMassController.contains(date)) {

                                                        user.setMuscleMass(allMuscleMass.get(datesMuscleMassController.indexOf(date)));

                                                    } else {

                                                        if(allDates.indexOf(date) == 0) {

                                                            if(Float.parseFloat(String.valueOf(task.getResult().get("Muscle Mass On A Given Day"))) == allMuscleMass.get(0)) {

                                                                user.setMuscleMass(Float.parseFloat(String.valueOf(task.getResult().get("Muscle Mass On A Given Day"))));

                                                            } else {

                                                                user.setMuscleMass(allMuscleMass.get(0));

                                                                HashMap<String, Float> muscleMassOnAGivenDay = new HashMap<>();
                                                                muscleMassOnAGivenDay.put("Muscle Mass On A Given Day", allMuscleMass.get(0));

                                                                firebaseFirestore.collection("Users").document(user.getCurrentUserUID())
                                                                        .collection("Information of the day").document("Muscle Mass")
                                                                        .collection("Years").document(date.split(" ")[2])
                                                                        .collection("Months").document(date.split(" ")[1])
                                                                        .collection("Days").document(date.split(" ")[0]).set(muscleMassOnAGivenDay);
                                                            }

                                                        } else {

                                                            if(Float.parseFloat(String.valueOf(task.getResult().get("Muscle Mass On A Given Day"))) == allMuscleMass.get(allDates.indexOf(date) - 1)) {

                                                                user.setMuscleMass(Float.parseFloat(String.valueOf(task.getResult().get("Muscle Mass On A Given Day"))));

                                                            } else {

                                                                user.setMuscleMass(allMuscleMass.get(allDates.indexOf(date) - 1));

                                                                HashMap<String, Float> muscleMassOnAGivenDay = new HashMap<>();
                                                                muscleMassOnAGivenDay.put("Muscle Mass On A Given Day", allMuscleMass.get(allDates.indexOf(date) - 1));

                                                                firebaseFirestore.collection("Users").document(user.getCurrentUserUID())
                                                                        .collection("Information of the day").document("Muscle Mass")
                                                                        .collection("Years").document(date.split(" ")[2])
                                                                        .collection("Months").document(date.split(" ")[1])
                                                                        .collection("Days").document(date.split(" ")[0]).set(muscleMassOnAGivenDay);
                                                            }
                                                        }
                                                    }


                                                } else {

                                                    if(allDates.indexOf(date) == 0) {

                                                        user.setMuscleMass(allMuscleMass.get(0));

                                                    } else {

                                                        user.setMuscleMass(allMuscleMass.get(allDates.indexOf(date) - 1));
                                                    }

                                                    HashMap<String, Float> muscleMassOnAGivenDay = new HashMap<>();
                                                    muscleMassOnAGivenDay.put("Muscle Mass On A Given Day", user.getMuscleMass());

                                                    firebaseFirestore.collection("Users").document(user.getCurrentUserUID())
                                                            .collection("Information of the day").document("Muscle Mass")
                                                            .collection("Years").document(date.split(" ")[2])
                                                            .collection("Months").document(date.split(" ")[1])
                                                            .collection("Days").document(date.split(" ")[0]).set(muscleMassOnAGivenDay);

                                                }

                                                callComplete(emitter);
                                            }

                                        }
                                    });

                        }
                    }
                });
    }

    private Observable<String> getObservable() {

        return Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(final ObservableEmitter<String> emitter) throws Exception {

                getUserInformation(emitter);
                weightOperations(emitter);
                muscleMassOperations(emitter);
                getProductsFromDatabaseBreakfast(emitter);
                getProductsFromDatabaseLunch(emitter);
                getProductsFromDatabaseDinner(emitter);
                getProductsFromDatabaseSnacks(emitter);
            }
        });
    }

    private Observer getObserver() {

        return new Observer() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(Object o) {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

                if(getFragmentRefreshDietDiary() != null) {

                    refreshDietDiaryFragment.refresh();
                }
            }
        };
    }

    private void callComplete(ObservableEmitter<String> emitter) {

        if((productsReceivedBreakfast == true) && (productsReceivedLunch == true)
                && (productsReceivedDinner == true) && (productsReceivedSnacks == true)
                && (userInformationReceived == true) && (weightOperationsDone == true)
                && (muscleMassOperationsDone == true)) {

            emitter.onComplete();

            productsReceivedBreakfast = false;
            productsReceivedLunch = false;
            productsReceivedDinner = false;
            productsReceivedSnacks = false;
            userInformationReceived = false;
            weightOperationsDone = false;
            muscleMassOperationsDone = false;
        }
    }

    public String getDate() {

        return date;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.my_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.logoutItem) {

            new AlertDialog.Builder(this)
                    .setTitle("Logout")
                    .setMessage("Are you sure you want to log out?")
                    .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            mAuth.signOut();
                            user.resetAllValues();

                            Intent intent = new Intent(MainScreen.this, StartNowActivity.class);
                            startActivity(intent);
                        }
                    })
                    .setNegativeButton("CANCEL", null).show();

            return true;
        }


        return false;
    }

    private void setCurrentDate(Date date) {

        this.date = dateFormat.format(date);
        showDate();

    }

    private void showDate() {

        if (this.date.equals(dateFormat.format(new Date()))) {

            textViewDatePickerAppBarMainScreen.setText("Today");

        } else {

            textViewDatePickerAppBarMainScreen.setText(this.date);
        }
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        int id = menuItem.getItemId();

        switch (id) {

            case R.id.dietDiary:

                showCalendar();
                unlockAppBarOpen();

                DietDiaryFragment dietDiaryFragment = new DietDiaryFragment();

                setArgumentsDietDiaryFragment(dietDiaryFragment);
                openFragment(dietDiaryFragment);

                drawerLayout.closeDrawer(GravityCompat.START);

                break;

            case R.id.weightController:

                hideCalendar("Weight Controller");
                lockAppBarClosed();

                WeightControllerFragment weightControllerFragment = new WeightControllerFragment();

                setArgumentsWeightControllerFragment(weightControllerFragment);

                openFragment(weightControllerFragment);

                drawerLayout.closeDrawer(GravityCompat.START);

                break;

            case R.id.muscleMassController:

                if(user.getMuscleMass() == 0) {

                    Toast.makeText(this, "You didn't enter muscle mass!", Toast.LENGTH_LONG).show();

                } else {

                    hideCalendar("Muscle Mass Controller");
                    lockAppBarClosed();

                    MuscleMassControllerFragment muscleMassControllerFragment = new MuscleMassControllerFragment();

                    setArgumentsMuscleMassControllerFragment(muscleMassControllerFragment);

                    openFragment(muscleMassControllerFragment);

                    drawerLayout.closeDrawer(GravityCompat.START);
                }
                
                break;


            case R.id.bodyCircuits:

                hideCalendar("Body Circuits");
                lockAppBarClosed();

                openFragment(new BodyCircuitsFragment());

                drawerLayout.closeDrawer(GravityCompat.START);

                break;

            case R.id.HarissBenedict:

                Intent harissBenedict = new Intent(MainScreen.this, ActivityHarissBenedict.class);
                startActivity(harissBenedict);

                drawerLayout.closeDrawer(GravityCompat.START);

                break;

            case R.id.MifflinStJeor:

                Intent mifflinStJeor = new Intent(MainScreen.this, ActivityMifflinStJeor.class);
                startActivity(mifflinStJeor);

                drawerLayout.closeDrawer(GravityCompat.START);

                break;

            case R.id.KatchMcArdle:

                if(user.getMuscleMass() == 0) {

                    Toast.makeText(this, "This method uses muscle mass to calculate caloric demand!", Toast.LENGTH_LONG).show();

                } else {

                    Intent katchMcArdle = new Intent(MainScreen.this, ActivityKatchMcArdle.class);
                    startActivity(katchMcArdle);

                    drawerLayout.closeDrawer(GravityCompat.START);
                }

                break;
        }

        return true;
    }

    private BottomNavigationView.OnNavigationItemSelectedListener bottomNavigationListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                    int id = menuItem.getItemId();

                    switch (id) {

                        case R.id.bottomNavigationDietDiary:

                            showCalendar();
                            unlockAppBarOpen();

                            DietDiaryFragment dietDiaryFragment = new DietDiaryFragment();
                            setArgumentsDietDiaryFragment(dietDiaryFragment);
                            openFragment(dietDiaryFragment);

                            break;

                        case R.id.bottomNavigationWeightController:

                            hideCalendar("Weight Controller");
                            lockAppBarClosed();

                            WeightControllerFragment weightControllerFragment = new WeightControllerFragment();
                            setArgumentsWeightControllerFragment(weightControllerFragment);

                            openFragment(weightControllerFragment);

                            break;

                        case R.id.bottomNavigationBodyCircuits:

                            hideCalendar("Body Circuits");
                            lockAppBarClosed();

                            openFragment(new BodyCircuitsFragment());

                            break;
                    }

                    return true;
                }
            };

    @Override
    public void onClick(View view) {

        int id = view.getId();

        if (id == R.id.relativeLayoutDatePickerButton) {

            float rotation = isExpaned ? 0 : 180;

            ViewCompat.animate(imageViewArrowAppBarMainScreen).rotation(rotation).start();

            isExpaned = !isExpaned;
            appBarLayout.setExpanded(isExpaned, true);
        }
    }

    private void hideCalendar(String title) {

        textViewToolbarTittle.setText(title);

        textViewDatePickerAppBarMainScreen.setVisibility(View.INVISIBLE);
        imageViewArrowAppBarMainScreen.setVisibility(View.INVISIBLE);

        relativeLayoutDatePickerButton.setClickable(false);
    }

    private void showCalendar() {

        textViewToolbarTittle.setText("");

        textViewDatePickerAppBarMainScreen.setVisibility(View.VISIBLE);
        imageViewArrowAppBarMainScreen.setVisibility(View.VISIBLE);

        relativeLayoutDatePickerButton.setClickable(true);
    }

    private void lockAppBarClosed() {

        appBarLayout.setExpanded(false, false);
        appBarLayout.setActivated(false);

        CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) appBarLayout.getLayoutParams();
        layoutParams.height = (int) getResources().getDimension(R.dimen.lockAppBarClosed);
    }

    private void unlockAppBarOpen() {

        appBarLayout.setExpanded(false, false);
        appBarLayout.setActivated(true);

        CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) appBarLayout.getLayoutParams();
        layoutParams.height = (int) getResources().getDimension(R.dimen.unlockAppBarClosed);
    }

    private void openFragment(Fragment fragment) {

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        transaction.replace(R.id.content_main_screen, fragment);
        transaction.commit();
    }

    private void lockDrag() {

        if (appBarLayout.getLayoutParams() != null) {

            CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) appBarLayout.getLayoutParams();
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

    private void setEmail() {

        user.setCurrentUser(FirebaseAuth.getInstance().getCurrentUser());

        if (user.getCurrentUser() != null) {

            textViewEmailNavHeader.setText(user.getCurrentUser().getEmail());
        }
    }

    private void setInitialFragment() {

        FragmentManager fragmentManager = getSupportFragmentManager();
        DietDiaryFragment dietDiaryFragment = (DietDiaryFragment) fragmentManager.findFragmentByTag("DietDiaryFragment");

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        if (dietDiaryFragment == null) {

            dietDiaryFragment = new DietDiaryFragment();
        }

        setArgumentsDietDiaryFragment(dietDiaryFragment);

        fragmentTransaction.add(R.id.content_main_screen, dietDiaryFragment, "DietDiaryFragment");
        fragmentTransaction.commit();
    }

    private void setArgumentsDietDiaryFragment(DietDiaryFragment dietDiaryFragment) {

        Bundle bundle = new Bundle();

        bundle.putString("date", dateFormat.format(new Date()));
        bundle.putParcelable("breakfast", breakfast);
        bundle.putParcelable("lunch", lunch);
        bundle.putParcelable("dinner", dinner);
        bundle.putParcelable("snacks", snacks);

        dietDiaryFragment.setArguments(bundle);
    }

    private void setArgumentsWeightControllerFragment(WeightControllerFragment  weightControllerFragment) {

        Bundle bundle = new Bundle();

        bundle.putSerializable("allWeights", allWeights);
        bundle.putSerializable("datesWeightController", datesWeightController);

        weightControllerFragment.setArguments(bundle);
    }

    private void setArgumentsMuscleMassControllerFragment(MuscleMassControllerFragment muscleMassControllerFragment) {

        Bundle bundle = new Bundle();

        bundle.putSerializable("allMuscleMass", allMuscleMass);
        bundle.putSerializable("datesMuscleMassController", datesMuscleMassController);

        muscleMassControllerFragment.setArguments(bundle);
    }

    @Override
    public void onBackPressed() {

        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {

            drawerLayout.closeDrawer(GravityCompat.START);

        } else {

            Intent intent = new Intent(Intent.ACTION_MAIN);

            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            startActivity(intent);
        }
    }

    @Override
    public void collapse() {

        if(isExpaned) {

            ViewCompat.animate(imageViewArrowAppBarMainScreen).rotation(0).start();
            appBarLayout.setExpanded(false, true);

            isExpaned = false;
        }
    }

    @Override
    public void enable(boolean b) {

        enableBottomBar(b);
    }

    public interface FragmentRefreshDietDiary {

        void refresh();
    }

    public void setFragmentRefreshDietDiary(FragmentRefreshDietDiary refreshDietDiaryFragment) {

        this.refreshDietDiaryFragment = refreshDietDiaryFragment;
    }

    public FragmentRefreshDietDiary getFragmentRefreshDietDiary() {

        return refreshDietDiaryFragment;
    }

    public interface RefreshDate {

        void refreshDate(String date);
    }

    public void setRefreshDate(RefreshDate refreshDate) {
        this.refreshDate = refreshDate;
    }

    public RefreshDate getRefreshDate() {

        return refreshDate;
    }
}
