package com.example.registration;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import static com.example.registration.RegistrationProcessActivity.nextPage;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import com.example.healthylife.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class InformationFragment extends Fragment implements View.OnClickListener {

    private ImageButton imageButtonNextInformation;

    public InformationFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_information, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        imageButtonNextInformation = view.findViewById(R.id.imageButtonNextInformation);

        imageButtonNextInformation.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {

        int id = view.getId();

        if(id == R.id.imageButtonNextInformation) {

            nextPage();
        }
    }
}
