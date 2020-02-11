package com.example.caloric_demand;

public class KatchMcArdle extends CaloricDemand {

    public KatchMcArdle(float muscleMass, String levelOfActivity) {

        super(muscleMass, levelOfActivity);
    }

    @Override
    public void calculateGDA() {

        gda = 370 + (21.6f * muscleMass);

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
