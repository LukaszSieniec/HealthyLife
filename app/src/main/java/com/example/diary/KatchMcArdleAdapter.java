package com.example.diary;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.caloric_demand.KatchMcArdle;
import com.example.format_numbers.FormatNumbers;
import com.example.healthylife.R;
import com.example.user.User;
import java.util.ArrayList;
import java.util.Map;

public class KatchMcArdleAdapter extends RecyclerView.Adapter<KatchMcArdleAdapter.KatchMcArdleViewHolder> {

    private User user;
    private KatchMcArdle katchMcArdle;

    private Map<Integer, Float> consumedCalories;
    private Map<Integer, Float> allMuscleMass;
    private ArrayList<String> namesOfDaysOfTheMonth;
    private Context context;
    private int resource;

    public KatchMcArdleAdapter(Map<Integer, Float> consumedCalories, Context context, int resource, ArrayList<String> namesOfDaysOfTheMonth, Map<Integer, Float> allMuscleMass) {

        this.consumedCalories = consumedCalories;
        this.context = context;
        this.resource = resource;
        this.namesOfDaysOfTheMonth = namesOfDaysOfTheMonth;
        this.allMuscleMass = allMuscleMass;

        user = User.getInstance();

        katchMcArdle = new KatchMcArdle(user.getMuscleMass(), user.getLevelOfActivity());
    }

    @NonNull
    @Override
    public KatchMcArdleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = ((Activity)context).getLayoutInflater();

        View view = layoutInflater.inflate(resource, parent, false);

        KatchMcArdleViewHolder katchMcArdleViewHolder = new KatchMcArdleViewHolder(view);

        return katchMcArdleViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull KatchMcArdleViewHolder holder, int position) {

        holder.textViewDay.setText(String.valueOf(position + 1) + "\n" + namesOfDaysOfTheMonth.get(position) + ".");

        if(position + 1 < 10) {

            holder.textViewDay.setText(customHeadline(holder.textViewDay.getText().toString(), 1, 6));

        } else {

            holder.textViewDay.setText(customHeadline(holder.textViewDay.getText().toString(), 3, 7));
        }

        if(consumedCalories.containsKey(position + 1)) {

            if(consumedCalories.get(position + 1) == 0) {

                holder.textViewConsumedCaloriesItem.setText("-");
                holder.textViewResultCaloriesOfDay.setText("-");

            } else {

                holder.textViewConsumedCaloriesItem.setText(FormatNumbers.formatNumberWithoutDecimalPlaces(consumedCalories.get(position + 1)));

                katchMcArdle.setMuscleMass(allMuscleMass.get(position + 1));
                katchMcArdle.calculateGDA();

                float difference = katchMcArdle.getGda() - consumedCalories.get(position + 1);

                holder.textViewResultCaloriesOfDay.setText(FormatNumbers.formatNumberWithoutDecimalPlaces(difference));

                if(difference > 0) {

                    holder.textViewResultCaloriesOfDay.setTextColor(context.getResources().getColor(R.color.backgroundScreens));

                } else {

                    holder.textViewResultCaloriesOfDay.setTextColor(context.getResources().getColor(R.color.colorAccent));
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return namesOfDaysOfTheMonth.size();
    }

    private SpannableString customHeadline(String text, int start, int end) {

        SpannableString spannableString = new SpannableString(text);

        StyleSpan boldSpan = new StyleSpan(Typeface.BOLD);

        spannableString.setSpan(boldSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new RelativeSizeSpan(0.5f), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        return spannableString;
    }

    public static class KatchMcArdleViewHolder extends RecyclerView.ViewHolder {

        TextView textViewDay, textViewConsumedCaloriesItem, textViewResultCaloriesOfDay;

        public KatchMcArdleViewHolder(@NonNull View itemView) {
            super(itemView);

            textViewDay = itemView.findViewById(R.id.textViewDay);
            textViewConsumedCaloriesItem = itemView.findViewById(R.id.textViewConsumedCaloriesItem);
            textViewResultCaloriesOfDay = itemView.findViewById(R.id.textViewResultCaloriesOfDay);
        }
    }
}
