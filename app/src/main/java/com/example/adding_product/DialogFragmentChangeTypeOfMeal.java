package com.example.adding_product;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import com.example.healthylife.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class DialogFragmentChangeTypeOfMeal extends DialogFragment implements View.OnClickListener {

    private Button buttonBreakfastChangeTypeOfMeal, buttonLunchChangeTypeOfMeal, buttonDinnerChangeTypeOfMeal, buttonSnacksChangeTypeOfMeal;

    private String typeOfMeal = "";
    private OnChangeTypeOfMealDialogFragment onChangeTypeOfMealDialogFragment;


    public DialogFragmentChangeTypeOfMeal() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        onChangeTypeOfMealDialogFragment = (OnChangeTypeOfMealDialogFragment) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        if(getArguments() != null) {

            typeOfMeal = getArguments().getString("meal");
        }

        return inflater.inflate(R.layout.fragment_dialog_fragment_change_type_of_meal, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        buttonBreakfastChangeTypeOfMeal = view.findViewById(R.id.buttonBreakfastChangeTypeOfMeal);
        buttonLunchChangeTypeOfMeal = view.findViewById(R.id.buttonLunchChangeTypeOfMeal);
        buttonDinnerChangeTypeOfMeal = view.findViewById(R.id.buttonDinnerChangeTypeOfMeal);
        buttonSnacksChangeTypeOfMeal = view.findViewById(R.id.buttonSnacksChangeTypeOfMeal);

        buttonBreakfastChangeTypeOfMeal.setOnClickListener(this);
        buttonLunchChangeTypeOfMeal.setOnClickListener(this);
        buttonDinnerChangeTypeOfMeal.setOnClickListener(this);
        buttonSnacksChangeTypeOfMeal.setOnClickListener(this);

        switch(typeOfMeal) {

            case "Breakfast":
                selectedTypeOfMeal(buttonBreakfastChangeTypeOfMeal);
                break;

            case "Lunch":
                selectedTypeOfMeal(buttonLunchChangeTypeOfMeal);
                break;

            case "Dinner":
                selectedTypeOfMeal(buttonDinnerChangeTypeOfMeal);
                break;

            case "Snacks":
                selectedTypeOfMeal(buttonSnacksChangeTypeOfMeal);
                break;
        }
    }

    @Override
    public void onClick(View view) {

        int id = view.getId();

        switch (id) {

            case R.id.buttonBreakfastChangeTypeOfMeal:

                onChangeTypeOfMealDialogFragment.changeTypeOfMeal("Breakfast");
                dismiss();
                break;

            case R.id.buttonLunchChangeTypeOfMeal:

                onChangeTypeOfMealDialogFragment.changeTypeOfMeal("Lunch");
                dismiss();
                break;

            case R.id.buttonDinnerChangeTypeOfMeal:

                onChangeTypeOfMealDialogFragment.changeTypeOfMeal("Dinner");
                dismiss();
                break;

            case R.id.buttonSnacksChangeTypeOfMeal:

                onChangeTypeOfMealDialogFragment.changeTypeOfMeal("Snacks");
                dismiss();
                break;

        }
    }

    public interface OnChangeTypeOfMealDialogFragment {

        void changeTypeOfMeal(String typeOfMeal);
    }

    private void selectedTypeOfMeal(Button selectedButton) {

        selectedButton.setTextColor(ContextCompat.getColor(getActivity(), R.color.backgroundScreens));
        selectedButton.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_check_black_24dp, 0);
    }
}
