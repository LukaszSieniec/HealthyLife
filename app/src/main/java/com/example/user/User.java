package com.example.user;

import com.google.firebase.auth.FirebaseUser;

public class User {

    private static volatile User single_instance;

    private static class UserValues {

        private FirebaseUser currentUser;

        private String email = "";
        private String accountName = "";
        private String currentUserUID = "";

        private String gender = "";
        private String dateOfBirth = "";
        private String levelOfActivity = "";

        private int age;
        private float weight;
        private float muscleMass;
        private float height;

        private float targetWeight;
        private float targetMuscleMass;
    }

    private volatile UserValues userValues = new UserValues();

    private User(){}

    public static User getInstance() {

        if(single_instance == null) {

            synchronized (User.class) {

                if(single_instance == null) {

                    single_instance = new User();
                }
            }
        }

        return single_instance;
    }

    public synchronized void resetAllValues() {
        userValues = new UserValues();
    }

    public synchronized FirebaseUser getCurrentUser() {
        return userValues.currentUser;
    }

    public synchronized void setCurrentUser(FirebaseUser currentUser) {
        this.userValues.currentUser = currentUser;
    }

    public synchronized String getEmail() {
        return userValues.email;
    }

    public synchronized void setEmail(String email) {
        this.userValues.email = email;
    }

    public synchronized String getAccountName() {
        return userValues.accountName;
    }

    public synchronized void setAccountName(String accountName) {
        this.userValues.accountName = accountName;
    }

    public synchronized  String getCurrentUserUID() {
        return userValues.currentUserUID;
    }

    public synchronized void setCurrentUserUID(String currentUserUID) {
        this.userValues.currentUserUID = currentUserUID;
    }

    public synchronized  String getGender() {
        return userValues.gender;
    }

    public synchronized void setGender(String gender) {
        this.userValues.gender = gender;
    }

    public synchronized String getDateOfBirth() {
        return userValues.dateOfBirth;
    }

    public synchronized void setDateOfBirth(String dateOfBirth) {
        this.userValues.dateOfBirth = dateOfBirth;
    }

    public synchronized String getLevelOfActivity() {
        return userValues.levelOfActivity;
    }

    public synchronized void setLevelOfActivity(String levelOfActivity) {
        this.userValues.levelOfActivity = levelOfActivity;
    }

    public synchronized int getAge() {
        return userValues.age;
    }

    public synchronized void setAge(int age) {
        this.userValues.age = age;
    }

    public synchronized float getWeight() {
        return userValues.weight;
    }

    public synchronized void setWeight(float weight) {
        this.userValues.weight = weight;
    }

    public synchronized float getMuscleMass() {
        return userValues.muscleMass;
    }

    public synchronized void setMuscleMass(float muscleMass) {
        this.userValues.muscleMass = muscleMass;
    }

    public synchronized  float getHeight() {
        return userValues.height;
    }

    public synchronized void setHeight(float height) {
        this.userValues.height = height;
    }

    public synchronized float getTargetWeight() {

        return userValues.targetWeight;
    }

    public synchronized void setTargetWeight(float targetWeight) {

        this.userValues.targetWeight = targetWeight;
    }

    public synchronized float getTargetMuscleMass() {

        return userValues.targetMuscleMass;
    }

    public synchronized void setTargetMuscleMass(float targetMuscleMass) {

        this.userValues.targetMuscleMass = targetMuscleMass;
    }
}

