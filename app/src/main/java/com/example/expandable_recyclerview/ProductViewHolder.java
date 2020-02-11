package com.example.expandable_recyclerview;

import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.example.healthylife.R;
import com.thoughtbot.expandablerecyclerview.viewholders.ChildViewHolder;
import static com.example.format_numbers.FormatNumbers.formatNumberWithDecimalPlaces;
import static com.example.format_numbers.FormatNumbers.formatNumberWithoutDecimalPlaces;

public class ProductViewHolder extends ChildViewHolder {

     TextView textViewNameProduct, textViewCaloriesProduct, textViewWeightProduct;
     RelativeLayout relativeLayoutExpandableRecyclerViewProduct;

    public ProductViewHolder(View itemView) {
        super(itemView);

        relativeLayoutExpandableRecyclerViewProduct = itemView.findViewById(R.id.relativeLayoutExpandableRecyclerViewProduct);
        textViewNameProduct = itemView.findViewById(R.id.textViewNameProduct);
        textViewCaloriesProduct = itemView.findViewById(R.id.textViewCaloriesProduct);
        textViewWeightProduct = itemView.findViewById(R.id.textViewWeightProduct);
    }

    public void bind(Product product) {

        textViewNameProduct.setText(product.getName());
        textViewCaloriesProduct.setText(formatNumberWithoutDecimalPlaces(product.getCalories()) + " calories");
        textViewWeightProduct.setText((formatNumberWithDecimalPlaces(product.getWeight()) + " " + product.getUnit()));
    }
}
