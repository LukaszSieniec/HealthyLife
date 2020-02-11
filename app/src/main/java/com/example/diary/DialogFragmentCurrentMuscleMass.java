package com.example.diary;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import com.example.format_numbers.FormatNumbers;
import com.example.healthylife.R;
import com.example.user.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class DialogFragmentCurrentMuscleMass extends DialogFragment implements View.OnClickListener, View.OnTouchListener {

    private ImageButton imageButtonCancelCurrentMuscleMass, imageButtonSaveCurrentMuscleMass;
    private TextView textViewDateCurrentMuscleMass;
    private Button buttonDecreaseMuscleMass, buttonIncreaseMuscleMass;
    private EditText editTextChangeCurrentMuscleMass;

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("d MMMM yyyy", Locale.ENGLISH);
    private String date = "";

    private float oldMuscleMass = 0;
    private float newMuscleMass = 0;
    private long startTime = 0;

    private float currentMuscleMass = 0;

    private int CLICK_ACTION_THRESHOLD = 200;
    private float startX;
    private float startY;

    private User user = User.getInstance();
    private FirebaseFirestore firebaseFirestore;

    private RefreshCurrentMuscleMass refreshCurrentMuscleMass;


    public DialogFragmentCurrentMuscleMass() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        if(getArguments() != null) {

            currentMuscleMass = getArguments().getFloat("currentMuscleMass");
        }

        refreshCurrentMuscleMass = (RefreshCurrentMuscleMass) getTargetFragment();

        return inflater.inflate(R.layout.fragment_dialog_fragment_current_muscle_mass, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        imageButtonCancelCurrentMuscleMass = view.findViewById(R.id.imageButtonCancelCurrentMuscleMass );
        imageButtonSaveCurrentMuscleMass  = view.findViewById(R.id.imageButtonSaveCurrentMuscleMass );

        textViewDateCurrentMuscleMass  = view.findViewById(R.id.textViewDateCurrentMuscleMass );

        buttonDecreaseMuscleMass  = view.findViewById(R.id.buttonDecreaseMuscleMass );
        buttonIncreaseMuscleMass  = view.findViewById(R.id.buttonIncreaseMuscleMass );

        editTextChangeCurrentMuscleMass  = view.findViewById(R.id.editTextChangeCurrentMuscleMass );
        editTextChangeCurrentMuscleMass .setFilters(new InputFilter[] {new WeightInputFilter(3, 1)});

        imageButtonCancelCurrentMuscleMass .setOnClickListener(this);
        imageButtonSaveCurrentMuscleMass .setOnClickListener(this);

        buttonDecreaseMuscleMass.setOnTouchListener(this);
        buttonIncreaseMuscleMass.setOnTouchListener(this);

        firebaseFirestore = FirebaseFirestore.getInstance();

        date = dateFormat.format(new Date());
        textViewDateCurrentMuscleMass .setText("Today - " + date);

        editTextChangeCurrentMuscleMass .setText(FormatNumbers.formatNumberWithOneDecimalPlace(currentMuscleMass).replace(",", "."));
    }

    @Override
    public void onClick(View view) {

        int id = view.getId();

        if(id == R.id.imageButtonCancelCurrentMuscleMass) {

            dismiss();

        } else if(id == R.id.imageButtonSaveCurrentMuscleMass) {

            if(!editTextChangeCurrentMuscleMass.getText().toString().equals("")) {

                Map<String, Object> newMuscleMass = new HashMap<>();
                newMuscleMass.put("Current Muscle Mass", Float.parseFloat(editTextChangeCurrentMuscleMass.getText().toString()));

                firebaseFirestore.collection("Users").document(user.getCurrentUserUID())
                        .collection("Muscle Mass Controller Date").document(date)
                        .set(newMuscleMass).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if(task.isSuccessful()) {

                            Toast.makeText(getActivity(), "You have entered a new muscle mass", Toast.LENGTH_LONG).show();

                            if(getRefreshCurrentMuscleMass() != null) {

                                refreshCurrentMuscleMass.refreshCurrentMuscleMass(Float.parseFloat(editTextChangeCurrentMuscleMass.getText().toString()), date);
                            }

                            dismiss();
                        }
                    }
                });

                HashMap<String, Float> muscleMassOnAGivenDay = new HashMap<>();
                muscleMassOnAGivenDay.put("Muscle Mass On A Given Day", Float.parseFloat(editTextChangeCurrentMuscleMass.getText().toString()));

                firebaseFirestore.collection("Users").document(user.getCurrentUserUID())
                        .collection("Information of the day").document("Muscle Mass")
                        .collection("Years").document(date.split(" ")[2])
                        .collection("Months").document(date.split(" ")[1])
                        .collection("Days").document(date.split(" ")[0]).set(muscleMassOnAGivenDay);

            } else {

                Toast.makeText(getActivity(), "You haven't entered your current muscle mass!", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {

        int id = view.getId();

        if (!editTextChangeCurrentMuscleMass.getText().toString().equals("")) {

            oldMuscleMass = Float.parseFloat(editTextChangeCurrentMuscleMass.getText().toString().replace(",", "."));
            newMuscleMass = 0;

            if(id == R.id.buttonDecreaseMuscleMass) {

                if(oldMuscleMass > 0) {

                    changeMuscleMass(event, "subtract");
                }

            } else if(id == R.id.buttonIncreaseMuscleMass) {

                changeMuscleMass(event, "add");
            }
        }

        return true;
    }

    private void changeMuscleMass(MotionEvent event, String operation) {

        switch (event.getAction()) {

            case MotionEvent.ACTION_DOWN:

                startX = event.getX();
                startY = event.getY();
                break;


            case MotionEvent.ACTION_MOVE:

                if(startTime == 0) {

                    startTime = System.currentTimeMillis();
                }

                if(System.currentTimeMillis() > startTime + 1000) {

                    if(operation.equals("subtract")) {

                        newMuscleMass = oldMuscleMass - 0.1f;
                        editTextChangeCurrentMuscleMass.setText(FormatNumbers.formatNumberWithOneDecimalPlace(newMuscleMass).replace(",", "."));

                    } else if(operation.equals("add")) {

                        newMuscleMass = oldMuscleMass + 0.1f;
                        editTextChangeCurrentMuscleMass.setText(FormatNumbers.formatNumberWithOneDecimalPlace(newMuscleMass).replace(",", "."));
                    }
                }

                break;

            case MotionEvent.ACTION_UP:

                float endX = event.getX();
                float endY = event.getY();

                if (isAClick(startX, endX, startY, endY)) {

                    if(operation.equals("subtract")) {

                        newMuscleMass = oldMuscleMass - 0.1f;
                        editTextChangeCurrentMuscleMass.setText(FormatNumbers.formatNumberWithOneDecimalPlace(newMuscleMass).replace(",", "."));

                    } else if(operation.equals("add")) {

                        newMuscleMass= oldMuscleMass + 0.1f;
                        editTextChangeCurrentMuscleMass.setText(FormatNumbers.formatNumberWithOneDecimalPlace(newMuscleMass).replace(",", "."));
                    }
                }

                startTime = 0;
                break;

        }
    }

    private boolean isAClick(float startX, float endX, float startY, float endY) {

        float differenceX = Math.abs(startX - endX);
        float differenceY = Math.abs(startY - endY);

        return !(differenceX > CLICK_ACTION_THRESHOLD || differenceY > CLICK_ACTION_THRESHOLD);
    }

    public interface RefreshCurrentMuscleMass {

        void refreshCurrentMuscleMass (float currentMuscleMass , String date);
    }

    public RefreshCurrentMuscleMass  getRefreshCurrentMuscleMass () {

        return refreshCurrentMuscleMass ;
    }
}
