package com.example.expandable_recyclerview;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.healthylife.R;
import com.thoughtbot.expandablerecyclerview.ExpandableRecyclerViewAdapter;
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;
import java.util.List;

public class ProductAdapter extends ExpandableRecyclerViewAdapter <MealViewHolder, ProductViewHolder> {

    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void OnItemClick (Product product, int position);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public ProductAdapter(List<? extends ExpandableGroup> groups) {
        super(groups);
    }

    @Override
    public MealViewHolder onCreateGroupViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());

        View view = layoutInflater.inflate(R.layout.expandable_recyclerview_meal, parent, false);

        return new MealViewHolder(view);
    }

    @Override
    public ProductViewHolder onCreateChildViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());

        View view = layoutInflater.inflate(R.layout.expandable_recyclerview_product, parent, false);

        return new ProductViewHolder(view);
    }

    @Override
    public void onBindChildViewHolder(final ProductViewHolder holder, final int flatPosition, final ExpandableGroup group, final int childIndex) {

        Product product = (Product) group.getItems().get(childIndex);
        holder.bind(product);

        holder.relativeLayoutExpandableRecyclerViewProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(onItemClickListener != null) {

                    onItemClickListener.OnItemClick((Product) group.getItems().get(childIndex), childIndex);
                }
            }
        });
    }

    @Override
    public void onBindGroupViewHolder(MealViewHolder holder, int flatPosition, ExpandableGroup group) {

        MealExpandableRecyclerView meal = (MealExpandableRecyclerView) group;
        holder.bind(meal);
    }
}
