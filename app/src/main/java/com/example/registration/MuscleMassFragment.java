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
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import com.example.user.User;
import com.example.healthylife.R;
import static com.example.user.User.getInstance;
import static com.example.registration.RegistrationProcessActivity.nextPage;

/**
 * A simple {@link Fragment} subclass.
 */
public class MuscleMassFragment extends Fragment implements View.OnClickListener, UpdateButton {

    private User newUser;
    private float muscleMass;

    private EditText editTextEnterCurrentMuscleMass;
    private ImageButton imageButtonNextMuscleMass;
    private TextView textViewSecondSkip;

    public MuscleMassFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        return inflater.inflate(R.layout.fragment_muscle_mass, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        newUser = getInstance();

        editTextEnterCurrentMuscleMass = view.findViewById(R.id.editTextEnterCurrentMuscleMass);
        editTextEnterCurrentMuscleMass.setFilters(new InputFilter[] {new DecimalDigitsInputFilter(3, 2)});

        imageButtonNextMuscleMass = view.findViewById(R.id.imageButtonNextMuscleMass);

        textViewSecondSkip = view.findViewById(R.id.textViewSecondSkip);

        textViewSecondSkip.setOnClickListener(this);
        imageButtonNextMuscleMass.setOnClickListener(this);

        if(newUser.getMuscleMass() == 0) {

            lockButton();

        } else {

            editTextEnterCurrentMuscleMass.setText(String.valueOf(newUser.getMuscleMass()));
            unlockButton();
        }

        editTextEnterCurrentMuscleMass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {

                if((charSequence.toString().length() > 0)) {

                    unlockButton();

                } else {

                    lockButton();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    @Override
    public void onClick(View view) {

        int id = view.getId();

        if(id == R.id.imageButtonNextMuscleMass) {

            //Additional protection despite the use of regex expressions in the event of an unexpected incident
            try {

                muscleMass = Float.parseFloat(editTextEnterCurrentMuscleMass.getText().toString());

            } catch (NumberFormatException exc) {

                Toast.makeText(getActivity(), "You entered the invalid number, please try again!", Toast.LENGTH_LONG).show();
            }

            newUser.setMuscleMass(muscleMass);

            nextPage();

        } else if(id == R.id.textViewSecondSkip) {

            nextPage();
        }

        InputMethodManager inputMethodManager = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    public void unlockButton() {

        imageButtonNextMuscleMass.setClickable(true);
        imageButtonNextMuscleMass.setAlpha(1.0f);
    }

    @Override
    public void lockButton() {

        imageButtonNextMuscleMass.setClickable(false);
        imageButtonNextMuscleMass.setAlpha(0.2f);
    }
}
