package io.oreto.latte.num;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Represents an BigInteger range
 */
public class BigIntRange extends NumRange<BigInteger> {
    /**
     * Create a new unbounded range
     * @return The new range
     */
    public static BigIntRange empty() {
        return new BigIntRange();
    }

    /**
     * Creates a new Range with bounds from and to
     * @param from the lower bound
     * @param to the upper bound
     * @return A new Range bounded between from and to
     */
    public static BigIntRange of(BigInteger from, BigInteger to) {
        return new BigIntRange().from(from).to(to);
    }

    /**
     * Creates a new Range with lower bound from
     * @param from the lower bound
     * @return A new range
     */
    public static BigIntRange From(BigInteger from) {
        return new BigIntRange().from(from);
    }

    /**
     * Creates a new Range with upper bound to
     * @param to the upper bound
     * @return A new range
     */
    public static BigIntRange To(BigInteger to) {
        return new BigIntRange().from(BigInteger.ZERO).to(to);
    }

    /**
     * Creates a new Range with upper bound to, not inclusive
     * @param to the upper bound, not inclusive
     * @return A new range
     */
    public static BigIntRange Until(BigInteger to) {
        return new BigIntRange().from(BigInteger.ZERO).until(to);
    }


    /**
     * The default size of the step for iteration
     * @return The size of the step
     */
    @Override
    protected BigInteger defaultStep() { return BigInteger.valueOf(1L); }

    /**
     * Map each number value in the range to a value R
     * @param mapper Function which maps the number to the type R
     * @return List of each mapped number
     */
    @Override
    public <T> List<T> map(Function<BigInteger, T> mapper) {
        List<T> l = new ArrayList<>();
        BigInteger step = by();
        BigInteger len = inclusive ? to().add(step) : to();
        for (BigInteger i = from(); i.compareTo(len) < 0; i = i.add(step)) {
            l.add(mapper.apply(i));
        }
        return l;
    }


    /**
     * Iterate through each number in the range applying the consumer on each value.
     * @param consumer Consumer to pass each value in the range to as an argument
     */
    @Override
    public void each(Consumer<BigInteger> consumer) {
        BigInteger step = by();
        BigInteger len = inclusive ? to().add(step) : to();
        for (BigInteger i = from(); i.compareTo(len) < 0; i = i.add(step)) {
            consumer.accept(i);
        }
    }

    /**
     * Set the lower bound
     * @param from the lower bound
     * @return This range
     */
    @Override
    public BigIntRange from(BigInteger from){ super.from(from); return this; }

    /**
     * Set the upper bound
     * @param to the upper bound
     * @return This range
     */
    @Override
    public BigIntRange to(BigInteger to){ super.to(to); return this; }

    /**
     * Set the upper bound, not inclusive
     * @param to the upper bound
     * @return This range
     */
    @Override
    public BigIntRange until(BigInteger to){ super.until(to); return this; }

    /**
     * Determine if the element is in the bounds of this range
     * @param element The element to test
     * @return True if the element is in the bounds defined in the range, false otherwise
     */
    @Override
    public boolean in(BigInteger element) {
        return super.in(element) && element.subtract(from()).remainder(by()).equals(BigInteger.ZERO);
    }
}
