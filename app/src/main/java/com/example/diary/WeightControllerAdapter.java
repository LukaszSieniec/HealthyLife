package com.example.diary;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.format_numbers.FormatNumbers;
import com.example.healthylife.R;
import java.util.ArrayList;

public class WeightControllerAdapter extends RecyclerView.Adapter<WeightControllerViewHolder> {

    private ArrayList<String> datesWeightController;
    private ArrayList<Float> allWeights;
    private Context context;
    private int resource;

    public WeightControllerAdapter(ArrayList<String> datesWeightController, ArrayList<Float> allWeights, Context context, int resource) {

        this.datesWeightController = datesWeightController;
        this.allWeights = allWeights;
        this.context = context;
        this.resource = resource;
    }

    @NonNull
    @Override
    public WeightControllerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = ((Activity)context).getLayoutInflater();
        View view = layoutInflater.inflate(resource, parent, false);

        WeightControllerViewHolder weightControllerViewHolder = new WeightControllerViewHolder(view);

        return weightControllerViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull WeightControllerViewHolder holder, int position) {

        String date = datesWeightController.get(position);
        float weight = allWeights.get(position);

        holder.textViewDateWeightController.setText(date);
        holder.textViewWeight_WeightController.setText(FormatNumbers.formatNumberWithOneDecimalPlace(weight) + " kg");

        if(position > 0) {

            if(allWeights.get(position) > allWeights.get(position - 1)) {

                holder.imageViewUpOrDown.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_arrow_upward_red_24dp));

            } else if(allWeights.get(position) < allWeights.get(position - 1)) {

                holder.imageViewUpOrDown.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_arrow_downward_blue_24dp));
            }
        }
    }

    @Override
    public int getItemCount() {
        return datesWeightController.size();
    }
}

class WeightControllerViewHolder extends RecyclerView.ViewHolder {

    TextView textViewDateWeightController, textViewWeight_WeightController;
    ImageView imageViewUpOrDown;

    public WeightControllerViewHolder(@NonNull View itemView) {
        super(itemView);

        textViewDateWeightController = itemView.findViewById(R.id.textViewDateWeightController);
        textViewWeight_WeightController = itemView.findViewById(R.id.textViewWeight_WeightController);
        imageViewUpOrDown = itemView.findViewById(R.id.imageViewUpOrDown);
    }
}
