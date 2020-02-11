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
import com.example.caloric_demand.HarissBenedict;
import com.example.format_numbers.FormatNumbers;
import com.example.healthylife.R;
import com.example.user.User;
import java.util.ArrayList;
import java.util.Map;

public class HarissBenedictAdapter extends RecyclerView.Adapter<HarissBenedictAdapter.HarissBenedictViewHolder> {

    private User user;
    private HarissBenedict harissBenedict;

    private Map<Integer, Float> consumedCalories;
    private Map<Integer, Float> allWeight;
    private ArrayList<String> namesOfDaysOfTheMonth;
    private Context context;
    private int resource;

    public HarissBenedictAdapter(Map<Integer, Float> consumedCalories, Context context, int resource, ArrayList<String> namesOfDaysOfTheMonth, Map<Integer, Float> allWeight) {

        this.consumedCalories = consumedCalories;
        this.context = context;
        this.resource = resource;
        this.namesOfDaysOfTheMonth = namesOfDaysOfTheMonth;
        this.allWeight = allWeight;

        user = User.getInstance();

        harissBenedict = new HarissBenedict(user.getWeight(), user.getHeight(),
                user.getAge(), user.getGender(), user.getLevelOfActivity());
    }

    @NonNull
    @Override
    public HarissBenedictViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = ((Activity)context).getLayoutInflater();

        View view = layoutInflater.inflate(resource, parent, false);

        HarissBenedictViewHolder harissBenedictViewHolder  = new HarissBenedictViewHolder(view);

        return harissBenedictViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull HarissBenedictViewHolder holder, int position) {

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

                harissBenedict.setWeight(allWeight.get(position + 1));
                harissBenedict.calculateGDA();

                float difference = harissBenedict.getGda() - consumedCalories.get(position + 1);

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

    public static class HarissBenedictViewHolder extends RecyclerView.ViewHolder {

        TextView textViewDay, textViewConsumedCaloriesItem, textViewResultCaloriesOfDay;

        public HarissBenedictViewHolder(@NonNull View itemView) {
            super(itemView);

            textViewDay = itemView.findViewById(R.id.textViewDay);
            textViewConsumedCaloriesItem = itemView.findViewById(R.id.textViewConsumedCaloriesItem);
            textViewResultCaloriesOfDay = itemView.findViewById(R.id.textViewResultCaloriesOfDay);
        }
    }
}
