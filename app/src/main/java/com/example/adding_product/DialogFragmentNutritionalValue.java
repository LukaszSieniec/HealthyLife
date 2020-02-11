package com.example.adding_product;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.example.healthylife.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class DialogFragmentNutritionalValue extends DialogFragment implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    private ImageButton imageButtonCancelNutritionalValue, imageButtonSaveProductNutritionalValue;
    private EditText editTextAMetricPortionNutritionalValue,editTextCaloriesNutritionalValue, editTextProteinNutritionalValue,
                     editTextCarbohydratesNutritionalValue, editTextFatNutritionalValue;
    private TextView textViewUnitNutritionalValue;
    private Spinner spinnerNutritionalValue;

    private ArrayAdapter<CharSequence> charSequenceArrayAdapter;

    public DialogFragmentNutritionalValue() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        return inflater.inflate(R.layout.fragment_dialog_fragment_nutritional_value, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        imageButtonCancelNutritionalValue = view.findViewById(R.id.imageButtonCancelNutritionalValue);
        imageButtonSaveProductNutritionalValue = view.findViewById(R.id.imageButtonSaveProductNutritionalValue);

        editTextAMetricPortionNutritionalValue = view.findViewById(R.id.editTextAMetricPortionNutritionalValue);
        editTextCaloriesNutritionalValue = view.findViewById(R.id.editTextCaloriesNutritionalValue);
        editTextProteinNutritionalValue = view.findViewById(R.id.editTextProteinNutritionalValue);
        editTextCarbohydratesNutritionalValue = view.findViewById(R.id.editTextCarbohydratesNutritionalValue);
        editTextFatNutritionalValue = view.findViewById(R.id.editTextFatNutritionalValue);

        textViewUnitNutritionalValue = view.findViewById(R.id.textViewUnitNutritionalValue);

        spinnerNutritionalValue = view.findViewById(R.id.spinnerNutritionalValue);

        imageButtonCancelNutritionalValue.setOnClickListener(this);
        imageButtonSaveProductNutritionalValue.setOnClickListener(this);
        spinnerNutritionalValue.setOnItemSelectedListener(this);

        charSequenceArrayAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.units, android.R.layout.simple_spinner_item);
        charSequenceArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerNutritionalValue.setAdapter(charSequenceArrayAdapter);

        editTextCaloriesNutritionalValue.setFilters(new InputFilter[] {new InputFilterNewProduct(4, 2)});
        editTextProteinNutritionalValue.setFilters(new InputFilter[] {new InputFilterNewProduct(4, 2)});
        editTextCarbohydratesNutritionalValue.setFilters(new InputFilter[] {new InputFilterNewProduct(4, 2)});
        editTextFatNutritionalValue.setFilters(new InputFilter[] {new InputFilterNewProduct(4, 2)});
    }

    @Override
    public void onClick(View view) {

        int id = view.getId();

        if(id == R.id.imageButtonCancelNutritionalValue) {

            dismiss();

        } else if(id == R.id.imageButtonSaveProductNutritionalValue) {

            if((editTextCaloriesNutritionalValue.getText().toString().equals(""))
                    || (editTextProteinNutritionalValue.getText().toString().equals(""))
                    || (editTextCarbohydratesNutritionalValue.getText().toString().equals(""))
                    || (editTextFatNutritionalValue.getText().toString().equals(""))) {

                Toast.makeText(getActivity(), "You haven't entered all the values", Toast.LENGTH_SHORT).show();

            } else if ((Float.parseFloat(editTextCaloriesNutritionalValue.getText().toString()) > 10000)
                    || (Float.parseFloat(editTextProteinNutritionalValue.getText().toString()) > 10000)
                    || (Float.parseFloat(editTextCarbohydratesNutritionalValue.getText().toString()) > 10000)
                    || (Float.parseFloat(editTextFatNutritionalValue.getText().toString()) > 10000)){

                Toast.makeText(getActivity(), "Calories, protein, carbohydrates and fat mustn't exceed 10,000 per 100g of product", Toast.LENGTH_LONG).show();

            } else {

                String unit = textViewUnitNutritionalValue.getText().toString();
                float calories = Float.parseFloat(editTextCaloriesNutritionalValue.getText().toString());
                float protein = Float.parseFloat(editTextProteinNutritionalValue.getText().toString());
                float carbohydrates = Float.parseFloat(editTextCarbohydratesNutritionalValue.getText().toString());
                float fat = Float.parseFloat(editTextFatNutritionalValue.getText().toString());

                Intent intent = new Intent();

                intent.putExtra("Weight", 100);
                intent.putExtra("Unit", unit);
                intent.putExtra("Calories", calories);
                intent.putExtra("Protein", protein);
                intent.putExtra("Carbohydrates", carbohydrates);
                intent.putExtra("Fat", fat);

                getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, intent);
                dismiss();
            }
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        textViewUnitNutritionalValue.setText(charSequenceArrayAdapter.getItem(position).toString());
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
