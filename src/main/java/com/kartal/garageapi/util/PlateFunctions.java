package com.kartal.garageapi.util;

import java.util.regex.Pattern;

public class PlateFunctions {

    /*
     ** check whether turkish plate number is valid
     */
    public static boolean isValidPlate(String plate) {
        String plateRegex = "^([0-9]{2})-([A-Za-z]{1,3})-([0-9]{2,4})$";
        return Pattern.matches(plateRegex, plate);
    }
}
