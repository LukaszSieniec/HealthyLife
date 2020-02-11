package com.example.registration;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import com.example.user.User;
import com.example.healthylife.R;
import static com.example.user.User.getInstance;
import static com.example.registration.RegistrationProcessActivity.nextPage;

/**
 * A simple {@link Fragment} subclass.
 */
public class WeightFragment extends Fragment implements View.OnClickListener, UpdateButton {

    private User newUser;
    private float currentWeight;

    private EditText editTextEnterCurrentWeight;
    private ImageButton imageButtonNextWeight;


    public WeightFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_weight, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        newUser = getInstance();

        editTextEnterCurrentWeight = view.findViewById(R.id.editTextEnterCurrentWeight);
        editTextEnterCurrentWeight.setFilters(new InputFilter[] {new DecimalDigitsInputFilter(3, 2)});

        imageButtonNextWeight = view.findViewById(R.id.imageButtonNextWeight);

        imageButtonNextWeight.setOnClickListener(this);

        if(newUser.getWeight() == 0) {

            lockButton();

        } else {

            editTextEnterCurrentWeight.setText(String.valueOf(newUser.getWeight()));
            unlockButton();
        }

        editTextEnterCurrentWeight.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if((charSequence.toString().length() > 0)) {

                    unlockButton();

                } else {

                    lockButton();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {


            }
        });
    }

    @Override
    public void onClick(View view) {

        int id = view.getId();

        if(id == R.id.imageButtonNextWeight) {

            //Additional protection despite the use of regex expressions in the event of an unexpected incident
            try {

                currentWeight = Float.parseFloat(editTextEnterCurrentWeight.getText().toString());

            } catch (NumberFormatException exc) {

                Toast.makeText(getActivity(), "You entered the invalid number, please try again!", Toast.LENGTH_LONG).show();
            }

            newUser.setWeight(currentWeight);

            InputMethodManager inputMethodManager = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);

            nextPage();
        }
    }

    @Override
    public void unlockButton() {

        imageButtonNextWeight.setClickable(true);
        imageButtonNextWeight.setAlpha(1.0f);
    }

    @Override
    public void lockButton() {

        imageButtonNextWeight.setClickable(false);
        imageButtonNextWeight.setAlpha(0.2f);
    }
}
