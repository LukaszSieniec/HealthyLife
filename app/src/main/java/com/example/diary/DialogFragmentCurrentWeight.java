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
public class DialogFragmentCurrentWeight extends DialogFragment implements View.OnClickListener, View.OnTouchListener {

    private ImageButton imageButtonCancelCurrentWeight, imageButtonSaveCurrentWeight;
    private TextView textViewDateCurrentWeight;
    private Button buttonDecreaseWeight, buttonIncreaseWeight;
    private EditText editTextChangeCurrentWeight;

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("d MMMM yyyy", Locale.ENGLISH);
    private String date = "";

    private float oldWeight = 0;
    private float newWeight = 0;
    private long startTime = 0;

    private float currentWeight = 0;

    private int CLICK_ACTION_THRESHOLD = 200;
    private float startX;
    private float startY;

    private User user = User.getInstance();
    private FirebaseFirestore firebaseFirestore;

    private RefreshCurrentWeight refreshCurrentWeight;

    public DialogFragmentCurrentWeight() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        if(getArguments() != null) {

            currentWeight = getArguments().getFloat("currentWeight");
        }

        refreshCurrentWeight = (RefreshCurrentWeight) getTargetFragment();

        return inflater.inflate(R.layout.fragment_dialog_fragment_current_weight, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        imageButtonCancelCurrentWeight = view.findViewById(R.id.imageButtonCancelCurrentWeight);
        imageButtonSaveCurrentWeight = view.findViewById(R.id.imageButtonSaveCurrentWeight);

        textViewDateCurrentWeight = view.findViewById(R.id.textViewDateCurrentWeight);

        buttonDecreaseWeight = view.findViewById(R.id.buttonDecreaseWeight);
        buttonIncreaseWeight = view.findViewById(R.id.buttonIncreaseWeight);

        editTextChangeCurrentWeight = view.findViewById(R.id.editTextChangeCurrentWeight);
        editTextChangeCurrentWeight.setFilters(new InputFilter[] {new WeightInputFilter(3, 1)});

        imageButtonCancelCurrentWeight.setOnClickListener(this);
        imageButtonSaveCurrentWeight.setOnClickListener(this);

        buttonDecreaseWeight.setOnTouchListener(this);
        buttonIncreaseWeight.setOnTouchListener(this);

        firebaseFirestore = FirebaseFirestore.getInstance();

        date = dateFormat.format(new Date());
        textViewDateCurrentWeight.setText("Today - " + date);

        editTextChangeCurrentWeight.setText(FormatNumbers.formatNumberWithOneDecimalPlace(currentWeight).replace(",", "."));
    }

    @Override
    public void onClick(View view) {

        int id = view.getId();

        if(id == R.id.imageButtonCancelCurrentWeight) {

            dismiss();

        } else if(id == R.id.imageButtonSaveCurrentWeight) {

            if(!editTextChangeCurrentWeight.getText().toString().equals("")) {

                Map<String, Object> newWeight = new HashMap<>();
                newWeight.put("Current Weight", Float.parseFloat(editTextChangeCurrentWeight.getText().toString()));

                firebaseFirestore.collection("Users").document(user.getCurrentUserUID())
                        .collection("Weight Controller Date").document(date)
                        .set(newWeight).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if(task.isSuccessful()) {

                            Toast.makeText(getActivity(), "You have entered a new weight", Toast.LENGTH_LONG).show();

                            if(getRefreshCurrentWeight() != null) {

                                refreshCurrentWeight.refreshCurrentWeight(Float.parseFloat(editTextChangeCurrentWeight.getText().toString()), date);
                            }

                            dismiss();
                        }
                    }
                });

                HashMap<String, Float> weightOnAGivenDay = new HashMap<>();
                weightOnAGivenDay.put("Weight On A Given Day", Float.parseFloat(editTextChangeCurrentWeight.getText().toString()));

                firebaseFirestore.collection("Users").document(user.getCurrentUserUID())
                        .collection("Information of the day").document("Weight")
                        .collection("Years").document(date.split(" ")[2])
                        .collection("Months").document(date.split(" ")[1])
                        .collection("Days").document(date.split(" ")[0]).set(weightOnAGivenDay);

            } else {

                Toast.makeText(getActivity(), "You haven't entered your current weight!", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {

        int id = view.getId();

        if (!editTextChangeCurrentWeight.getText().toString().equals("")) {

            oldWeight = Float.parseFloat(editTextChangeCurrentWeight.getText().toString().replace(",", "."));
            newWeight = 0;

            if(id == R.id.buttonDecreaseWeight) {

                if(oldWeight > 0) {

                    changeWeight(event, "subtract");
                }

            } else if(id == R.id.buttonIncreaseWeight) {

                changeWeight(event, "add");
            }
        }

        return true;
    }

    private void changeWeight(MotionEvent event, String operation) {

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

                        newWeight = oldWeight - 0.1f;
                        editTextChangeCurrentWeight.setText(FormatNumbers.formatNumberWithOneDecimalPlace(newWeight).replace(",", "."));

                    } else if(operation.equals("add")) {

                        newWeight = oldWeight + 0.1f;
                        editTextChangeCurrentWeight.setText(FormatNumbers.formatNumberWithOneDecimalPlace(newWeight).replace(",", "."));
                    }
                }

                break;

            case MotionEvent.ACTION_UP:

                float endX = event.getX();
                float endY = event.getY();

                if (isAClick(startX, endX, startY, endY)) {

                    if(operation.equals("subtract")) {

                        newWeight = oldWeight - 0.1f;
                        editTextChangeCurrentWeight.setText(FormatNumbers.formatNumberWithOneDecimalPlace(newWeight).replace(",", "."));

                    } else if(operation.equals("add")) {

                        newWeight = oldWeight + 0.1f;
                        editTextChangeCurrentWeight.setText(FormatNumbers.formatNumberWithOneDecimalPlace(newWeight).replace(",", "."));
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

    public interface RefreshCurrentWeight {

        void refreshCurrentWeight(float currentWeight, String date);
    }

    public RefreshCurrentWeight getRefreshCurrentWeight() {

        return refreshCurrentWeight;
    }
}
