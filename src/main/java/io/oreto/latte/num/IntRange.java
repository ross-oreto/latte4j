package io.oreto.latte.num;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Represents an Integer range
 */
public class IntRange extends NumRange<Integer> {

    /**
     * Create a new unbounded range
     * @return The new range
     */
    public static IntRange empty() {
        return new IntRange();
    }

    /**
     * Creates a new Range with bounds from and to
     * @param from the lower bound
     * @param to the upper bound
     * @return A new Range bounded between from and to
     */
    public static IntRange of(Integer from, Integer to) {
        return new IntRange().from(from).to(to);
    }

    /**
     * Creates a new Range with lower bound from
     * @param from the lower bound
     * @return A new range
     */
    public static IntRange From(Integer from) {
        return new IntRange().from(from);
    }

    /**
     * Creates a new Range with upper bound to
     * @param to the upper bound
     * @return A new range
     */
    public static IntRange To(Integer to) {
        return new IntRange().from(0).to(to);
    }

    /**
     * Creates a new Range with upper bound to, not inclusive
     * @param to the upper bound, not inclusive
     * @return A new range
     */
    public static IntRange Until(Integer to) {
        return new IntRange().from(0).until(to);
    }

    /**
     * The default size of the step for iteration
     * @return The size of the step
     */
    @Override
    protected Integer defaultStep() { return 1; }

    /**
     * Map each number value in the range to a value R
     * @param mapper Function which maps the number to the type R
     * @return List of each mapped number
     */
    @Override
    public <T> List<T> map(Function<Integer, T> mapper) {
        List<T> l = new ArrayList<>();
        int step = by();
        int len = inclusive ? to() + step : to();
        for (int i = from(); i < len; i+=step) {
            l.add(mapper.apply(i));
        }
        return l;
    }

    /**
     * Iterate through each number in the range applying the consumer on each value.
     * @param consumer Consumer to pass each value in the range to as an argument
     */
    @Override
    public void each(Consumer<Integer> consumer) {
        int step = by();
        int len = inclusive ? to() + step : to();
        for (int i = from(); i < len; i+=step) {
            consumer.accept(i);
        }
    }

    /**
     * Set the lower bound
     * @param from the lower bound
     * @return This range
     */
    @Override
    public IntRange from(Integer from){ super.from(from); return this; }

    /**
     * Set the upper bound
     * @param to the upper bound
     * @return This range
     */
    @Override
    public IntRange to(Integer to){ super.to(to); return this; }

    /**
     * Set the upper bound, not inclusive
     * @param to the upper bound
     * @return This range
     */
    @Override
    public IntRange until(Integer to){ super.until(to); return this; }

    /**
     * Determine if the element is in the bounds of this range
     * @param element The element to test
     * @return True if the element is in the bounds defined in the range, false otherwise
     */
    @Override
    public boolean in(Integer element) {
        return super.in(element) && (element - from()) % by() == 0;
    }
}
