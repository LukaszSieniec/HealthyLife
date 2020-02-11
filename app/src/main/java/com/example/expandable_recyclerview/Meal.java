package com.example.expandable_recyclerview;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.ArrayList;

public class Meal implements Parcelable {

    public static final String breakfastTitle = "Breakfast";
    public static final String lunchTitle = "Lunch";
    public static final String dinnerTitle = "Dinner";
    public static final String snacksTitle = "Snacks";

    private String dateOfMeal = "";

    private float totalCarbohydratesOfMeal;
    private float totalProteinOfMeal;
    private float totalFatOfMeal;
    private float totalCaloriesOfMeal;

    private ArrayList <Product> totalProductsOfMeal = new ArrayList<>();
    private ArrayList <String> idProductsOfMeal = new ArrayList<>();

    public Meal(){}

    @SuppressWarnings("unchecked")
    public Meal(Parcel in) {

        dateOfMeal = in.readString();
        totalCarbohydratesOfMeal = in.readFloat();
        totalProteinOfMeal = in.readFloat();
        totalFatOfMeal = in.readFloat();
        totalCaloriesOfMeal = in.readFloat();
        totalProductsOfMeal = in.readArrayList(null);
        idProductsOfMeal = in.readArrayList(null);
    }

    public static final Creator<Meal> CREATOR = new Creator<Meal>() {
        @Override
        public Meal createFromParcel(Parcel in) {
            return new Meal(in);
        }

        @Override
        public Meal[] newArray(int size) {
            return new Meal[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeString(dateOfMeal);
        dest.writeFloat(totalCarbohydratesOfMeal);
        dest.writeFloat(totalProteinOfMeal);
        dest.writeFloat(totalFatOfMeal);
        dest.writeFloat(totalCaloriesOfMeal);
        dest.writeList(totalProductsOfMeal);
        dest.writeList(idProductsOfMeal);
    }

    public void calculateTotalCaloriesOfMeal() {

        totalCaloriesOfMeal = 0;

        for(int i = 0; i < totalProductsOfMeal.size(); i ++) {

            totalCaloriesOfMeal += totalProductsOfMeal.get(i).getCalories();
        }
    }

    public void calculateTotalCarbohydratesOfMeal() {

        totalCarbohydratesOfMeal = 0;

        for(int i = 0; i < totalProductsOfMeal.size(); i ++) {

            totalCarbohydratesOfMeal += totalProductsOfMeal.get(i).getCarbohydrates();
        }
    }

    public void calculateTotalProteinOfMeal() {

        totalProteinOfMeal = 0;

        for(int i = 0; i < totalProductsOfMeal.size(); i++) {

            totalProteinOfMeal += totalProductsOfMeal.get(i).getProtein();
        }
    }

    public void calculateTotalFatOfMeal() {

        totalFatOfMeal = 0;

        for(int i = 0; i < totalProductsOfMeal.size(); i ++) {

            totalFatOfMeal += totalProductsOfMeal.get(i).getFat();
        }
    }

    public String getDateOfMeal() {
        return dateOfMeal;
    }

    public void setDateOfMeal(String dateOfMeal) {
        this.dateOfMeal = dateOfMeal;
    }

    public float getTotalCarbohydratesOfMeal() {
        return totalCarbohydratesOfMeal;
    }

    public void setTotalCarbohydratesOfMeal(float totalCarbohydratesOfMeal) {
        this.totalCarbohydratesOfMeal = totalCarbohydratesOfMeal;
    }

    public float getTotalProteinOfMeal() {
        return totalProteinOfMeal;
    }

    public void setTotalProteinOfMeal(float totalProteinOfMeal) {
        this.totalProteinOfMeal = totalProteinOfMeal;
    }

    public float getTotalFatOfMeal() {
        return totalFatOfMeal;
    }

    public void setTotalFatOfMeal(float totalFatOfMeal) {
        this.totalFatOfMeal = totalFatOfMeal;
    }

    public float getTotalCaloriesOfMeal() {
        return totalCaloriesOfMeal;
    }

    public void setTotalCaloriesOfMeal(float totalCaloriesOfMeal) {
        this.totalCaloriesOfMeal = totalCaloriesOfMeal;
    }

    public ArrayList<Product> getTotalProductsOfMeal() {
        return totalProductsOfMeal;
    }

    public void setTotalProductsOfMeal(ArrayList<Product> totalProductsOfMeal) {
        this.totalProductsOfMeal = totalProductsOfMeal;
    }

    public ArrayList<String> getIdProductsOfMeal() {
        return idProductsOfMeal;
    }

    public void setIdProductsOfMeal(ArrayList<String> idProductsOfMeal) {
        this.idProductsOfMeal = idProductsOfMeal;
    }

    public static Creator<Meal> getCREATOR() {
        return CREATOR;
    }
}
