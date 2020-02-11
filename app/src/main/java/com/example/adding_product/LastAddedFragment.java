package com.example.adding_product;


import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.diary.LastAddedProductsAdapter;
import com.example.expandable_recyclerview.Product;
import com.example.expandable_recyclerview.ProductDatabase;
import com.example.healthylife.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class LastAddedFragment extends Fragment implements SearchProduct.OnChangeTypeOfMealInFragment {

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private LastAddedProductsAdapter adapter;

    private ProductDatabase database;

    private String date = "";
    private String typeOfMeal = "";
    private float consumedCalories;

    public LastAddedFragment() {
        // Required empty public constructor
    }

    public static LastAddedFragment newInstance(String date, String typeOfMeal, float consumedCalories) {

        LastAddedFragment lastAddedFragment = new LastAddedFragment();

        Bundle bundle = new Bundle();
        bundle.putString("date", date);
        bundle.putString("typeOfMeal", typeOfMeal);
        bundle.putFloat("consumedCalories", consumedCalories);

        lastAddedFragment.setArguments(bundle);

        return lastAddedFragment;
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

        return inflater.inflate(R.layout.fragment_last_added, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        recyclerView = view.findViewById(R.id.recyclerViewRecentlyAddedProducts);

        database = ProductDatabase.getInstance(getActivity());

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        adapter = new LastAddedProductsAdapter(getActivity(), database.productDao().getLastProducts(), R.layout.last_added_products_item);
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new LastAddedProductsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Product product) {

                Product lastAddedProduct = product;

                Intent intent = new Intent(getActivity(), SaveProduct.class);
                intent.putExtra("product", lastAddedProduct);
                intent.putExtra("date", date);
                intent.putExtra("meal", typeOfMeal);
                intent.putExtra("consumedCalories", consumedCalories);

                startActivity(intent);
            }
        });
    }

    @Override
    public void changeTypeOfMealInFragment(String typeOfMeal) {

        this.typeOfMeal = typeOfMeal;
    }
}
