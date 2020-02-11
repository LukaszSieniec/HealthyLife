package com.example.adding_product;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import com.example.expandable_recyclerview.Product;
import com.example.healthylife.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class SearchFragment extends Fragment implements TextWatcher, View.OnTouchListener, SearchProduct.OnChangeTypeOfMealInFragment {

    private EditText editTextViewSearch;

    private ListView listViewSearch, listViewSelectedProduct;
    private ArrayAdapter<String> productsAdapter;
    private mySelectedProductAdapter selectedProductAdapter;

    private DatabaseReference databaseReference;

    private List <String> suggestionProducts;
    private List <Product> selectedProducts;

    private String productName = "";
    private String date = "";
    private String typeOfMeal = "";
    private float consumedCalories;

    public SearchFragment() {
        // Required empty public constructor
    }

    public static SearchFragment newInstance(String date, String typeOfMeal, float consumedCalories) {

        SearchFragment searchFragment = new SearchFragment();

        Bundle args = new Bundle();

        args.putString("date", date);
        args.putString("typeOfMeal", typeOfMeal);
        args.putFloat("consumedCalories", consumedCalories);
        searchFragment.setArguments(args);

        return searchFragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        if(getArguments() != null) {

            date = getArguments().getString("date");
            typeOfMeal = getArguments().getString("typeOfMeal");
            consumedCalories = getArguments().getFloat("consumedCalories");
        }

        ((SearchProduct)getActivity()).setOnChangeTypeOfMealInFragment(this);

        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        editTextViewSearch = view.findViewById(R.id.editTextViewSearch);

        listViewSearch = view.findViewById(R.id.listViewSearch);
        listViewSelectedProduct = view.findViewById(R.id.listViewSelectedProduct);

        editTextViewSearch.addTextChangedListener(this);
        editTextViewSearch.setOnTouchListener(this);

        databaseReference = FirebaseDatabase.getInstance().getReference();

        suggestionProducts = new ArrayList<>();
        selectedProducts = new ArrayList<>();

        productsAdapter = new ArrayAdapter<>(getActivity(), R.layout.list_single_item, R.id.textViewProductNameSearch, suggestionProducts);
        selectedProductAdapter = new mySelectedProductAdapter(getActivity(), R.layout.selected_product_single_item, selectedProducts);

        listViewSelectedProduct.setAdapter(selectedProductAdapter);
        listViewSearch.setAdapter(productsAdapter);

        editTextViewSearch.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_search, 0, 0, 0);

        databaseReference.child("Products").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                suggestionProducts.clear();

                for(DataSnapshot suggestionSnapshot : dataSnapshot.getChildren()) {

                    String suggestionProduct = suggestionSnapshot.child("name").getValue(String.class);

                    suggestionProducts.add(suggestionProduct);
                }

                productsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        listViewSearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                productName = productsAdapter.getItem(position);

                databaseReference.child("Products").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        for(DataSnapshot selectedSnapshot : dataSnapshot.getChildren()) {

                            if(selectedSnapshot.child("name").getValue(String.class).contains(productName)) {

                                selectedProducts.add(selectedSnapshot.getValue(Product.class));
                            }
                        }

                        listViewSelectedProduct.setVisibility(View.VISIBLE);
                        listViewSearch.setVisibility(View.INVISIBLE);

                        editTextViewSearch.setText(productName);

                        selectedProductAdapter.notifyDataSetChanged();

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });

        listViewSelectedProduct.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Product product = selectedProductAdapter.getItem(position);

                Intent intent = new Intent(getActivity(), SaveProduct.class);
                intent.putExtra("product", product);
                intent.putExtra("date", date);
                intent.putExtra("meal", typeOfMeal);
                intent.putExtra("consumedCalories", consumedCalories);

                startActivity(intent);
            }
        });
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

    @Override
    public void onTextChanged(CharSequence charSequence, int start, int before, int count) {

        productsAdapter.getFilter().filter(charSequence);

        if(charSequence.length() > 0) {

            editTextViewSearch.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_search, 0, R.drawable.ic_clear_black_selected_product, 0);

        } else {

            editTextViewSearch.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_search, 0, 0, 0);
        }
    }

    @Override
    public void afterTextChanged(Editable s) { }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        if(event.getAction() == MotionEvent.ACTION_UP) {

            if(event.getRawX() >= editTextViewSearch.getRight() - editTextViewSearch.getTotalPaddingRight() + 64) {

                editTextViewSearch.setText("");

                selectedProducts.clear();

                listViewSelectedProduct.setVisibility(View.INVISIBLE);
                listViewSearch.setVisibility(View.VISIBLE);

                return true;
            }
        }

        return false;
    }

    @Override
    public void changeTypeOfMealInFragment(String typeOfMeal) {
        this.typeOfMeal = typeOfMeal;
    }
}
