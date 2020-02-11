package com.example.caloric_demand;

public abstract class CaloricDemand {

    static final float VERY_LOW = 1.2f;
    static final float LOW = 1.4f;
    static final float MEDIUM = 1.6f;
    static final float HIGH = 1.8f;
    static final float VERY_HIGH = 2f;

    protected float weight;
    protected float height;
    protected float muscleMass;
    protected float gda;
    protected int age;

    protected String gender = "";
    protected String levelOfActivity = "";

    public CaloricDemand(float weight, float height, int age, String gender, String levelOfActivity) {
        this.weight = weight;
        this.height = height;
        this.age = age;
        this.gender = gender;
        this.levelOfActivity = levelOfActivity;
    }

    public CaloricDemand(float muscleMass, String levelOfActivity) {

        this.muscleMass = muscleMass;
        this.levelOfActivity = levelOfActivity;
    }

    public CaloricDemand(){}

    public abstract void calculateGDA();

    public float getGda() {
        return gda;
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getLevelOfActivity() {
        return levelOfActivity;
    }

    public void setLevelOfActivity(String levelOfActivity) {
        this.levelOfActivity = levelOfActivity;
    }

    public float getMuscleMass() {
        return muscleMass;
    }

    public void setMuscleMass(float muscleMass) {
        this.muscleMass = muscleMass;
    }
}
