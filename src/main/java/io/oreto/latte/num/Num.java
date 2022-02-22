package io.oreto.latte.num;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Utility methods for numbers
 */
public class Num {
    /**
     * Defines the type of numbers
     */
    public enum Type {
        natural     // 1, 2, 3, 4...
        , whole     // 0, 1, 2, 3...
        , integer   // -3, -2, -1, 0, 1, 2, 3
        , rational  // -1.1, -.5, 0, 1, 1.5, 2...
    }

    static final int defaultPlaces = 2;
    static final RoundingMode defaultRoundingMode = RoundingMode.HALF_UP;

    /**
     * Round the number to specified decimal places up or down depending on the mode
     * @param d The number value
     * @param places Number of decimal places rounding to
     * @param roundingMode Rounding mode determining to round up or down
     * @return The new rounded number
     */
    public static double round(double d, int places, RoundingMode roundingMode) {
        return new BigDecimal(d).setScale(places, roundingMode).doubleValue();
    }

    /**
     * Round the number to specified decimal places
     * @param d The number value
     * @param places Number of decimal places rounding to
     * @return The new rounded number
     */
    public static double round(double d, int places) {
        return new BigDecimal(d).setScale(places, defaultRoundingMode).doubleValue();
    }

    /**
     * Round the number to default decimal places
     * @param d The number value
     * @return The new rounded number
     */
    public static double round(double d) {
        return new BigDecimal(d).setScale(defaultPlaces, defaultRoundingMode).doubleValue();
    }

    /**
     * Round the number to specified decimal places up or down depending on the mode
     * @param d The number value
     * @param places Number of decimal places rounding to
     * @param roundingMode Rounding mode determining to round up or down
     * @return The new rounded number
     */
    public static float round(float d, int places, RoundingMode roundingMode) {
        return new BigDecimal(d).setScale(places, roundingMode).floatValue();
    }

    /**
     * Round the number to specified decimal places
     * @param d The number value
     * @param places Number of decimal places rounding to
     * @return The new rounded number
     */
    public static float round(float d, int places) {
        return new BigDecimal(d).setScale(places, defaultRoundingMode).floatValue();
    }

    /**
     * Round the number to default decimal places
     * @param d The number value
     * @return The new rounded number
     */
    public static float round(float d) {
        return new BigDecimal(d).setScale(defaultPlaces, defaultRoundingMode).floatValue();
    }

    /**
     * Round the number to specified decimal places up or down depending on the mode
     * @param d The number value
     * @param places Number of decimal places rounding to
     * @param roundingMode Rounding mode determining to round up or down
     * @return The new rounded number
     */
    public static BigDecimal round(BigDecimal d, int places, RoundingMode roundingMode) {
        return d.setScale(places, roundingMode);
    }

    /**
     * Round the number to specified decimal places
     * @param d The number value
     * @param places Number of decimal places rounding to
     * @return The new rounded number
     */
    public static BigDecimal round(BigDecimal d, int places) {
        return d.setScale(places, defaultRoundingMode);
    }

    /**
     * Round the number to default decimal places
     * @param d The number value
     * @return The new rounded number
     */
    public static BigDecimal round(BigDecimal d) {
        return d.setScale(defaultPlaces, defaultRoundingMode);
    }

    // --------------------------------- INTEGER ROUNDING ---------------------------------
    /**
     * Round the number to an integer
     * @param d The number value
     * @param roundingMode Rounding mode determining to round up or down
     * @return The new rounded number
     */
    public static int roundToInt(double d, RoundingMode roundingMode) {
        return new BigDecimal(d).setScale(0, roundingMode).intValue();
    }

    /**
     * Round the number to an integer
     * @param d The number value
     * @return The new rounded number
     */
    public static int roundToInt(double d) {
        return new BigDecimal(d).setScale(0, defaultRoundingMode).intValue();
    }

    /**
     * Round the number up to an integer
     * @param d The number value
     * @return The new rounded number
     */
    public static int roundUp(double d) {
        return new BigDecimal(d).setScale(0, RoundingMode.UP).intValue();
    }

    /**
     * Round the number down to an integer
     * @param d The number value
     * @return The new rounded number
     */
    public static int roundDown(double d) {
        return new BigDecimal(d).setScale(0, RoundingMode.DOWN).intValue();
    }

    /**
     * Round the number to an integer
     * @param d The number value
     * @param roundingMode Rounding mode determining to round up or down
     * @return The new rounded number
     */
    public static int roundToInt(float d, RoundingMode roundingMode) {
        return new BigDecimal(d).setScale(0, roundingMode).intValue();
    }

    /**
     * Round the number up to an integer
     * @param d The number value
     * @return The new rounded number
     */
    public static int roundUp(float d) {
        return new BigDecimal(d).setScale(0, RoundingMode.UP).intValue();
    }

    /**
     * Round the number down to an integer
     * @param d The number value
     * @return The new rounded number
     */
    public static int roundDown(float d) {
        return new BigDecimal(d).setScale(0, RoundingMode.DOWN).intValue();
    }

    /**
     * Round the number to an integer
     * @param d The number value
     * @param roundingMode Rounding mode determining to round up or down
     * @return The new rounded number
     */
    public static int roundToInt(BigDecimal d, RoundingMode roundingMode) {
        return d.setScale(0, roundingMode).intValue();
    }

    /**
     * Round the number up to an integer
     * @param d The number value
     * @return The new rounded number
     */
    public static int roundUp(BigDecimal d) {
        return d.setScale(0, RoundingMode.UP).intValue();
    }

    /**
     * Round the number down to an integer
     * @param d The number value
     * @return The new rounded number
     */
    public static int roundDown(BigDecimal d) {
        return d.setScale(0, RoundingMode.DOWN).intValue();
    }
}
