package com.example.signin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import com.example.diary.MainScreen;
import com.example.healthylife.R;
import com.example.healthylife.StartNowActivity;
import com.example.registration.RegistrationProcessActivity;
import com.example.registration.UpdateButton;
import com.example.user.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import java.util.regex.Pattern;

public class SignIn extends AppCompatActivity implements View.OnClickListener, UpdateButton {

    private FirebaseAuth mAuth;

    private EditText editTextEmailSignIn, editTextPasswordSignIn;
    private ImageButton imageButtonBackSignIn, imageButtonNextSignIn;
    private TextView textViewSignUpSecond;

    private boolean correctEmailSignIn = false;
    private boolean correctPasswordSignIn = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        User user = User.getInstance();
        user.resetAllValues();

        mAuth = FirebaseAuth.getInstance();

        editTextEmailSignIn = findViewById(R.id.editTextEmailSignIn);
        editTextPasswordSignIn = findViewById(R.id.editTextPasswordSignIn);

        imageButtonBackSignIn = findViewById(R.id.imageButtonBackSignIn);
        imageButtonNextSignIn = findViewById(R.id.imageButtonNextSignIn);

        textViewSignUpSecond = findViewById(R.id.textViewSignUpSecond);

        imageButtonBackSignIn.setOnClickListener(this);
        imageButtonNextSignIn.setOnClickListener(this);
        textViewSignUpSecond.setOnClickListener(this);

        lockButton();

        editTextEmailSignIn.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {

                if(isValidEmailSignIn(charSequence.toString())) {

                    correctEmailSignIn = true;

                } else {

                    correctEmailSignIn = false;
                }

                if ((correctEmailSignIn) && (correctPasswordSignIn)) {

                    unlockButton();

                } else {

                    lockButton();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        editTextPasswordSignIn.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {

                if(charSequence.length() > 5) {

                    correctPasswordSignIn = true;

                } else {

                    correctPasswordSignIn = false;
                }

                if ((correctEmailSignIn) && (correctPasswordSignIn)) {

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

        if(id == R.id.imageButtonBackSignIn) {

            Intent intent = new Intent(SignIn.this, StartNowActivity.class);
            startActivity(intent);

        } else if(id == R.id.textViewSignUpSecond) {

            Intent intent = new Intent(SignIn.this, RegistrationProcessActivity.class);
            startActivity(intent);

        } else if(id == R.id.imageButtonNextSignIn) {

            signIn();
        }
    }

    private void signIn() {

        mAuth.signInWithEmailAndPassword(editTextEmailSignIn.getText().toString(), editTextPasswordSignIn.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if(task.isSuccessful()) {

                            Intent intent = new Intent(SignIn.this, MainScreen.class);
                            startActivity(intent);

                        } else {

                            Log.e("Error", "Sign-in Failed: " + task.getException().getMessage());


                            try {

                                throw task.getException();

                            } catch (FirebaseAuthInvalidUserException exc) {

                                Toast.makeText(SignIn.this, "The e-mail address you provided does not exist.", Toast.LENGTH_LONG).show();

                            } catch(FirebaseAuthInvalidCredentialsException exc) {

                                Toast.makeText(SignIn.this, "You entered the password incorrectly.", Toast.LENGTH_LONG).show();

                            } catch (Exception exc) {

                                Toast.makeText(SignIn.this, "Login failed, try again!", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                });
    }

    private boolean isValidEmailSignIn(String email) {

        Pattern pattern = Patterns.EMAIL_ADDRESS;

        return pattern.matcher(email).matches();
    }

    @Override
    public void unlockButton() {

        imageButtonNextSignIn.setClickable(true);
        imageButtonNextSignIn.setAlpha(1.0f);
    }

    @Override
    public void lockButton() {

        imageButtonNextSignIn.setClickable(false);
        imageButtonNextSignIn.setAlpha(0.2f);
    }
}
