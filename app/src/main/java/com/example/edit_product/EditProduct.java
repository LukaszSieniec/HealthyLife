package com.example.edit_product;

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
import com.example.expandable_recyclerview.Meal;
import com.example.expandable_recyclerview.Product;
import com.example.healthylife.R;
import com.example.registration.DecimalDigitsInputFilter;
import com.example.registration.UpdateButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;
import static com.example.format_numbers.FormatNumbers.formatNumberWithDecimalPlaces;
import static com.example.format_numbers.FormatNumbers.formatNumberWithoutDecimalPlaces;

public class EditProduct extends AppCompatActivity implements TextWatcher, UpdateButton, View.OnClickListener, View.OnTouchListener {

    private EditText editTextCurrentWeightEditProduct;
    private Button buttonDeleteProduct, buttonEditProduct;

    private TextView textViewGramEditProduct, textViewCaloriesEditProduct, textViewFatEditProduct, textViewCarbohydratesEditProduct, textViewProteinEditProduct,
            textViewPortionWeightEditProduct, textViewPerPortionkJEditProduct, textViewPerPortionPercentEnergyEditProduct, textViewPerPortionCaloriesEditProduct,
            textViewPerPortionFatWeightEditProduct, textViewPerPortionPercentFatEditProduct, textViewPerPortionWeightCarbohydratesEditProduct,
            textViewPerPortionPercentCarbohydratesEditProduct, textViewPerPortionWeightProteinEditProduct, textViewPerPortionPercentProteinEditProduct;

    private NestedScrollView nestedScrollViewEditProduct;

    private CollapsingToolbarLayout collapsingToolbarLayoutEditProduct;

    private Meal selectedMeal = new Meal();

    private Product selectedProduct;
    private Product editProduct;

    private String date = "";
    private String typeOfMeal = "";
    private int position;

    private FirebaseFirestore firebaseFirestore;
    private String currentUser;

