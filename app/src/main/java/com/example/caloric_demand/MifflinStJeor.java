package com.example.caloric_demand;

public class MifflinStJeor extends CaloricDemand {

    public MifflinStJeor(float weight, float height, int age, String gender, String levelOfActivity) {

        super(weight, height, age, gender, levelOfActivity);
    }

    public MifflinStJeor(){}

    @Override
    public void calculateGDA() {
        if(gender.equals("Woman")) {

            gda = ((9.99f * weight) + (6.25f * height)) - (4.92f * age) - 161;

        } else if(gender.equals("Man")) {

            gda = ((9.99f * weight) + (6.25f * height)) - (4.92f * age) + 5;
        }

        switch (levelOfActivity) {

            case "Very Low":
                gda = gda * VERY_LOW;
                break;

            case "Low":
                gda = gda * LOW;
                break;

            case "Medium":
                gda = gda * MEDIUM;
                break;

            case "High":
                gda = gda * HIGH;
                break;

            case "Very High":
                gda = gda * VERY_HIGH;
                break;

        }
    }
}
