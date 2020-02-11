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
public class DialogFragmentTargetMuscleMass extends DialogFragment implements View.OnClickListener, View.OnTouchListener {

    private ImageButton imageButtonCancelTargetMuscleMass, imageButtonSaveTargetMuscleMass;
    private Button buttonDecreaseTargetMuscleMass,buttonIncreaseTargetMuscleMass;
    private EditText editTextChangeTargetMuscleMass;

    private float targetMuscleMass = 0;

    private float oldMuscleMass = 0;
    private float newMuscleMass = 0;
    private long startTime = 0;

    private int CLICK_ACTION_THRESHOLD = 200;
    private float startX;
    private float startY;

    private User user = User.getInstance();
    private FirebaseFirestore firebaseFirestore;

    private RefreshTargetMuscleMass refreshTargetMuscleMass;

    public DialogFragmentTargetMuscleMass() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        if(getArguments() != null) {

            targetMuscleMass = getArguments().getFloat("targetMuscleMass");
        }

        refreshTargetMuscleMass = (RefreshTargetMuscleMass)getTargetFragment();

        return inflater.inflate(R.layout.fragment_dialog_fragment_target_muscle_mass, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        imageButtonCancelTargetMuscleMass = view.findViewById(R.id.imageButtonCancelTargetMuscleMass );
        imageButtonSaveTargetMuscleMass  = view.findViewById(R.id.imageButtonSaveTargetMuscleMass );

        buttonDecreaseTargetMuscleMass  = view.findViewById(R.id.buttonDecreaseTargetMuscleMass );
        buttonIncreaseTargetMuscleMass  = view.findViewById(R.id.buttonIncreaseTargetMuscleMass );

        editTextChangeTargetMuscleMass  = view.findViewById(R.id.editTextChangeTargetMuscleMass );
        editTextChangeTargetMuscleMass .setFilters(new InputFilter[] {new WeightInputFilter(3, 1)});

        imageButtonCancelTargetMuscleMass .setOnClickListener(this);
        imageButtonSaveTargetMuscleMass .setOnClickListener(this);

        buttonDecreaseTargetMuscleMass .setOnTouchListener(this);
        buttonIncreaseTargetMuscleMass .setOnTouchListener(this);

        editTextChangeTargetMuscleMass .setText(FormatNumbers.formatNumberWithOneDecimalPlace(targetMuscleMass ).replace(",", "."));

        firebaseFirestore = FirebaseFirestore.getInstance();

    }

    @Override
    public void onClick(View view) {

        int id = view.getId();

        if(id == R.id.imageButtonCancelTargetMuscleMass) {

            dismiss();

        } else if(id == R.id.imageButtonSaveTargetMuscleMass) {

            if(!editTextChangeTargetMuscleMass.getText().toString().equals("")) {

                Map<String, Float> targetMuscleMass= new HashMap<>();
                targetMuscleMass.put("targetMuscleMass", Float.parseFloat(editTextChangeTargetMuscleMass.getText().toString()));

                firebaseFirestore.collection("Users").document(user.getCurrentUserUID()).set(targetMuscleMass, SetOptions.merge())
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                if(task.isSuccessful()) {

                                    Toast.makeText(getActivity(), "You have entered a new target muscle mass", Toast.LENGTH_LONG).show();

                                    if(getRefreshTargetMuscleMass() != null) {

                                        refreshTargetMuscleMass.refreshTargetMuscleMass(Float.parseFloat(editTextChangeTargetMuscleMass.getText().toString()));
                                    }

                                    dismiss();
                                }
                            }
                        });

            } else {

                Toast.makeText(getActivity(), "You haven't entered your target muscle mass!", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {

        int id = view.getId();

        if (!editTextChangeTargetMuscleMass.getText().toString().equals("")) {

            oldMuscleMass = Float.parseFloat(editTextChangeTargetMuscleMass.getText().toString().replace(",", "."));
            newMuscleMass = 0;

            if(id == R.id.buttonDecreaseTargetMuscleMass) {

                if(oldMuscleMass > 0) {

                    changeTargetMuscleMass(event, "subtract");
                }

            } else if(id == R.id.buttonIncreaseTargetMuscleMass) {

                changeTargetMuscleMass(event, "add");
            }
        }

        return true;
    }

    private void changeTargetMuscleMass(MotionEvent event, String operation) {

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
                        editTextChangeTargetMuscleMass.setText(FormatNumbers.formatNumberWithOneDecimalPlace(newMuscleMass).replace(",", "."));

                    } else if(operation.equals("add")) {

                        newMuscleMass = oldMuscleMass + 0.1f;
                        editTextChangeTargetMuscleMass.setText(FormatNumbers.formatNumberWithOneDecimalPlace(newMuscleMass).replace(",", "."));
                    }
                }

                break;

            case MotionEvent.ACTION_UP:

                float endX = event.getX();
                float endY = event.getY();

                if (isAClick(startX, endX, startY, endY)) {

                    if(operation.equals("subtract")) {

                        newMuscleMass = oldMuscleMass - 0.1f;
                        editTextChangeTargetMuscleMass.setText(FormatNumbers.formatNumberWithOneDecimalPlace(newMuscleMass).replace(",", "."));

                    } else if(operation.equals("add")) {

                        newMuscleMass = oldMuscleMass + 0.1f;
                        editTextChangeTargetMuscleMass.setText(FormatNumbers.formatNumberWithOneDecimalPlace(newMuscleMass).replace(",", "."));
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

    public interface RefreshTargetMuscleMass {

        void refreshTargetMuscleMass(float targetMuscleMass);
    }

    public RefreshTargetMuscleMass getRefreshTargetMuscleMass() {
        return refreshTargetMuscleMass;
    }
}
