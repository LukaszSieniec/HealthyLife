package com.example.adding_product;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.example.diary.MainScreen;
import com.example.expandable_recyclerview.Product;
import com.example.expandable_recyclerview.ProductDatabase;
import com.example.healthylife.R;
import com.example.registration.DecimalDigitsInputFilter;
import com.example.registration.UpdateButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

import static com.example.format_numbers.FormatNumbers.*;

public class SaveProduct extends AppCompatActivity implements TextWatcher, UpdateButton, View.OnClickListener, View.OnTouchListener {

    private EditText editTextCurrentWeightSaveProduct;
    private Button buttonSaveProduct;
    private TextView textViewGramSaveProduct, textViewCaloriesSaveProduct, textViewFatSaveProduct, textViewCarbohydratesSaveProduct, textViewProteinSaveProduct,
            textViewPortionWeightSaveProduct, textViewPerPortionkJSaveProduct, textViewPerPortionPercentEnergySaveProduct, textViewPerPortionCaloriesSaveProduct,
            textViewPerPortionFatWeightSaveProduct, textViewPerPortionPercentFatSaveProduct, textViewPerPortionWeightCarbohydratesSaveProduct,
            textViewPerPortionPercentCarbohydratesSaveProduct, textViewPerPortionWeightProteinSaveProduct, textViewPerPortionPercentProteinSaveProduct;

    private NestedScrollView nestedScrollViewSaveProduct;

    private ProductDatabase database;

    private CollapsingToolbarLayout collapsingToolbarLayoutSaveProduct;
    private Product selectedProduct;
    private Product newProduct;

    private FirebaseFirestore firebaseFirestore;
    private String currentUser;

