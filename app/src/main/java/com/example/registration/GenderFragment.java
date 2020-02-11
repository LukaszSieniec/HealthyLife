package com.example.registration;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import static com.example.user.User.getInstance;
import com.example.user.User;
import com.example.healthylife.R;
import com.mikhaellopez.circularimageview.CircularImageView;
import static com.example.registration.RegistrationProcessActivity.nextPage;

/**
 * A simple {@link Fragment} subclass.
 */
public class GenderFragment extends Fragment implements View.OnClickListener, UpdateButton {

    private User newUser;

    private CircularImageView imageViewWoman, imageViewMan;
    private TextView textViewManOrWoman;
    private ImageButton imageButtonNextGender;

    public GenderFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_gender, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        newUser = getInstance();

        imageViewWoman = view.findViewById(R.id.imageViewWoman);
        imageViewMan = view.findViewById(R.id.imageViewMan);

        textViewManOrWoman = view.findViewById(R.id.textViewManOrWoman);

        imageButtonNextGender = view.findViewById(R.id.imageButtonNextGender);

        imageViewWoman.setOnClickListener(this);
        imageViewMan.setOnClickListener(this);
        imageButtonNextGender.setOnClickListener(this);

        if(newUser.getGender().equals("")) {

            lockButton();

        } else {

            textViewManOrWoman.setText(newUser.getGender());
            setAlphaSelectedImage(newUser.getGender());

            unlockButton();
        }
    }

    private void selectedWoman() {

        imageViewMan.setAlpha(0.2f);
        imageViewWoman.setAlpha(1.0f);

        newUser.setGender("Woman");
        textViewManOrWoman.setText("Woman");
    }

    private void selectedMan() {

        imageViewWoman.setAlpha(0.2f);
        imageViewMan.setAlpha(1.0f);

        newUser.setGender("Man");
        textViewManOrWoman.setText("Man");
    }

    @Override
    public void onClick(View view) {

        int id = view.getId();

        if(id == R.id.imageViewWoman) {

            selectedWoman();
            unlockButton();

        } else if(id == R.id.imageViewMan) {

            selectedMan();
            unlockButton();

        } else if(id == R.id.imageButtonNextGender) {

            nextPage();
        }
    }

    @Override
    public void unlockButton() {

        imageButtonNextGender.setClickable(true);
        imageButtonNextGender.setAlpha(1.0f);
    }

    @Override
    public void lockButton() {

        imageButtonNextGender.setClickable(false);
        imageButtonNextGender.setAlpha(0.2f);
    }

    private void setAlphaSelectedImage(String gender) {

        if(gender.equals("Woman")) {

            imageViewWoman.setAlpha(1.0f);

        } else if(gender.equals("Man")) {

            imageViewMan.setAlpha(1.0f);
        }
    }
}
