package io.oreto.latte.num;

import io.oreto.latte.Range;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * Represents a Number range
 * @param <T> The number type of the range
 */
abstract public class NumRange<T extends Number> extends Range<T> {
    /**
     * Determine if the number is in the bounds from and to
     * @param element The element to test
     * @param from the lower bound
     * @param to the upper bound
     * @param inclusive if true the upper bound is included (<=) in the bounds
     *                  , otherwise the test is strictly less than upper bound
     * @return True if the element is in the bounds from and to, false otherwise
     */
    public static boolean numberIn(Number element, Number from, Number to, boolean inclusive) {
        boolean in;
        if (element instanceof Integer) {
            in = NumRange.in((Integer) element, from.intValue(), to.intValue(), inclusive);
        } else if (element instanceof Byte) {
            in = NumRange.in((Byte) element, from.byteValue(), to.byteValue(), inclusive);
        } else if (element instanceof Short) {
            in = NumRange.in((Short) element, from.shortValue(), to.shortValue(), inclusive);
        } else if (element instanceof Long) {
            in = NumRange.in((Long) element, from.longValue(), to.longValue(), inclusive);
        } else if (element instanceof Float) {
            in = NumRange.in((Float) element, from.floatValue(), to.floatValue(), inclusive);
        } else if (element instanceof Double) {
            in = NumRange.in((Double) element, from.doubleValue(), to.doubleValue(), inclusive);
        } else if (element instanceof BigInteger) {
            in = NumRange.in((BigInteger) element, (BigInteger) from, (BigInteger) to, inclusive);
        } else if (element instanceof BigDecimal) {
            in = NumRange.in((BigDecimal) element, (BigDecimal) from, (BigDecimal) to, inclusive);
        } else {
            in = false;
        }
        return in;
    }

    protected T step;

    /**
     * Set the incremental step used to iterate through this range
     * @param step The number size of each step
     * @return This number range
     */
    public NumRange<T> by(T step) { this.step = step; return this; }

    /**
     * Get the current step number value
     * @return The size of the step
     */
    public T by() { return step == null ? (step = defaultStep()) : step; }

    /**
     * The default size of the step
     * @return The size of the step
     */
    abstract protected T defaultStep();
}
