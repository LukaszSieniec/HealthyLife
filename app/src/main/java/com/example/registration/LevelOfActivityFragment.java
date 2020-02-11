package com.example.registration;

import android.graphics.Typeface;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import com.example.user.User;
import com.example.healthylife.R;
import static com.example.user.User.getInstance;
import static com.example.registration.RegistrationProcessActivity.nextPage;

/**
 * A simple {@link Fragment} subclass.
 */
public class LevelOfActivityFragment extends Fragment implements View.OnClickListener, UpdateButton {

    private User newUser;

    private Button buttonVeryLow, buttonLow, buttonMedium, buttonHigh, buttonVeryHigh;
    private ImageButton imageButtonNextLevelOfActivity;


    public LevelOfActivityFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_level_of_activity, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        newUser = getInstance();

        buttonVeryLow = view.findViewById(R.id.buttonVeryLow);
        buttonLow = view.findViewById(R.id.buttonLow);
        buttonMedium = view.findViewById(R.id.buttonMedium);
        buttonHigh = view.findViewById(R.id.buttonHigh);
        buttonVeryHigh = view.findViewById(R.id.buttonVeryHigh);

        imageButtonNextLevelOfActivity = view.findViewById(R.id.imageButtonNextLevelOfActivity);

        buttonVeryLow.setOnClickListener(this);
        buttonLow.setOnClickListener(this);
        buttonMedium.setOnClickListener(this);
        buttonHigh.setOnClickListener(this);
        buttonVeryHigh.setOnClickListener(this);
        imageButtonNextLevelOfActivity.setOnClickListener(this);

        buttonVeryLow.setText(customHeadline(buttonVeryLow.getText().toString(), 0, 8));
        buttonLow.setText(customHeadline(buttonLow.getText().toString(), 0, 3));
        buttonMedium.setText(customHeadline(buttonMedium.getText().toString(), 0, 6));
        buttonHigh.setText(customHeadline(buttonHigh.getText().toString(), 0, 4));
        buttonVeryHigh.setText(customHeadline(buttonVeryHigh.getText().toString(), 0, 9));


        if(newUser.getLevelOfActivity().equals("")) {

            lockButton();

        } else {

            selectedLevelOfActivity(newUser.getLevelOfActivity());
            unlockButton();
        }

    }

    @Override
    public void onClick(View view) {

        int id = view.getId();

        switch(id) {

            case R.id.buttonVeryLow:

                selectedLevelOfActivity(buttonVeryLow, buttonLow, buttonMedium, buttonHigh, buttonVeryHigh);
                newUser.setLevelOfActivity("Very Low");
                unlockButton();
                break;

            case R.id.buttonLow:

                selectedLevelOfActivity(buttonLow, buttonVeryLow, buttonMedium, buttonHigh, buttonVeryHigh);
                newUser.setLevelOfActivity("Low");
                unlockButton();
                break;

            case R.id.buttonMedium:

                selectedLevelOfActivity(buttonMedium, buttonVeryLow, buttonLow, buttonHigh, buttonVeryHigh);
                newUser.setLevelOfActivity("Medium");
                unlockButton();
                break;

            case R.id.buttonHigh:

                selectedLevelOfActivity(buttonHigh, buttonVeryLow, buttonLow, buttonMedium, buttonVeryHigh);
                newUser.setLevelOfActivity("High");
                unlockButton();
                break;

            case R.id.buttonVeryHigh:

                selectedLevelOfActivity(buttonVeryHigh, buttonVeryLow, buttonLow, buttonMedium, buttonHigh);
                newUser.setLevelOfActivity("Very High");
                unlockButton();
                break;

            case R.id.imageButtonNextLevelOfActivity:

                nextPage();
                break;
        }
    }

    private SpannableString customHeadline(String text, int start, int end) {

        SpannableString spannableString = new SpannableString(text);

        StyleSpan boldSpan = new StyleSpan(Typeface.BOLD);

        spannableString.setSpan(boldSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new RelativeSizeSpan(1.25f), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        return spannableString;
    }

    private void selectedLevelOfActivity(Button selected, Button unselectedFirst, Button unselectedSecond,
                                         Button unselectedThird, Button unselectedFourth) {

        selected.setBackgroundResource(R.drawable.selected_button_bg_rectangle);
        selected.setTextColor(ContextCompat.getColor(getActivity(), R.color.black));

        unselectedFirst.setBackgroundResource(R.drawable.unselected_button_bg_rectangle);
        unselectedFirst.setTextColor(ContextCompat.getColor(getActivity(), R.color.white));

        unselectedSecond.setBackgroundResource(R.drawable.unselected_button_bg_rectangle);
        unselectedSecond.setTextColor(ContextCompat.getColor(getActivity(), R.color.white));

        unselectedThird.setBackgroundResource(R.drawable.unselected_button_bg_rectangle);
        unselectedThird.setTextColor(ContextCompat.getColor(getActivity(), R.color.white));

        unselectedFourth.setBackgroundResource(R.drawable.unselected_button_bg_rectangle);
        unselectedFourth.setTextColor(ContextCompat.getColor(getActivity(), R.color.white));

    }

    private void selectedLevelOfActivity (String levelOfActivity) {

        switch(levelOfActivity) {

            case "Very Low":

                customSelectedButton(buttonVeryLow);
                break;

            case "Low":

                customSelectedButton(buttonLow);
                break;

            case "Medium":

                customSelectedButton(buttonMedium);
                break;

            case "High":

                customSelectedButton(buttonHigh);
                break;

            case "Very High":

                customSelectedButton(buttonVeryHigh);
                break;
        }
    }

    private void customSelectedButton(Button button) {

        button.setBackgroundResource(R.drawable.selected_button_bg_rectangle);
        button.setTextColor(ContextCompat.getColor(getActivity(), R.color.black));
    }

    @Override
    public void unlockButton() {

        imageButtonNextLevelOfActivity.setClickable(true);
        imageButtonNextLevelOfActivity.setAlpha(1.0f);
    }

    @Override
    public void lockButton() {

        imageButtonNextLevelOfActivity.setClickable(false);
        imageButtonNextLevelOfActivity.setAlpha(0.2f);
    }
}
