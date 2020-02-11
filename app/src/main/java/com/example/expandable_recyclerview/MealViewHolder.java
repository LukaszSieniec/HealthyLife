package com.example.expandable_recyclerview;

import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import com.example.healthylife.R;
import com.thoughtbot.expandablerecyclerview.viewholders.GroupViewHolder;

public class MealViewHolder extends GroupViewHolder {

    private TextView textViewMealInformation;
    private ImageView imageViewArrowExpandableRecyclerView;
    private LinearLayout linearLayoutMeal;
    private View divider;
    private Context context;

    private boolean moreThanZero;
    private boolean isExpand = false;

    public MealViewHolder(View itemView) {
        super(itemView);

        textViewMealInformation = itemView.findViewById(R.id.textViewMealInformation);
        imageViewArrowExpandableRecyclerView = itemView.findViewById(R.id.imageViewArrowExpandableRecyclerView);
        linearLayoutMeal = itemView.findViewById(R.id.linearLayoutMeal);
        divider = itemView.findViewById(R.id.dividerMeal);

        context = itemView.getContext();
    }

    public void bind(MealExpandableRecyclerView meal) {

        if(meal.getItemCount() == 0) {

            if(isExpand == true) {

                linearLayoutMeal.setBackgroundResource(R.color.meal_background);
                divider.setAlpha(0f);
            }

            moreThanZero = false;
            imageViewArrowExpandableRecyclerView.setAlpha(0f);
            textViewMealInformation.setText("You have not added any product");

        } else {

            if(isExpand == true) {

                linearLayoutMeal.setBackgroundResource(R.color.white);
                divider.setAlpha(1.0f);
            }

            moreThanZero = true;
            imageViewArrowExpandableRecyclerView.setAlpha(1f);
            textViewMealInformation.setText("Products: " + meal.getItems().size());
        }
    }

    @Override
    public void expand() {

        if(moreThanZero == true) {

            animateExpand();
            isExpand = true;
        }
    }

    @Override
    public void collapse() {

        animateCollapse();
        isExpand = false;
    }

    private void animateExpand() {

        RotateAnimation rotate =
                new RotateAnimation(360, 180, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(300);
        rotate.setFillAfter(true);

        imageViewArrowExpandableRecyclerView.startAnimation(rotate);
        imageViewArrowExpandableRecyclerView.setColorFilter(ContextCompat.getColor(context, R.color.arrow));
        linearLayoutMeal.setBackgroundResource(R.color.white);
        divider.setAlpha(1.0f);

    }

    private void animateCollapse() {

        RotateAnimation rotate =
                new RotateAnimation(180, 0, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(300);
        rotate.setFillAfter(true);

        imageViewArrowExpandableRecyclerView.setAnimation(rotate);
        imageViewArrowExpandableRecyclerView.setColorFilter(ContextCompat.getColor(context, R.color.white));
        linearLayoutMeal.setBackgroundResource(R.color.meal_background);
        divider.setAlpha(0f);
    }
}
