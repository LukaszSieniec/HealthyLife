package com.example.diary;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.healthylife.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class BodyCircuitsFragment extends Fragment {


    public BodyCircuitsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_body_circuits, container, false);
    }

}
