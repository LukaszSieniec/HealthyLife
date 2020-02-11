package com.example.diary;

public class AllMealsOfTheDay {

    private float totalCarbohydratesOfAllMeals;
    private float totalProteinOfAllMeals;
    private float totalFatOfAllMeals;
    private float totalCaloriesOfAllMeals;

    private float caloriesToBeConsumed;
    private float percentGDA;

    private float percentCarbohydrates;
    private float percentProtein;
    private float percentFat;

    public AllMealsOfTheDay(){}

    public AllMealsOfTheDay(float totalCarbohydratesOfAllMeals, float totalProteinOfAllMeals, float totalFatOfAllMeals, float totalCaloriesOfAllMeals) {

        this.totalCarbohydratesOfAllMeals = totalCarbohydratesOfAllMeals;
        this.totalProteinOfAllMeals = totalProteinOfAllMeals;
        this.totalFatOfAllMeals = totalFatOfAllMeals;
        this.totalCaloriesOfAllMeals = totalCaloriesOfAllMeals;
    }

    public void calculateTotalCarbohydratesOfAllMeals(float totalCarbohydratesOfBreakfast, float totalCarbohydratesOfLunch, float totalCarbohydratesOfDinner,
                                                       float totalCarbohydratesSnacks) {

        totalCarbohydratesOfAllMeals = totalCarbohydratesOfBreakfast + totalCarbohydratesOfLunch + totalCarbohydratesOfDinner + totalCarbohydratesSnacks;
    }

    public void calculateTotalProteinOfAllMeals(float totalProteinOfBreakfast, float totalProteinOfLunch, float totalProteinOfDinner,
                                                float totalProteinOfSnacks) {

        totalProteinOfAllMeals = totalProteinOfBreakfast + totalProteinOfLunch + totalProteinOfDinner + totalProteinOfSnacks;
    }

    public void calculateTotalFatOfAllMeal(float totalFatOfBreakfast, float totalFatOfLunch, float totalFatOfDinner, float totalFatOfSnacks) {

        totalFatOfAllMeals = totalFatOfBreakfast + totalFatOfLunch + totalFatOfDinner + totalFatOfSnacks;
    }

    public void calculateTotalCaloriesOfAllMeal(float totalCaloriesOfBreakfast, float totalCaloriesOfLunch, float totalCaloriesOfDinner, float totalCaloriesOfSnacks) {

        totalCaloriesOfAllMeals = totalCaloriesOfBreakfast + totalCaloriesOfLunch + totalCaloriesOfDinner + totalCaloriesOfSnacks;
    }

    public void calculateCaloriesToBeConsumed(float GDA, float consumedCalories) {

        caloriesToBeConsumed = GDA - consumedCalories;
    }

    public void calculatePercentGDA(float GDA) {

        percentGDA = (totalCaloriesOfAllMeals/GDA) * 100;
    }

    public float getTotalCarbohydratesOfAllMeals() {
        return totalCarbohydratesOfAllMeals;
    }

    public void setTotalCarbohydratesOfAllMeals(float totalCarbohydratesOfAllMeals) {
        this.totalCarbohydratesOfAllMeals = totalCarbohydratesOfAllMeals;
    }

    public float getTotalProteinOfAllMeals() {
        return totalProteinOfAllMeals;
    }

    public void setTotalProteinOfAllMeals(float totalProteinOfAllMeals) {
        this.totalProteinOfAllMeals = totalProteinOfAllMeals;
    }

    public float getTotalFatOfAllMeals() {
        return totalFatOfAllMeals;
    }

    public void setTotalFatOfAllMeals(float totalFatOfAllMeals) {
        this.totalFatOfAllMeals = totalFatOfAllMeals;
    }

    public float getTotalCaloriesOfAllMeals() {
        return totalCaloriesOfAllMeals;
    }

    public void setTotalCaloriesOfAllMeals(float totalCaloriesOfAllMeals) {
        this.totalCaloriesOfAllMeals = totalCaloriesOfAllMeals;
    }

    public float getCaloriesToBeConsumed() {

        return caloriesToBeConsumed;
    }

    public float getPercentGDA() {

        return percentGDA;
    }

    public void calculatePercentOfCarbohydratesFromAllMacros() {

        percentCarbohydrates = totalCarbohydratesOfAllMeals / (totalCarbohydratesOfAllMeals + totalProteinOfAllMeals + totalFatOfAllMeals) * 100;
    }

    public void calculatePercentOfProteinFromAllMacros() {

        percentProtein = totalProteinOfAllMeals / (totalCarbohydratesOfAllMeals + totalProteinOfAllMeals + totalFatOfAllMeals) * 100;
    }

    public void calculatePercentOfFatFromAllMacros() {

        percentFat = totalFatOfAllMeals / (totalCarbohydratesOfAllMeals + totalProteinOfAllMeals + totalFatOfAllMeals) * 100;
    }

    public float getPercentCarbohydrates() {
        return percentCarbohydrates;
    }

    public float getPercentProtein() {
        return percentProtein;
    }

    public float getPercentFat() {
        return percentFat;
    }
}