    private String date = "";
    private String typeOfMeal = "";
    private float consumedCalories;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_product);

        collapsingToolbarLayoutSaveProduct = findViewById(R.id.collapsingToolbarLayoutSaveProduct);

        editTextCurrentWeightSaveProduct = findViewById(R.id.editTextCurrentWeightSaveProduct);

        buttonSaveProduct = findViewById(R.id.buttonSaveProduct);

        textViewGramSaveProduct = findViewById(R.id.textViewGramSaveProduct);
        textViewCaloriesSaveProduct = findViewById(R.id.textViewCaloriesSaveProduct);
        textViewFatSaveProduct = findViewById(R.id.textViewFatSaveProduct);
        textViewCarbohydratesSaveProduct = findViewById(R.id.textViewCarbohydratesSaveProduct);
        textViewProteinSaveProduct = findViewById(R.id.textViewProteinSaveProduct);
        textViewPortionWeightSaveProduct = findViewById(R.id.textViewPortionWeightSaveProduct);
        textViewPerPortionkJSaveProduct = findViewById(R.id.textViewPerPortionkJSaveProduct);
        textViewPerPortionPercentEnergySaveProduct = findViewById(R.id.textViewPerPortionPercentEnergySaveProduct);
        textViewPerPortionCaloriesSaveProduct = findViewById(R.id.textViewPerPortionCaloriesSaveProduct);
        textViewPerPortionFatWeightSaveProduct = findViewById(R.id.textViewPerPortionFatWeightSaveProduct);
        textViewPerPortionPercentFatSaveProduct = findViewById(R.id.textViewPerPortionPercentFatSaveProduct);
        textViewPerPortionWeightCarbohydratesSaveProduct = findViewById(R.id.textViewPerPortionWeightCarbohydratesSaveProduct);
        textViewPerPortionPercentCarbohydratesSaveProduct = findViewById(R.id.textViewPerPortionPercentCarbohydratesSaveProduct);
        textViewPerPortionWeightProteinSaveProduct = findViewById(R.id.textViewPerPortionWeightProteinSaveProduct);
        textViewPerPortionPercentProteinSaveProduct = findViewById(R.id.textViewPerPortionPercentProteinSaveProduct);

        nestedScrollViewSaveProduct = findViewById(R.id.nestedScrollViewSaveProduct);

        editTextCurrentWeightSaveProduct.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(3, 2)});
        editTextCurrentWeightSaveProduct.addTextChangedListener(this);

        buttonSaveProduct.setOnClickListener(this);
        nestedScrollViewSaveProduct.setOnTouchListener(this);

        database = ProductDatabase.getInstance(this);

        selectedProduct = getIntent().getParcelableExtra("product");
        newProduct = new Product(selectedProduct.getName(), selectedProduct.getUnit());

        date = getIntent().getStringExtra("date");
        typeOfMeal = getIntent().getStringExtra("meal");
        consumedCalories = getIntent().getFloatExtra("consumedCalories", consumedCalories);

        firebaseFirestore = FirebaseFirestore.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();

        setInitialProductValues();

        setCollapsingToolbarLayoutSaveProduct();

    }

    private void setCollapsingToolbarLayoutSaveProduct() {

        collapsingToolbarLayoutSaveProduct.setExpandedTitleTextAppearance(R.style.ExpandedAppBar);
        collapsingToolbarLayoutSaveProduct.setCollapsedTitleTextAppearance(R.style.CollapsedAppBar);
        collapsingToolbarLayoutSaveProduct.setTitle(selectedProduct.getName());
    }

    private void setInitialProductValues() {

        editTextCurrentWeightSaveProduct.setText(formatNumberWithoutDecimalPlaces(selectedProduct.getWeight()));

        textViewGramSaveProduct.setText(selectedProduct.getUnit());
        textViewCaloriesSaveProduct.setText(getResources().getString(R.string.textViewCaloriesSaveProduct, formatNumberWithoutDecimalPlaces(selectedProduct.getCalories())));
        textViewFatSaveProduct.setText(getResources().getString(R.string.textViewFatSaveProduct, formatNumberWithDecimalPlaces(selectedProduct.getFat())));
        textViewCarbohydratesSaveProduct.setText(getResources().getString(R.string.textViewCarbohydratesSaveProduct, formatNumberWithDecimalPlaces(selectedProduct.getCarbohydrates())));
        textViewProteinSaveProduct.setText(getResources().getString(R.string.textViewProteinSaveProduct, formatNumberWithDecimalPlaces(selectedProduct.getProtein())));

        textViewPortionWeightSaveProduct.setText(getResources().getString(R.string.textViewPortionWeightSaveProduct,
                formatNumberWithoutDecimalPlaces(selectedProduct.getWeight()), selectedProduct.getUnit()));

        textViewPerPortionkJSaveProduct.setText(getResources().getString(R.string.textViewPerPortionkJSaveProduct,
                formatNumberWithoutDecimalPlaces(selectedProduct.convertCaloriesToKiloJoules(selectedProduct.getCalories()))));

        textViewPerPortionPercentEnergySaveProduct.setText(getResources().getString(R.string.textViewPerPortionPercentEnergySaveProduct,
                formatNumberWithoutDecimalPlaces(selectedProduct.calculatePercentEnergyRWS(selectedProduct.getCalories()))));

        textViewPerPortionCaloriesSaveProduct.setText(getResources().getString(R.string.textViewPerPortionCaloriesSaveProduct, formatNumberWithoutDecimalPlaces(selectedProduct.getCalories())));

        textViewPerPortionFatWeightSaveProduct.setText(getResources().getString(R.string.textViewPerPortionFatWeightSaveProduct, formatNumberWithDecimalPlaces(selectedProduct.getFat())));

        textViewPerPortionPercentFatSaveProduct.setText(getResources().getString(R.string.textViewPerPortionPercentFatSaveProduct,
                formatNumberWithoutDecimalPlaces(selectedProduct.calculatePercentFatRWS(selectedProduct.getFat()))));

        textViewPerPortionWeightCarbohydratesSaveProduct.setText(getResources().getString(R.string.textViewPerPortionWeightCarbohydratesSaveProduct,
                formatNumberWithDecimalPlaces(selectedProduct.getCarbohydrates())));

        textViewPerPortionPercentCarbohydratesSaveProduct.setText(getResources().getString(R.string.textViewPerPortionPercentCarbohydratesSaveProduct,
                formatNumberWithoutDecimalPlaces(selectedProduct.calculatePercentCarbohydratesRWS(selectedProduct.getCarbohydrates()))));

        textViewPerPortionWeightProteinSaveProduct.setText(getResources().getString(R.string.textViewPerPortionWeightProteinSaveProduct,
                formatNumberWithDecimalPlaces(selectedProduct.getProtein())));

        textViewPerPortionPercentProteinSaveProduct.setText(getResources().getString(R.string.textViewPerPortionPercentProteinSaveProduct,
                formatNumberWithoutDecimalPlaces(selectedProduct.calculatePercentProteinRWS(selectedProduct.getProtein()))));

    }


    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

    @Override
    public void onTextChanged(CharSequence charSequence, int start, int before, int count) {

        if((charSequence.toString().length() > 0)) {

            float currentWeight = Float.parseFloat(charSequence.toString());

            newProduct.setCarbohydrates(selectedProduct.updateCarbohydrates(currentWeight));
            newProduct.setProtein(selectedProduct.updateProtein(currentWeight));
            newProduct.setFat(selectedProduct.updateFat(currentWeight));
            newProduct.setWeight(selectedProduct.updatePortionWeight(currentWeight));
            newProduct.setCalories(selectedProduct.updateCalories(currentWeight));

            textViewCaloriesSaveProduct.setText(getResources().getString(R.string.textViewCaloriesSaveProduct, formatNumberWithoutDecimalPlaces(selectedProduct.updateCalories(currentWeight))));

            textViewFatSaveProduct.setText(getResources().getString(R.string.textViewFatSaveProduct, formatNumberWithDecimalPlaces(selectedProduct.updateFat(currentWeight))));

            textViewCarbohydratesSaveProduct.setText(getResources().getString(R.string.textViewCarbohydratesSaveProduct, formatNumberWithDecimalPlaces(selectedProduct.updateCarbohydrates(currentWeight))));

            textViewProteinSaveProduct.setText(getResources().getString(R.string.textViewProteinSaveProduct, formatNumberWithDecimalPlaces(selectedProduct.updateProtein(currentWeight))));

            textViewPortionWeightSaveProduct.setText(getResources().getString(R.string.textViewPortionWeightSaveProduct, formatNumberWithoutDecimalPlaces(selectedProduct.updatePortionWeight(currentWeight)),
                    selectedProduct.getUnit()));

            textViewPerPortionkJSaveProduct.setText(getResources().getString(R.string.textViewPerPortionkJSaveProduct, formatNumberWithoutDecimalPlaces(selectedProduct.updatePerPortionkJ(currentWeight))));

            textViewPerPortionPercentEnergySaveProduct.setText(getResources().getString(R.string.textViewPerPortionPercentEnergySaveProduct, formatNumberWithoutDecimalPlaces(selectedProduct.updatePerPortionPercentEnergy(currentWeight))));

            textViewPerPortionCaloriesSaveProduct.setText(getResources().getString(R.string.textViewPerPortionCaloriesSaveProduct, formatNumberWithoutDecimalPlaces(selectedProduct.updatePerPortionCalories(currentWeight))));

            textViewPerPortionFatWeightSaveProduct.setText(getResources().getString(R.string.textViewPerPortionFatWeightSaveProduct, formatNumberWithDecimalPlaces(selectedProduct.updatePerPortionFatWeight(currentWeight))));

            textViewPerPortionPercentFatSaveProduct.setText(getResources().getString(R.string.textViewPerPortionPercentFatSaveProduct, formatNumberWithoutDecimalPlaces(selectedProduct.updatePerPortionPercentFat(currentWeight))));

            textViewPerPortionWeightCarbohydratesSaveProduct.setText(getResources().getString(R.string.textViewPerPortionWeightCarbohydratesSaveProduct, formatNumberWithDecimalPlaces(selectedProduct.updatePerPortionWeightCarbohydrates(currentWeight))));

            textViewPerPortionPercentCarbohydratesSaveProduct.setText(getResources().getString(R.string.textViewPerPortionPercentCarbohydratesSaveProduct, formatNumberWithoutDecimalPlaces(selectedProduct.updatePerPortionPercentCarbohydrates(currentWeight))));

            textViewPerPortionWeightProteinSaveProduct.setText(getResources().getString(R.string.textViewPerPortionWeightProteinSaveProduct, formatNumberWithDecimalPlaces(selectedProduct.updatePerPortionWeightProtein(currentWeight))));

            textViewPerPortionPercentProteinSaveProduct.setText(getResources().getString(R.string.textViewPerPortionPercentProteinSaveProduct, formatNumberWithoutDecimalPlaces(selectedProduct.updatePerPortionPercentProtein(currentWeight))));

            unlockButton();

        } else {

            lockButton();
        }
    }

    @Override
    public void afterTextChanged(Editable s) { }

    @Override
    public void onClick(View view) {

        int id = view.getId();

        if(id == R.id.buttonSaveProduct) {

            firebaseFirestore.collection("Users").document(currentUser)
                    .collection("Types of Meals").document(typeOfMeal)
                    .collection("Date of " + typeOfMeal).document(date)
                    .collection("List of Products")
                    .add(newProduct)
                    .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentReference> task) {

                            if(task.isSuccessful()) {

                                float newValueConsumedCalories = newProduct.getCalories() + consumedCalories;

                                Map<String, Object> caloriesConsumed = new HashMap<>();
                                caloriesConsumed.put("Calories", newValueConsumedCalories);

                                firebaseFirestore.collection("Users").document(currentUser)
                                        .collection("Information of the day").document("Calories consumed")
                                        .collection("Years").document(date.split(" ")[2])
                                        .collection("Months").document(date.split(" ")[1])
                                        .collection("Days").document(date.split(" ")[0])
                                        .set(caloriesConsumed).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        if(task.isSuccessful()) {

                                            Toast.makeText(SaveProduct.this, "New product added", Toast.LENGTH_SHORT).show();

                                            database.productDao().insertProduct(newProduct);

                                            Intent intent = new Intent(SaveProduct.this, MainScreen.class);
                                            intent.putExtra("date", date);
                                            startActivity(intent);
                                        }
                                    }
                                });
                            }
                        }
                    });

        }
    }

    @Override
    public void unlockButton() {

        buttonSaveProduct.setClickable(true);
        buttonSaveProduct.setAlpha(1.0f);
    }

    @Override
    public void lockButton() {

        buttonSaveProduct.setClickable(false);
        buttonSaveProduct.setAlpha(0.2f);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        if (event != null && event.getAction() == MotionEvent.ACTION_MOVE) {

            InputMethodManager inputMethodManager = ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE));
            boolean isKeyboardUp = inputMethodManager.isAcceptingText();

            if (isKeyboardUp) {
                inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
        }

        return false;
    }
}