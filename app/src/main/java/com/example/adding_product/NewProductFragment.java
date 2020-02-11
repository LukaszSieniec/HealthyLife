package com.example.adding_product;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.example.expandable_recyclerview.Product;
import com.example.healthylife.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import static com.example.format_numbers.FormatNumbers.formatNumberWithDecimalPlaces;
import static com.example.format_numbers.FormatNumbers.formatNumberWithoutDecimalPlaces;

/**
 * A simple {@link Fragment} subclass.
 */
public class NewProductFragment extends Fragment implements View.OnClickListener, SearchProduct.OnChangeTypeOfMealInFragment {

    private TextView textViewEnterProductNameNewProduct, textViewEnterTheNutritionalValueNewProduct;
    private Button buttonSaveNewProduct;
    private LinearLayout linearLayoutEnterProductName, linearLayoutNutritionalValueNewProduct;

    private FragmentManager manager;
    private Product newProduct = new Product();

    private String date = "";
    private String typeOfMeal = "";
    private float consumedCalories;

    public static final int NAME_PRODUCT_DIALOG_FRAGMENT = 1;
    public static final int NUTRITIONAL_VALUE_DIALOG_FRAGMENT = 2;

    private DatabaseReference databaseReference;
    private int maxID = 0;

    public static NewProductFragment newInstance(String date, String typeOfMeal, float consumedCalories) {

        NewProductFragment newProductFragment = new NewProductFragment();

        Bundle bundle = new Bundle();
        bundle.putString("date", date);
        bundle.putString("typeOfMeal", typeOfMeal);
        bundle.putFloat("consumedCalories", consumedCalories);

        newProductFragment.setArguments(bundle);

        return newProductFragment;
    }

    public NewProductFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        if(getArguments() != null) {

            date = getArguments().getString("date");
            typeOfMeal = getArguments().getString("typeOfMeal");
            consumedCalories = getArguments().getFloat("consumedCalories");
        }

        ((SearchProduct)getActivity()).setOnChangeTypeOfMealInFragment(this);

        return inflater.inflate(R.layout.fragment_new_product, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        textViewEnterProductNameNewProduct = view.findViewById(R.id.textViewEnterProductNameNewProduct);
        textViewEnterTheNutritionalValueNewProduct = view.findViewById(R.id.textViewEnterTheNutritionalValueNewProduct);

        buttonSaveNewProduct = view.findViewById(R.id.buttonSaveNewProduct);

        linearLayoutEnterProductName = view.findViewById(R.id.linearLayoutEnterProductName);
        linearLayoutNutritionalValueNewProduct = view.findViewById(R.id.linearLayoutNutritionalValueNewProduct);

        buttonSaveNewProduct.setOnClickListener(this);
        linearLayoutEnterProductName.setOnClickListener(this);
        linearLayoutNutritionalValueNewProduct.setOnClickListener(this);

        manager = getFragmentManager();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Products");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {

                    maxID = (int) dataSnapshot.getChildrenCount();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onClick(View view) {

        int id = view.getId();

        switch (id) {

            case R.id.buttonSaveNewProduct:

                if((textViewEnterProductNameNewProduct.getText().toString().equals("Enter the product name"))
                    || (textViewEnterTheNutritionalValueNewProduct.getText().toString().equals("Enter the nutritional value"))) {

                    Toast.makeText(getActivity(), "To save a new product, enter all information!", Toast.LENGTH_LONG).show();

                } else {

                    addNewProduct();
                }

                break;


            case R.id.linearLayoutEnterProductName:

                DialogFragmentNameProduct dialogFragmentNameProduct = new DialogFragmentNameProduct();

                dialogFragmentNameProduct.setTargetFragment(this, NAME_PRODUCT_DIALOG_FRAGMENT);
                dialogFragmentNameProduct.show(manager.beginTransaction(), "Product Name");

                break;

            case R.id.linearLayoutNutritionalValueNewProduct:

                DialogFragmentNutritionalValue dialogFragmentNutritionalValue = new DialogFragmentNutritionalValue();

                dialogFragmentNutritionalValue.setTargetFragment(this, NUTRITIONAL_VALUE_DIALOG_FRAGMENT);
                dialogFragmentNutritionalValue.show(manager.beginTransaction(), "Nutritional Value");
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        Bundle bundle = data.getExtras();

        if ((resultCode == Activity.RESULT_OK) && (requestCode == NAME_PRODUCT_DIALOG_FRAGMENT)) {

            String productName = bundle.getString("productName");

            textViewEnterProductNameNewProduct.setText(productName);
            newProduct.setName(productName);

        } else if ((resultCode == Activity.RESULT_OK) && (requestCode == NUTRITIONAL_VALUE_DIALOG_FRAGMENT)) {

            float carbohydrates = bundle.getFloat("Carbohydrates");
            float protein = bundle.getFloat("Protein");
            float fat = bundle.getFloat("Fat");
            int weight = bundle.getInt("Weight");
            float calories = bundle.getFloat("Calories");
            String unit = bundle.getString("Unit");

            textViewEnterTheNutritionalValueNewProduct.setText(getResources().getString(R.string.textViewEnterTheNutritionalValueNewProductSecond,
                    String.valueOf(weight), String.valueOf(unit), formatNumberWithoutDecimalPlaces(calories), formatNumberWithDecimalPlaces(protein),
                    formatNumberWithDecimalPlaces(carbohydrates), formatNumberWithDecimalPlaces(fat)));

            newProduct.setCarbohydrates(carbohydrates);
            newProduct.setProtein(protein);
            newProduct.setFat(fat);
            newProduct.setWeight(weight);
            newProduct.setCalories(calories);
            newProduct.setUnit(unit);
        }
    }

    private void addNewProduct() {

        databaseReference.child(String.valueOf(maxID + 1)).setValue(newProduct);

        Intent intent = new Intent(getActivity(), SaveProduct.class);

        intent.putExtra("product", newProduct);
        intent.putExtra("date", date);
        intent.putExtra("meal", typeOfMeal);
        intent.putExtra("consumedCalories", consumedCalories);
        startActivity(intent);
    }

    @Override
    public void changeTypeOfMealInFragment(String typeOfMeal) {

        this.typeOfMeal = typeOfMeal;
    }
}