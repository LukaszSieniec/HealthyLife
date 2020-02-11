package com.example.diary;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.expandable_recyclerview.Product;
import com.example.format_numbers.FormatNumbers;
import com.example.healthylife.R;
import java.util.List;

public class LastAddedProductsAdapter extends RecyclerView.Adapter<LastAddedProductsAdapter.LastAddedProductsViewHolder> {

    private Context context;
    private List<Product> products;
    private int resource;

    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {

        void onItemClick(Product product);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public LastAddedProductsAdapter(Context context, List<Product> products, int resource) {
        this.context = context;
        this.products = products;
        this.resource = resource;
    }

    @NonNull
    @Override
    public LastAddedProductsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = ((Activity)context).getLayoutInflater();

        View view = layoutInflater.inflate(resource, parent, false);

        LastAddedProductsViewHolder lastAddedProductsViewHolder = new LastAddedProductsViewHolder(view, onItemClickListener, products);

        return lastAddedProductsViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull LastAddedProductsViewHolder holder, int position) {

        Product product = products.get(position);

        holder.textViewLastAddedName.setText(product.getName());
        holder.textViewLastAddedWeight.setText(FormatNumbers.formatNumberWithoutDecimalPlaces(product.getWeight()) + " " + product.getUnit());
        holder.textViewLastAddedCalories.setText(FormatNumbers.formatNumberWithoutDecimalPlaces(product.getCalories()) + " calories");

    }

    @Override
    public int getItemCount() {
        return products.size();
    }



    public static class LastAddedProductsViewHolder extends RecyclerView.ViewHolder {

        TextView textViewLastAddedName, textViewLastAddedWeight, textViewLastAddedCalories;

        public LastAddedProductsViewHolder(@NonNull View itemView, final OnItemClickListener onItemClickListener, final List<Product> products) {
            super(itemView);

            textViewLastAddedName = itemView.findViewById(R.id.textViewLastAddedName);
            textViewLastAddedWeight = itemView.findViewById(R.id.textViewLastAddedWeight);
            textViewLastAddedCalories = itemView.findViewById(R.id.textViewLastAddedCalories);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(onItemClickListener != null) {

                        onItemClickListener.onItemClick(products.get(getAdapterPosition()));
                    }
                }
            });

        }
    }
}
