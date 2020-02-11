package com.example.adding_product;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.example.healthylife.R;
import com.google.android.material.tabs.TabLayout;

public class SearchProduct extends AppCompatActivity implements View.OnClickListener, DialogFragmentChangeTypeOfMeal.OnChangeTypeOfMealDialogFragment {

    private TextView textViewNameMealSearchProduct, textViewDateSearchProduct;

    private ViewPager pagerSearchProduct;

    private MyPagerAdapter myPagerAdapter;

    private TabLayout tabLayoutSearchProduct;

    private ImageView imageViewBackSearchProduct;

    private LinearLayout linearLayoutSearchProduct;

    private String date = "";
    private String typeOfMeal = "";
    private float consumedCalories;

    private FragmentManager manager;

    private OnChangeTypeOfMealInFragment onChangeTypeOfMealInFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_product);

        textViewNameMealSearchProduct = findViewById(R.id.textViewNameMealSearchProduct);
        textViewDateSearchProduct = findViewById(R.id.textViewDateSearchProduct);

        imageViewBackSearchProduct = findViewById(R.id.imageViewBackSearchProduct);

        pagerSearchProduct = findViewById(R.id.pagerSearchProduct);
        tabLayoutSearchProduct = findViewById(R.id.tabLayoutSearchProduct);

        linearLayoutSearchProduct = findViewById(R.id.linearLayoutSearchProduct);

        imageViewBackSearchProduct.setOnClickListener(this);
        linearLayoutSearchProduct.setOnClickListener(this);

        date = getIntent().getStringExtra("date");
        typeOfMeal = getIntent().getStringExtra("meal");
        consumedCalories = getIntent().getFloatExtra("consumedCalories", 0);

        myPagerAdapter = new MyPagerAdapter(getSupportFragmentManager(), date, typeOfMeal, consumedCalories);
        pagerSearchProduct.setAdapter(myPagerAdapter);

        tabLayoutSearchProduct.setupWithViewPager(pagerSearchProduct);

        manager = getSupportFragmentManager();

        textViewNameMealSearchProduct.setText(typeOfMeal);
        textViewDateSearchProduct.setText(date);

    }

    @Override
    public void onClick(View view) {

        int id = view.getId();

        if(id == R.id.imageViewBackSearchProduct) {

            onBackPressed();

        } else if(id == R.id.linearLayoutSearchProduct) {

            DialogFragmentChangeTypeOfMeal dialogFragmentChangeTypeOfMeal = new DialogFragmentChangeTypeOfMeal();

            Bundle bundle = new Bundle();
            bundle.putString("meal", typeOfMeal);

            dialogFragmentChangeTypeOfMeal.setArguments(bundle);
            dialogFragmentChangeTypeOfMeal.show(manager.beginTransaction(), "Change Type Of Meal");
        }
    }

    @Override
    public void changeTypeOfMeal(String typeOfMeal) {

        textViewNameMealSearchProduct.setText(typeOfMeal);

        myPagerAdapter.setTypeOfMeal(typeOfMeal);
        onChangeTypeOfMealInFragment.changeTypeOfMealInFragment(typeOfMeal);
    }

    public interface OnChangeTypeOfMealInFragment {

        void changeTypeOfMealInFragment(String typeOfMeal);
    }

    public void setOnChangeTypeOfMealInFragment(OnChangeTypeOfMealInFragment onChangeTypeOfMealInFragment) {

        this.onChangeTypeOfMealInFragment = onChangeTypeOfMealInFragment;
    }
}
