package com.example.adding_product;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

public class MyPagerAdapter extends FragmentStatePagerAdapter {

    private final int NUM_PAGES = 3;

    private final String firstTab = "New Product";
    private final String secondTab = "Search";
    private final String thirdTab = "Last Added";

    private String date = "";
    private String typeOfMeal = "";
    private float consumedCalories;

    public MyPagerAdapter(@NonNull FragmentManager fm, String date, String typeOfMeal, float consumedCalories) {
        super(fm, FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        this.date = date;
        this.typeOfMeal = typeOfMeal;
        this.consumedCalories = consumedCalories;

    }

    public void setTypeOfMeal(String typeOfMeal) {

        this.typeOfMeal = typeOfMeal;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {

        switch (position) {

            case 0:
                return NewProductFragment.newInstance(date, typeOfMeal, consumedCalories);

            case 1:
                return SearchFragment.newInstance(date, typeOfMeal, consumedCalories);

            case 2:
                return LastAddedFragment.newInstance(date, typeOfMeal, consumedCalories);

            default: return null;
        }
    }

    @Override
    public int getCount() {
        return NUM_PAGES;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {

        switch (position) {

            case 0:
                return firstTab;

            case 1:
                return secondTab;

            case 2:
                return thirdTab;

            default: return null;
        }
    }
}
