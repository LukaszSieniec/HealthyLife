package com.example.healthylife;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.example.diary.MainScreen;
import com.example.registration.RegistrationProcessActivity;
import com.example.signin.SignIn;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class StartNowActivity extends AppCompatActivity implements View.OnClickListener {

    private Button buttonStartNow;
    private TextView textViewSignIn;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_now_activity);

        buttonStartNow = findViewById(R.id.buttonStartNow);
        textViewSignIn = findViewById(R.id.textViewSignIn);

        buttonStartNow.setOnClickListener(this);
        textViewSignIn.setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();

    }

    @Override
    public void onClick(View view) {

        int id = view.getId();

        if(id == R.id.buttonStartNow) {

            Intent intent = new Intent(StartNowActivity.this, RegistrationProcessActivity.class);
            startActivity(intent);

        } else if(id == R.id.textViewSignIn) {

            Intent intent = new Intent(StartNowActivity.this, SignIn.class);
            startActivity(intent);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser != null) {

            Intent intent = new Intent(StartNowActivity.this, MainScreen.class);
            startActivity(intent);
        }
    }

    @Override
    public void onBackPressed() {

        Intent intent = new Intent(Intent.ACTION_MAIN);

        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        startActivity(intent);
    }
}
