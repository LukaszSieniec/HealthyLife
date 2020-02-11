package com.example.adding_product;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.example.expandable_recyclerview.Product;
import com.example.healthylife.R;
import java.util.List;

import static com.example.format_numbers.FormatNumbers.formatNumberWithDecimalPlaces;
import static com.example.format_numbers.FormatNumbers.formatNumberWithoutDecimalPlaces;

public class mySelectedProductAdapter extends ArrayAdapter<Product> {

    private Context context;
    private List<Product> productList;
    private int resource;

    public mySelectedProductAdapter(Context context, int resource, List<Product> productList) {
        super(context, resource, productList);
        this.context = context;
        this.productList = productList;
        this.resource = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = convertView;
        HolderProduct holderProduct = null;

        if(view == null) {

            LayoutInflater layoutInflater = ((Activity)context).getLayoutInflater();
            view = layoutInflater.inflate(resource, parent, false);

            holderProduct = new HolderProduct();

            holderProduct.textViewSelectedProductName = view.findViewById(R.id.textViewSelectedProductName);
            holderProduct.textViewSelectedProductWeight = view.findViewById(R.id.textViewSelectedProductWeight);
            holderProduct.textViewSelectedProductCalories = view.findViewById(R.id.textViewSelectedProductCalories);

            view.setTag(holderProduct);

        } else {

            holderProduct = (HolderProduct) view.getTag();
        }

        Product product = productList.get(position);

        holderProduct.textViewSelectedProductName.setText(product.getName());
        holderProduct.textViewSelectedProductWeight.setText(formatNumberWithoutDecimalPlaces(product.getWeight()) + " " + product.getUnit());
        holderProduct.textViewSelectedProductCalories.setText(formatNumberWithoutDecimalPlaces(product.getCalories()) + " calories");

        return view;
    }
}

class HolderProduct {

    TextView textViewSelectedProductName, textViewSelectedProductWeight, textViewSelectedProductCalories;
}