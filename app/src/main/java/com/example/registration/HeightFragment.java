package com.example.registration;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
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
public class HeightFragment extends Fragment implements View.OnClickListener, UpdateButton {

    private User newUser;
    private float height;
    private float bmi;

    private EditText editTextEnterHeight;
    private ImageButton imageButtonNextHeight;


    public HeightFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_height, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        newUser = getInstance();

        editTextEnterHeight = view.findViewById(R.id.editTextEnterHeight);
        editTextEnterHeight.setFilters(new InputFilter[] {new DecimalDigitsInputFilter(3, 2)});

        imageButtonNextHeight = view.findViewById(R.id.imageButtonNextHeight);

        imageButtonNextHeight.setOnClickListener(this);

        if(newUser.getHeight() == 0) {

            lockButton();

        } else {

            editTextEnterHeight.setText(String.valueOf(newUser.getHeight()));
            unlockButton();
        }

        editTextEnterHeight.addTextChangedListener(new TextWatcher() {
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

        if(id == R.id.imageButtonNextHeight) {

            //Additional protection despite the use of regex expressions in the event of an unexpected incident
            try {

                height = Float.parseFloat(editTextEnterHeight.getText().toString());

            } catch (NumberFormatException exc) {

                Toast.makeText(getActivity(), "You entered the invalid number, please try again!", Toast.LENGTH_LONG).show();
            }

            newUser.setHeight(height);

            if(calculateBMI() > 45) {

                createAlertDialog("\n" +
                        "Given your height, your current weight is very high and you should seek medical advice before starting any diet.\n" +
                        "\n" +
                        "Are you sure you want to continue?");

            } else if (calculateBMI() < 11) {

                createAlertDialog("\n" +
                        "Given your height, your current weight is very low and you should seek medical advice before starting any diet.\n" +
                        "\n" +
                        "Are you sure you want to continue?");

            } else {

                InputMethodManager inputMethodManager = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);

                nextPage();
            }
        }
    }

    @Override
    public void unlockButton() {

        imageButtonNextHeight.setClickable(true);
        imageButtonNextHeight.setAlpha(1.0f);
    }

    @Override
    public void lockButton() {

        imageButtonNextHeight.setClickable(false);
        imageButtonNextHeight.setAlpha(0.2f);
    }

    private void createAlertDialog(String message) {

        new AlertDialog.Builder(getActivity())
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Warning!")
                .setMessage(message)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        InputMethodManager inputMethodManager = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        inputMethodManager.hideSoftInputFromWindow(getView().getWindowToken(), 0);

                        nextPage();
                    }
                })

                .setNegativeButton("CANCEL", null).show();
    }

    private float calculateBMI() {

        bmi = newUser.getWeight() / (newUser.getHeight()/100 * newUser.getHeight() / 100);

        return bmi;
    }
}