    private float consumedCalories;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_product);

        collapsingToolbarLayoutEditProduct = findViewById(R.id.collapsingToolbarLayoutEditProduct);

        editTextCurrentWeightEditProduct = findViewById(R.id.editTextCurrentWeightEditProduct);

        buttonDeleteProduct = findViewById(R.id.buttonDeleteProduct);
        buttonEditProduct = findViewById(R.id.buttonEditProduct);

        textViewGramEditProduct = findViewById(R.id.textViewGramEditProduct);
        textViewCaloriesEditProduct = findViewById(R.id.textViewCaloriesEditProduct);
        textViewFatEditProduct = findViewById(R.id.textViewFatEditProduct);
        textViewCarbohydratesEditProduct = findViewById(R.id.textViewCarbohydratesEditProduct);
        textViewProteinEditProduct = findViewById(R.id.textViewProteinEditProduct);
        textViewPortionWeightEditProduct = findViewById(R.id.textViewPortionWeightEditProduct);
        textViewPerPortionkJEditProduct = findViewById(R.id.textViewPerPortionkJEditProduct);
        textViewPerPortionPercentEnergyEditProduct = findViewById(R.id.textViewPerPortionPercentEnergyEditProduct);
        textViewPerPortionCaloriesEditProduct = findViewById(R.id.textViewPerPortionCaloriesEditProduct);
        textViewPerPortionFatWeightEditProduct = findViewById(R.id.textViewPerPortionFatWeightEditProduct);
        textViewPerPortionPercentFatEditProduct = findViewById(R.id.textViewPerPortionPercentFatEditProduct);
        textViewPerPortionWeightCarbohydratesEditProduct = findViewById(R.id.textViewPerPortionWeightCarbohydratesEditProduct);
        textViewPerPortionPercentCarbohydratesEditProduct = findViewById(R.id.textViewPerPortionPercentCarbohydratesEditProduct);
        textViewPerPortionWeightProteinEditProduct = findViewById(R.id.textViewPerPortionWeightProteinEditProduct);
        textViewPerPortionPercentProteinEditProduct= findViewById(R.id.textViewPerPortionPercentProteinEditProduct);

        nestedScrollViewEditProduct = findViewById(R.id.nestedScrollViewEditProduct);

        editTextCurrentWeightEditProduct.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(3, 2)});
        editTextCurrentWeightEditProduct.addTextChangedListener(this);

        buttonDeleteProduct.setOnClickListener(this);
        buttonEditProduct.setOnClickListener(this);
        nestedScrollViewEditProduct.setOnTouchListener(this);

        selectedProduct = getIntent().getParcelableExtra("product");
        editProduct = new Product(selectedProduct.getName(), selectedProduct.getUnit());

        date = getIntent().getStringExtra("date");
        typeOfMeal = getIntent().getStringExtra("meal");
        consumedCalories = getIntent().getFloatExtra("consumedCalories", 0);
        position = getIntent().getIntExtra("position", -1);
        selectedMeal.setIdProductsOfMeal(getIntent().getStringArrayListExtra("idProducts"));

        firebaseFirestore = FirebaseFirestore.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();

        setInitialProductValues();
        setCollapsingToolbarLayoutEditProduct();

    }

    private void setCollapsingToolbarLayoutEditProduct() {

        collapsingToolbarLayoutEditProduct.setExpandedTitleTextAppearance(R.style.ExpandedAppBar);
        collapsingToolbarLayoutEditProduct.setCollapsedTitleTextAppearance(R.style.CollapsedAppBar);
        collapsingToolbarLayoutEditProduct.setTitle(selectedProduct.getName());
    }

    private void setInitialProductValues() {

        editTextCurrentWeightEditProduct.setText(formatNumberWithoutDecimalPlaces(selectedProduct.getWeight()));

        textViewGramEditProduct.setText(selectedProduct.getUnit());
        textViewCaloriesEditProduct.setText(getResources().getString(R.string.textViewCaloriesEditProduct, formatNumberWithoutDecimalPlaces(selectedProduct.getCalories())));
        textViewFatEditProduct.setText(getResources().getString(R.string.textViewFatEditProduct, formatNumberWithDecimalPlaces(selectedProduct.getFat())));
        textViewCarbohydratesEditProduct.setText(getResources().getString(R.string.textViewCarbohydratesEditProduct, formatNumberWithDecimalPlaces(selectedProduct.getCarbohydrates())));
        textViewProteinEditProduct.setText(getResources().getString(R.string.textViewProteinEditProduct, formatNumberWithDecimalPlaces(selectedProduct.getProtein())));

        textViewPortionWeightEditProduct.setText(getResources().getString(R.string.textViewPortionWeightEditProduct,
                formatNumberWithoutDecimalPlaces(selectedProduct.getWeight()), selectedProduct.getUnit()));

        textViewPerPortionkJEditProduct.setText(getResources().getString(R.string.textViewPerPortionkJEditProduct,
                formatNumberWithoutDecimalPlaces(selectedProduct.convertCaloriesToKiloJoules(selectedProduct.getCalories()))));

        textViewPerPortionPercentEnergyEditProduct.setText(getResources().getString(R.string.textViewPerPortionPercentEnergyEditProduct,
                formatNumberWithoutDecimalPlaces(selectedProduct.calculatePercentEnergyRWS(selectedProduct.getCalories()))));

        textViewPerPortionCaloriesEditProduct.setText(getResources().getString(R.string.textViewPerPortionCaloriesEditProduct, formatNumberWithoutDecimalPlaces(selectedProduct.getCalories())));

        textViewPerPortionFatWeightEditProduct.setText(getResources().getString(R.string.textViewPerPortionFatWeightEditProduct, formatNumberWithDecimalPlaces(selectedProduct.getFat())));

        textViewPerPortionPercentFatEditProduct.setText(getResources().getString(R.string.textViewPerPortionPercentFatEditProduct,
                formatNumberWithoutDecimalPlaces(selectedProduct.calculatePercentFatRWS(selectedProduct.getFat()))));

        textViewPerPortionWeightCarbohydratesEditProduct.setText(getResources().getString(R.string.textViewPerPortionWeightCarbohydratesEditProduct,
                formatNumberWithDecimalPlaces(selectedProduct.getCarbohydrates())));

        textViewPerPortionPercentCarbohydratesEditProduct.setText(getResources().getString(R.string.textViewPerPortionPercentCarbohydratesEditProduct,
                formatNumberWithoutDecimalPlaces(selectedProduct.calculatePercentCarbohydratesRWS(selectedProduct.getCarbohydrates()))));

        textViewPerPortionWeightProteinEditProduct.setText(getResources().getString(R.string.textViewPerPortionWeightProteinEditProduct,
                formatNumberWithDecimalPlaces(selectedProduct.getProtein())));

        textViewPerPortionPercentProteinEditProduct.setText(getResources().getString(R.string.textViewPerPortionPercentProteinEditProduct,
                formatNumberWithoutDecimalPlaces(selectedProduct.calculatePercentProteinRWS(selectedProduct.getProtein()))));


    }


    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int start, int before, int count) {

        if((charSequence.toString().length() > 0) && (!charSequence.toString().equals("."))) {

            float currentWeight = Float.parseFloat(charSequence.toString());

            editProduct.setCarbohydrates(selectedProduct.updateCarbohydrates(currentWeight));
            editProduct.setProtein(selectedProduct.updateProtein(currentWeight));
            editProduct.setFat(selectedProduct.updateFat(currentWeight));
            editProduct.setWeight(selectedProduct.updatePortionWeight(currentWeight));
            editProduct.setCalories(selectedProduct.updateCalories(currentWeight));

            textViewCaloriesEditProduct.setText(getResources().getString(R.string.textViewCaloriesEditProduct, formatNumberWithoutDecimalPlaces(selectedProduct.updateCalories(currentWeight))));

            textViewFatEditProduct.setText(getResources().getString(R.string.textViewFatEditProduct, formatNumberWithDecimalPlaces(selectedProduct.updateFat(currentWeight))));

            textViewCarbohydratesEditProduct.setText(getResources().getString(R.string.textViewCarbohydratesEditProduct, formatNumberWithDecimalPlaces(selectedProduct.updateCarbohydrates(currentWeight))));

            textViewProteinEditProduct.setText(getResources().getString(R.string.textViewProteinEditProduct, formatNumberWithDecimalPlaces(selectedProduct.updateProtein(currentWeight))));

            textViewPortionWeightEditProduct.setText(getResources().getString(R.string.textViewPortionWeightEditProduct, formatNumberWithoutDecimalPlaces(selectedProduct.updatePortionWeight(currentWeight))
            , selectedProduct.getUnit()));

            textViewPerPortionkJEditProduct.setText(getResources().getString(R.string.textViewPerPortionkJEditProduct, formatNumberWithoutDecimalPlaces(selectedProduct.updatePerPortionkJ(currentWeight))));

            textViewPerPortionPercentEnergyEditProduct.setText(getResources().getString(R.string.textViewPerPortionPercentEnergyEditProduct, formatNumberWithoutDecimalPlaces(selectedProduct.updatePerPortionPercentEnergy(currentWeight))));

            textViewPerPortionCaloriesEditProduct.setText(getResources().getString(R.string.textViewPerPortionCaloriesEditProduct, formatNumberWithoutDecimalPlaces(selectedProduct.updatePerPortionCalories(currentWeight))));

            textViewPerPortionFatWeightEditProduct.setText(getResources().getString(R.string.textViewPerPortionFatWeightEditProduct, formatNumberWithDecimalPlaces(selectedProduct.updatePerPortionFatWeight(currentWeight))));

            textViewPerPortionPercentFatEditProduct.setText(getResources().getString(R.string.textViewPerPortionPercentFatEditProduct, formatNumberWithoutDecimalPlaces(selectedProduct.updatePerPortionPercentFat(currentWeight))));

            textViewPerPortionWeightCarbohydratesEditProduct.setText(getResources().getString(R.string.textViewPerPortionWeightCarbohydratesEditProduct, formatNumberWithDecimalPlaces(selectedProduct.updatePerPortionWeightCarbohydrates(currentWeight))));

            textViewPerPortionPercentCarbohydratesEditProduct.setText(getResources().getString(R.string.textViewPerPortionPercentCarbohydratesEditProduct, formatNumberWithoutDecimalPlaces(selectedProduct.updatePerPortionPercentCarbohydrates(currentWeight))));

            textViewPerPortionWeightProteinEditProduct.setText(getResources().getString(R.string.textViewPerPortionWeightProteinEditProduct, formatNumberWithDecimalPlaces(selectedProduct.updatePerPortionWeightProtein(currentWeight))));

            textViewPerPortionPercentProteinEditProduct.setText(getResources().getString(R.string.textViewPerPortionPercentProteinEditProduct, formatNumberWithoutDecimalPlaces(selectedProduct.updatePerPortionPercentProtein(currentWeight))));

            unlockButton();

        } else {

            lockButton();
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    public void unlockButton() {

        buttonEditProduct.setClickable(true);
        buttonEditProduct.setAlpha(1.0f);
    }

    @Override
    public void lockButton() {

        buttonEditProduct.setClickable(false);
        buttonEditProduct.setAlpha(0.2f);
    }

    @Override
    public void onClick(View view) {

        int id = view.getId();

        if(id == R.id.buttonDeleteProduct) {

            deleteProduct();

        } else if (id == R.id.buttonEditProduct) {

            editProduct();
        }
    }

    private void deleteProduct() {

        firebaseFirestore.collection("Users").document(currentUser)
                .collection("Types of Meals").document(typeOfMeal)
                .collection("Date of " + typeOfMeal).document(date)
                .collection("List of Products").document(selectedMeal.getIdProductsOfMeal().get(position))
                .delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if(task.isSuccessful()) {

                    float newValueConsumedCalories = consumedCalories - selectedProduct.getCalories();

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

                                Toast.makeText(EditProduct.this, "You have removed the product", Toast.LENGTH_SHORT).show();

                                Intent intent = new Intent(EditProduct.this, MainScreen.class);
                                startActivity(intent);
                            }
                        }
                    });
                }
            }
        });
    }

    private void editProduct() {

        firebaseFirestore.collection("Users").document(currentUser)
                .collection("Types of Meals").document(typeOfMeal)
                .collection("Date of " + typeOfMeal).document(date)
                .collection("List of Products").document(selectedMeal.getIdProductsOfMeal().get(position))
                .update("carbohydrates", editProduct.getCarbohydrates(),
                        "protein", editProduct.getProtein(),
                        "fat", editProduct.getFat(),
                        "weight", editProduct.getWeight(),
                        "calories", editProduct.getCalories()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if(task.isSuccessful()) {

                    float newValueConsumedCalories = editProduct.getCalories() + consumedCalories - selectedProduct.getCalories();

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

                                Toast.makeText(EditProduct.this, "You have edited the product", Toast.LENGTH_SHORT).show();

                                Intent intent = new Intent(EditProduct.this, MainScreen.class);
                                startActivity(intent);
                            }
                        }
                    });
                }
            }
        });
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
