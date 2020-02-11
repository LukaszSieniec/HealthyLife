package com.example.format_numbers;

import java.text.DecimalFormat;

public class FormatNumbers {

    public static String formatNumberWithDecimalPlaces(float number) {

        DecimalFormat decimalFormat = new DecimalFormat("#.##");

        return decimalFormat.format(number);
    }

    public static String formatNumberWithOneDecimalPlace(float number) {

        DecimalFormat decimalFormat = new DecimalFormat("#.#");

        return decimalFormat.format(number);
    }

    public static String formatNumberWithoutDecimalPlaces(float number) {

        DecimalFormat decimalFormat = new DecimalFormat("#");

        return decimalFormat.format(number);
    }
}
