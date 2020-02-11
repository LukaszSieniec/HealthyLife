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
import android.widget.Toast;
import com.example.format_numbers.FormatNumbers;
import com.example.healthylife.R;
import com.example.user.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class DialogFragmentTargetWeight extends DialogFragment implements View.OnClickListener, View.OnTouchListener {

    private ImageButton imageButtonCancelTargetWeight, imageButtonSaveTargetWeight;
    private Button buttonDecreaseTargetWeight,buttonIncreaseTargetWeight;
    private EditText editTextChangeTargetWeight;

    private float targetWeight = 0;

    private float oldWeight = 0;
    private float newWeight = 0;
    private long startTime = 0;

    private int CLICK_ACTION_THRESHOLD = 200;
    private float startX;
    private float startY;

    private User user = User.getInstance();
    private FirebaseFirestore firebaseFirestore;

    private RefreshTargetWeight refreshTargetWeight;

    public DialogFragmentTargetWeight() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        if(getArguments() != null) {

            targetWeight = getArguments().getFloat("targetWeight");
        }

        refreshTargetWeight = (RefreshTargetWeight)getTargetFragment();

        return inflater.inflate(R.layout.fragment_dialog_fragment_target_weight, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        imageButtonCancelTargetWeight = view.findViewById(R.id.imageButtonCancelTargetWeight);
        imageButtonSaveTargetWeight = view.findViewById(R.id.imageButtonSaveTargetWeight);

        buttonDecreaseTargetWeight = view.findViewById(R.id.buttonDecreaseTargetWeight);
        buttonIncreaseTargetWeight = view.findViewById(R.id.buttonIncreaseTargetWeight);

        editTextChangeTargetWeight = view.findViewById(R.id.editTextChangeTargetWeight);
        editTextChangeTargetWeight.setFilters(new InputFilter[] {new WeightInputFilter(3, 1)});

        imageButtonCancelTargetWeight.setOnClickListener(this);
        imageButtonSaveTargetWeight.setOnClickListener(this);

        buttonDecreaseTargetWeight.setOnTouchListener(this);
        buttonIncreaseTargetWeight.setOnTouchListener(this);

        editTextChangeTargetWeight.setText(FormatNumbers.formatNumberWithOneDecimalPlace(targetWeight).replace(",", "."));

        firebaseFirestore = FirebaseFirestore.getInstance();
    }

    @Override
    public void onClick(View view) {

        int id = view.getId();

        if(id == R.id.imageButtonCancelTargetWeight) {

            dismiss();

        } else if(id == R.id.imageButtonSaveTargetWeight) {

            if(!editTextChangeTargetWeight.getText().toString().equals("")) {

                Map<String, Float> targetWeight = new HashMap<>();
                targetWeight.put("targetWeight", Float.parseFloat(editTextChangeTargetWeight.getText().toString()));

                firebaseFirestore.collection("Users").document(user.getCurrentUserUID()).set(targetWeight, SetOptions.merge())
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if(task.isSuccessful()) {

                            Toast.makeText(getActivity(), "You have entered a new target weight", Toast.LENGTH_LONG).show();

                            if(getRefreshTargetWeight() != null) {

                                refreshTargetWeight.refreshTargetWeight(Float.parseFloat(editTextChangeTargetWeight.getText().toString()));
                            }

                            dismiss();
                        }
                    }
                });

            } else {

                Toast.makeText(getActivity(), "You haven't entered your target weight!", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {

        int id = view.getId();

        if (!editTextChangeTargetWeight.getText().toString().equals("")) {

            oldWeight = Float.parseFloat(editTextChangeTargetWeight.getText().toString().replace(",", "."));
            newWeight = 0;

            if(id == R.id.buttonDecreaseTargetWeight) {

                if(oldWeight > 0) {

                    changeTargetWeight(event, "subtract");
                }

            } else if(id == R.id.buttonIncreaseTargetWeight) {

                changeTargetWeight(event, "add");
            }
        }

        return true;
    }

    private void changeTargetWeight(MotionEvent event, String operation) {

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
                        editTextChangeTargetWeight.setText(FormatNumbers.formatNumberWithOneDecimalPlace(newWeight).replace(",", "."));

                    } else if(operation.equals("add")) {

                        newWeight = oldWeight + 0.1f;
                        editTextChangeTargetWeight.setText(FormatNumbers.formatNumberWithOneDecimalPlace(newWeight).replace(",", "."));
                    }
                }

                break;

            case MotionEvent.ACTION_UP:

                float endX = event.getX();
                float endY = event.getY();

                if (isAClick(startX, endX, startY, endY)) {

                    if(operation.equals("subtract")) {

                        newWeight = oldWeight - 0.1f;
                        editTextChangeTargetWeight.setText(FormatNumbers.formatNumberWithOneDecimalPlace(newWeight).replace(",", "."));

                    } else if(operation.equals("add")) {

                        newWeight = oldWeight + 0.1f;
                        editTextChangeTargetWeight.setText(FormatNumbers.formatNumberWithOneDecimalPlace(newWeight).replace(",", "."));
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

    public interface RefreshTargetWeight {

        void refreshTargetWeight(float targetWeight);
    }

    public RefreshTargetWeight getRefreshTargetWeight() {
        return refreshTargetWeight;
    }
}
