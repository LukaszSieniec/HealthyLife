package com.example.adding_product;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import com.example.healthylife.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class DialogFragmentNameProduct extends DialogFragment implements View.OnClickListener {

    private TextView textViewEnterNameProductDialogFragmentNameProduct;
    private EditText editTextEnterNameProduct;

    private ImageButton imageButtonCancelProductName, imageButtonSaveProductName;

    public DialogFragmentNameProduct() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_dialog_fragment_name_product, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        textViewEnterNameProductDialogFragmentNameProduct = view.findViewById(R.id.textViewEnterNameProductDialogFragmentNameProduct);

        editTextEnterNameProduct = view.findViewById(R.id.editTextEnterNameProduct);

        imageButtonCancelProductName = view.findViewById(R.id.imageButtonCancelProductName);
        imageButtonSaveProductName = view.findViewById(R.id.imageButtonSaveProductName);

        imageButtonCancelProductName.setOnClickListener(this);
        imageButtonSaveProductName.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {

        int id = view.getId();

        if(id == R.id.imageButtonCancelProductName) {

            dismiss();

        } else if(id == R.id.imageButtonSaveProductName) {

            if(editTextEnterNameProduct.getText().toString().equals("")) {

                Toast.makeText(getActivity(), "You haven't entered the product name", Toast.LENGTH_SHORT).show();

            } else {

                String productName = editTextEnterNameProduct.getText().toString();

                Intent intent = new Intent();
                intent.putExtra("productName", productName);

                getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, intent);
                dismiss();
            }
        }
    }
}