package io.oreto.latte.num;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Represents a Double range
 */
public class DoubleRange extends NumRange<Double> {
    /**
     * Create a new unbounded range
     * @return The new range
     */
    public static DoubleRange empty() {
        return new DoubleRange();
    }

    /**
     * Creates a new Range with bounds from and to
     * @param from the lower bound
     * @param to the upper bound
     * @return A new Range bounded between from and to
     */
    public static DoubleRange of(Double from, Double to) {
        return new DoubleRange().from(from).to(to);
    }

    /**
     * Creates a new Range with lower bound from
     * @param from the lower bound
     * @return A new range
     */
    public static DoubleRange From(Double from) {
        return new DoubleRange().from(from);
    }

    /**
     * Creates a new Range with upper bound to
     * @param to the upper bound
     * @return A new range
     */
    public static DoubleRange To(Double to) {
        return new DoubleRange().from(0D).to(to);
    }

    /**
     * Creates a new Range with upper bound to, not inclusive
     * @param to the upper bound, not inclusive
     * @return A new range
     */
    public static DoubleRange Until(Double to) {
        return new DoubleRange().from(0D).until(to);
    }

    /**
     * The default size of the step for iteration
     * @return The size of the step
     */
    @Override
    protected Double defaultStep() { return 1D; }

    /**
     * Map each number value in the range to a value R
     * @param mapper Function which maps the number to the type R
     * @return List of each mapped number
     */
    @Override
    public <T> List<T> map(Function<Double, T> mapper) {
        List<T> l = new ArrayList<>();
        double step = by();
        double len = inclusive ? to() + step : to();
        for (double i = from(); i < len; i+=step) {
            l.add(mapper.apply(i));
        }
        return l;
    }

    /**
     * Iterate through each number in the range applying the consumer on each value.
     * @param consumer Consumer to pass each value in the range to as an argument
     */
    @Override
    public void each(Consumer<Double> consumer) {
        double step = by();
        double len = inclusive ? to() + step : to();
        for (double i = from(); i < len; i+=step) {
            consumer.accept(i);
        }
    }

    /**
     * Set the lower bound
     * @param from the lower bound
     * @return This range
     */
    @Override
    public DoubleRange from(Double from){ super.from(from); return this; }

    /**
     * Set the upper bound
     * @param to the upper bound
     * @return This range
     */
    @Override
    public DoubleRange to(Double to){ super.to(to); return this; }

    /**
     * Set the upper bound, not inclusive
     * @param to the upper bound
     * @return This range
     */
    @Override
    public DoubleRange until(Double to){ super.until(to); return this; }

    /**
     * Determine if the element is in the bounds of this range
     * @param element The element to test
     * @return True if the element is in the bounds defined in the range, false otherwise
     */
    @Override
    public boolean in(Double element) {
        return super.in(element) && (element - from()) % by() == 0;
    }
}
