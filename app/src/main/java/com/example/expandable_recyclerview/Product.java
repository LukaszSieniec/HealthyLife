package com.example.expandable_recyclerview;

import android.os.Parcel;
import android.os.Parcelable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity (tableName = "LastProducts")
public class Product implements Parcelable {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private int id;

    @ColumnInfo(name = "name")
    private String name;
    @ColumnInfo(name = "carbohydrates")
    private float carbohydrates;
    @ColumnInfo(name = "protein")
    private float protein;
    @ColumnInfo(name = "fat")
    private float fat;
    @ColumnInfo(name = "calories")
    private float calories;
    @ColumnInfo(name = "weight")
    private float weight;
    @ColumnInfo(name = "unit")
    private String unit;

    @Ignore
    private final int caloriesRWS = 2000;
    @Ignore
    private final int fatRWS = 70;
    @Ignore
    private final int carbohydratesRWS = 260;
    @Ignore
    private final int proteinRWS = 50;

    @Ignore
    public Product(String name, float carbohydrates, float protein, float fat, float weight, float calories, String unit) {
        this.name = name;
        this.carbohydrates = carbohydrates;
        this.protein = protein;
        this.fat = fat;
        this.weight = weight;
        this.calories = calories;
        this.unit = unit;
    }


    @Ignore
    public Product(String name, String unit) {
        this.name = name;
        this.unit = unit;
    }

    public Product(){}


    protected Product(Parcel in) {
        name = in.readString();
        carbohydrates = in.readFloat();
        protein = in.readFloat();
        fat = in.readFloat();
        weight = in.readFloat();
        calories = in.readFloat();
        unit = in.readString();
    }

    public static final Creator<Product> CREATOR = new Creator<Product>() {
        @Override
        public Product createFromParcel(Parcel in) {
            return new Product(in);
        }

        @Override
        public Product[] newArray(int size) {
            return new Product[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeFloat(carbohydrates);
        dest.writeFloat(protein);
        dest.writeFloat(fat);
        dest.writeFloat(weight);
        dest.writeFloat(calories);
        dest.writeString(unit);
    }

    public float updateCalories(float currentWeight) {

        return (currentWeight/weight) * calories;

    }

    public float updateFat(float currentWeight) {

        return (currentWeight/weight) * fat;
    }

    public float updateCarbohydrates(float currentWeight) {

        return (currentWeight/weight) * carbohydrates;
    }

    public float updateProtein(float currentWeight) {

        return (currentWeight/weight) * protein;
    }

    public float updatePortionWeight(float currentWeight) {

        return currentWeight;
    }

    public float updatePerPortionkJ(float currentWeight) {

        return convertCaloriesToKiloJoules((currentWeight/weight) * calories);
    }

    public float updatePerPortionPercentEnergy(float currentWeight) {

        return calculatePercentEnergyRWS((currentWeight/weight) * calories);
    }

    public float updatePerPortionCalories(float currentWeight) {

        return (currentWeight/weight) * calories;
    }

    public float updatePerPortionFatWeight(float currentWeight) {

        return (currentWeight/weight) * fat;
    }

    public float updatePerPortionPercentFat(float currentWeight) {

        return calculatePercentFatRWS((currentWeight/weight) * fat);
    }

    public float updatePerPortionWeightCarbohydrates(float currentWeight) {

        return (currentWeight/weight) * carbohydrates;
    }

    public float updatePerPortionPercentCarbohydrates(float currentWeight) {

        return calculatePercentCarbohydratesRWS((currentWeight/weight) * carbohydrates);
    }

    public float updatePerPortionWeightProtein(float currentWeight) {

        return (currentWeight/weight) * protein;
    }

    public float updatePerPortionPercentProtein(float currentWeight) {

        return calculatePercentProteinRWS(currentWeight/weight) * protein;
    }

    public float convertCaloriesToKiloJoules(float calories) {

        return calories * 4.184f;
    }

    public float calculatePercentEnergyRWS(float calories) {

        return (calories/caloriesRWS) * 100;
    }

    public float calculatePercentFatRWS(float fat) {

        return (fat/fatRWS) * 100;
    }

    public float calculatePercentCarbohydratesRWS(float carbohydrates) {

        return (carbohydrates/carbohydratesRWS) * 100;
    }

    public float calculatePercentProteinRWS(float protein) {

        return (protein/proteinRWS) * 100;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getCarbohydrates() {
        return carbohydrates;
    }

    public void setCarbohydrates(float carbohydrates) {
        this.carbohydrates = carbohydrates;
    }

    public float getProtein() {
        return protein;
    }

    public void setProtein(float protein) {
        this.protein = protein;
    }

    public float getFat() {
        return fat;
    }

    public void setFat(float fat) {
        this.fat = fat;
    }

    public float getCalories() {
        return calories;
    }

    public void setCalories(float calories) {
        this.calories = calories;
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public static Creator<Product> getCREATOR() {
        return CREATOR;
    }
}
