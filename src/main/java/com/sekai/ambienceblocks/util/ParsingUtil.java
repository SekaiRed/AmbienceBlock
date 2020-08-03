package com.sekai.ambienceblocks.util;

public class ParsingUtil {
    public static int tryParseInt(String value, int defaultVal) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return defaultVal;
        }
    }

    public static int tryParseInt(String value) {
        return tryParseInt(value, 0);
    }

    public static float tryParseFloat(String value, float defaultVal) {
        try {
            return Float.parseFloat(value);
        } catch (NumberFormatException e) {
            return defaultVal;
        }
    }

    public static float tryParseFloat(String value) {
        return tryParseFloat(value, 0f);
    }

    public static double tryParseDouble(String value, double defaultVal) {
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            return defaultVal;
        }
    }

    public static double tryParseDouble(String value) {
        return tryParseDouble(value, 0D);
    }

    public static boolean isNumericWithDecimal(String str) {
        if ( str == null )
        {
            return false;
        }
        int strSize = str.length();
        for (int i = 0; i < strSize; i++)
        {
            if (!Character.isDigit(str.charAt(i)) || str.charAt(i) != 46)
                return false;
        }
        return true;
    }

    public static boolean isNumberOrDot(char c) {
        return (c >= '0' && c <= '9') || c == '.';
    }

    public static boolean isNumber(char c) {
        return (c >= '0' && c <= '9');
    }

    public static int countChar(String s, char c) {
        int count = 0;
        int strSize = s.length();
        for (int i = 0; i < strSize; i++)
        {
            if(s.charAt(i) == c)
                count++;
        }
        return count;
    }
}
