package com.example.caloric_demand;

public class HarissBenedict extends CaloricDemand {

    public HarissBenedict(float weight, float height, int age, String gender, String levelOfActivity) {

        super(weight, height, age, gender, levelOfActivity);
    }

    public HarissBenedict(){}

    @Override
    public void calculateGDA() {

        if(gender.equals("Woman")) {

            gda = 655 +((9.6f * weight) + (1.8f * height)) - (4.7f * age);

        } else if(gender.equals("Man")) {

            gda = 66 + ((13.7f * weight) + (5 * height)) - (6.76f * age);
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
