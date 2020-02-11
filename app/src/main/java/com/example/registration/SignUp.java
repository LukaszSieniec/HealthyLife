package com.example.registration;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import com.example.healthylife.StartNowActivity;
import com.example.user.User;
import com.example.healthylife.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.firestore.FirebaseFirestore;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;
import static com.example.user.User.getInstance;

/**
 * A simple {@link Fragment} subclass.
 */
public class SignUp extends Fragment implements View.OnClickListener, UpdateButton {

    private User newUser;
    private FirebaseAuth mAuth;
    private FirebaseFirestore firebaseFirestore;

    private EditText editTextEmailSignUp, editTextPasswordSignUp;
    private ImageButton imageButtonNextSignUp;

    private boolean correctEmailSignUp = false;
    private boolean correctPasswordSignUp = false;

    private String userUid = "";

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("d MMMM yyyy", Locale.ENGLISH);
    private String date = "";

    private ProgressDialog progressDialog;

    public SignUp() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
        return inflater.inflate(R.layout.fragment_sign_up, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        newUser = getInstance();
        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        editTextEmailSignUp = view.findViewById(R.id.editTextEmailSignUp);
        editTextPasswordSignUp = view.findViewById(R.id.editTextPasswordSignUp);

        imageButtonNextSignUp = view.findViewById(R.id.imageButtonNextSignUp);

        imageButtonNextSignUp.setOnClickListener(this);

        date = dateFormat.format(new Date());

        lockButton();

        editTextEmailSignUp.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {

                if (isValidEmailSignUp(charSequence.toString())) {

                    correctEmailSignUp  = true;

                } else {

                    correctEmailSignUp  = false;
                }

                if ((correctEmailSignUp) && (correctPasswordSignUp)) {

                    unlockButton();

                } else {

                    lockButton();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        editTextPasswordSignUp.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {

                if (charSequence.toString().length() > 5) {

                    correctPasswordSignUp  = true;

                } else {

                    correctPasswordSignUp  = false;
                }

                if ((correctEmailSignUp ) && (correctPasswordSignUp )) {

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

    private boolean isValidEmailSignUp(String email) {

        Pattern pattern = Patterns.EMAIL_ADDRESS;

        return pattern.matcher(email).matches();
    }

    @Override
    public void onClick(View view) {

        int id = view.getId();

        if (id == R.id.imageButtonNextSignUp) {

            newUser.setEmail(editTextEmailSignUp.getText().toString());

            signUp();

        }
    }

    @Override
    public void unlockButton() {

        imageButtonNextSignUp.setClickable(true);
        imageButtonNextSignUp.setAlpha(1.0f);
    }

    @Override
    public void lockButton() {

        imageButtonNextSignUp.setClickable(false);
        imageButtonNextSignUp.setAlpha(0.2f);
    }

    private void signUp () {

        mAuth.createUserWithEmailAndPassword(editTextEmailSignUp.getText().toString(), editTextPasswordSignUp.getText().toString())
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if(task.isSuccessful()) {

                            userUid = task.getResult().getUser().getUid();

                            saveUserData();
                            saveWeightAndMuscleMassController();
                            saveWeightOnAGivenDay();
                            saveMuscleMassOnAGivenDay();

                            Intent intent = new Intent(getActivity(), StartNowActivity.class);
                            startActivity(intent);

                            Toast.makeText(getActivity(), "Registration was successful!", Toast.LENGTH_LONG).show();

                        } else {

                            try {

                                throw task.getException();

                            } catch (FirebaseAuthUserCollisionException exc) {

                                Toast.makeText(getActivity(), "E-mail adress: " + editTextEmailSignUp.getText().toString() + " is already used.", Toast.LENGTH_LONG).show();

                            } catch (Exception exc) {

                                Toast.makeText(getActivity(), "Sign Up failed, please try again!", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                });
    }

    private void saveUserData() {

        Map<String, Object> user = new HashMap<>();

        user.put("accountName", newUser.getAccountName());
        user.put("gender", newUser.getGender());
        user.put("dateOfBirth", newUser.getDateOfBirth());
        user.put("levelOfActivity", newUser.getLevelOfActivity());
        user.put("age", newUser.getAge());
        user.put("weight", newUser.getWeight());
        user.put("muscleMass", newUser.getMuscleMass());
        user.put("height", newUser.getHeight());
        user.put("targetWeight", 0);
        user.put("targetMuscleMass", 0);

        firebaseFirestore.collection("Users").document(userUid).set(user);

    }

    private void saveWeightAndMuscleMassController() {

        Map<String, Object> newWeight = new HashMap<>();
        newWeight.put("Current Weight", newUser.getWeight());

        firebaseFirestore.collection("Users").document(userUid)
                .collection("Weight Controller Date").document(date).set(newWeight);

        Map<String, Object> newMuscleMass = new HashMap<>();
        newMuscleMass.put("Current Muscle Mass", newUser.getMuscleMass());

        firebaseFirestore.collection("Users").document(userUid)
                .collection("Muscle Mass Controller Date").document(date).set(newMuscleMass);
    }

    private void saveWeightOnAGivenDay() {

        Map<String, Float> weightOnAGivenDay = new HashMap<>();
        weightOnAGivenDay.put("Weight On A Given Day", newUser.getWeight());

        firebaseFirestore.collection("Users").document(userUid)
                .collection("Information of the day").document("Weight")
                .collection("Years").document(date.split(" ")[2])
                .collection("Months").document(date.split(" ")[1])
                .collection("Days").document(date.split(" ")[0])
                .set(weightOnAGivenDay);
    }

    private void saveMuscleMassOnAGivenDay() {

        Map<String, Float> muscleMassOnAGivenDay = new HashMap<>();
        muscleMassOnAGivenDay.put("Muscle Mass On A Given Day", newUser.getMuscleMass());

        firebaseFirestore.collection("Users").document(userUid)
                .collection("Information of the day").document("Muscle Mass")
                .collection("Years").document(date.split(" ")[2])
                .collection("Months").document(date.split(" ")[1])
                .collection("Days").document(date.split(" ")[0])
                .set(muscleMassOnAGivenDay);
    }
}

