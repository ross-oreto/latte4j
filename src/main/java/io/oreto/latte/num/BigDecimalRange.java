package io.oreto.latte.num;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Represents an BigDecimal range
 */
public class BigDecimalRange extends NumRange<BigDecimal> {
    /**
     * Create a new unbounded range
     * @return The new range
     */
    public static BigDecimalRange empty() {
        return new BigDecimalRange();
    }

    /**
     * Creates a new Range with bounds from and to
     * @param from the lower bound
     * @param to the upper bound
     * @return A new Range bounded between from and to
     */
    public static BigDecimalRange of(BigDecimal from, BigDecimal to) {
        return new BigDecimalRange().from(from).to(to);
    }

    /**
     * Creates a new Range with lower bound from
     * @param from the lower bound
     * @return A new range
     */
    public static BigDecimalRange From(BigDecimal from) {
        return new BigDecimalRange().from(from);
    }

    /**
     * Creates a new Range with upper bound to
     * @param to the upper bound
     * @return A new range
     */
    public static BigDecimalRange To(BigDecimal to) {
        return new BigDecimalRange().from(BigDecimal.ZERO).to(to);
    }

    /**
     * Creates a new Range with upper bound to, not inclusive
     * @param to the upper bound, not inclusive
     * @return A new range
     */
    public static BigDecimalRange Until(BigDecimal to) {
        return new BigDecimalRange().from(BigDecimal.ZERO).until(to);
    }


    /**
     * The default size of the step for iteration
     * @return The size of the step
     */
    @Override
    protected BigDecimal defaultStep() { return BigDecimal.valueOf(1L); }

    /**
     * Map each number value in the range to a value R
     * @param mapper Function which maps the number to the type R
     * @return List of each mapped number
     */
    @Override
    public <T> List<T> map(Function<BigDecimal, T> mapper) {
        List<T> l = new ArrayList<>();
        BigDecimal step = by();
        BigDecimal len = inclusive ? to().add(step) : to();
        for (BigDecimal i = from(); i.compareTo(len) < 0; i = i.add(step)) {
            l.add(mapper.apply(i));
        }
        return l;
    }


    /**
     * Iterate through each number in the range applying the consumer on each value.
     * @param consumer Consumer to pass each value in the range to as an argument
     */
    @Override
    public void each(Consumer<BigDecimal> consumer) {
        BigDecimal step = by();
        BigDecimal len = inclusive ? to().add(step) : to();
        for (BigDecimal i = from(); i.compareTo(len) < 0; i = i.add(step)) {
            consumer.accept(i);
        }
    }

    /**
     * Set the lower bound
     * @param from the lower bound
     * @return This range
     */
    @Override
    public BigDecimalRange from(BigDecimal from){ super.from(from); return this; }

    /**
     * Set the upper bound
     * @param to the upper bound
     * @return This range
     */
    @Override
    public BigDecimalRange to(BigDecimal to){ super.to(to); return this; }

    /**
     * Set the upper bound, not inclusive
     * @param to the upper bound
     * @return This range
     */
    @Override
    public BigDecimalRange until(BigDecimal to){ super.until(to); return this; }

    /**
     * Determine if the element is in the bounds of this range
     * @param element The element to test
     * @return True if the element is in the bounds defined in the range, false otherwise
     */
    @Override
    public boolean in(BigDecimal element) {
        return super.in(element) && element.subtract(from()).remainder(by()).equals(BigDecimal.ZERO);
    }
}
