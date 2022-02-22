package io.oreto.latte;

import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Represents a range of any Comparable type
 * @param <T> The type of the range
 */
public abstract class Range<T> implements Comparable<T> {

    /**
     * Determine if the element is in the bounds from and to
     * @param element The element to test
     * @param from the lower bound
     * @param to the upper bound
     * @param inclusive if true the upper bound is included {@code (<=)} in the bounds
     *                  , otherwise the test is strictly less than upper bound
     * @param <T> The type of the Range
     * @return True if the element is in the bounds from and to, false otherwise
     */
    public static <T extends Comparable<T>> boolean in(T element, T from, T to, boolean inclusive) {
        int l = element.compareTo(from);
        int r = element.compareTo(to);
        return inclusive
                ? l >= 0 && r <= 0
                : l >= 0 && r < 0;
    }

    /**
     * Determine if the element is in the bounds from and to
     * @param element The element to test
     * @param from the lower bound
     * @param to the upper bound
     * @param <T> The type of the Range
     * @return True if the element is in the bounds from and to, false otherwise
     */
    public static <T extends Comparable<T>> boolean in(T element, T from, T to) {
        return in(element, from, to, false);
    }

    /**
     * Determine if the element is in the bounds from and to
     * @param element The element to test
     * @param from the lower bound
     * @param to the upper bound
     * @param comp The Comparator used to test the bounds with
     * @param inclusive if true the upper bound is included {@code (<=)} in the bounds
     *                  , otherwise the test is strictly less than upper bound
     * @param <T> The type of the Range
     * @return True if the element is in the bounds from and to, false otherwise
     */
    public static <T> boolean in(T element, T from, T to, Comparator<T> comp, boolean inclusive) {
        int l = comp.compare(element, from);
        int r = comp.compare(element, to);
        return inclusive
                ? l >= 0 && r <= 0
                : l >= 0 && r < 0;
    }

    /**
     * Determine if the element is in the bounds from and to
     * @param element The element to test
     * @param from the lower bound
     * @param to the upper bound
     * @param comp The Comparator used to test the bounds with
     * @param <T> The type of the Range
     * @return True if the element is in the bounds from and to, false otherwise
     */
    public static <T> boolean in(T element, T from, T to, Comparator<T> comp) {
        return in(element, from, to, comp, false);
    }

    @SuppressWarnings("unchecked")
    private Comparator<T> comparator = (o1, o2) -> ((Comparable<T>)o1).compareTo(o2);

    private T from;
    private T to;
    protected boolean inclusive;

    protected Range() { }

    protected Range(T from, T to) {
        this.from = from;
        this.to = to;
    }

    protected Range(T from, T to, Comparator<T> comparator) {
        this(from, to);
        this.comparator = comparator;
    }

    /**
     * Get the lower bound
     * @return The lower bound of the range
     */
    public T from(){ return from; }

    /**
     * Set the lower bound
     * @param from the lower bound
     * @return This range
     */
    public Range<T> from(T from){ this.from = from; return this; }

    /**
     * Get the upper bound
     * @return The upper bound of the range
     */
    public T to(){ return to; }

    /**
     * Set the upper bound
     * @param to the upper bound
     * @return This range
     */
    public Range<T> to(T to){ this.to = to; this.inclusive = true; return this; }

    /**
     * Set the upper bound, not inclusive
     * @param until the upper bound
     * @return This range
     */
    public Range<T> until(T until){ this.to = until; this.inclusive = false; return this; }

    /**
     * Determine if the element is in the bounds of this range
     * @param element The element to test
     * @param comparator The Comparator used to test the bounds with
     * @return True if the element is in the bounds defined in the range, false otherwise
     */
    public boolean in(T element, Comparator<T> comparator) {
        return in(element, from, to, comparator, inclusive);
    }

    /**
     * Determine if the element is in the bounds of this range
     * @param element The element to test
     * @return True if the element is in the bounds defined in the range, false otherwise
     */
    public boolean in(T element) {
        return in(element, from, to, comparator, inclusive);
    }

    /**
     * Determine if every specified element is in the bounds of this range
     * @param element The elements to test
     * @return True if all the elements are in the bounds defined in the range, false otherwise
     */
    @SafeVarargs
    public final boolean allIn(T... element) {
        for (T t : element) {
            if (!this.in(t))
                return false;
        }
        return true;
    }

    /**
     * Determine if any of specified elements are in the bounds of this range
     * @param element The elements to test
     * @return True if any of the elements are in the bounds defined in the range, false otherwise
     */
    @SafeVarargs
    public final boolean anyIn(T... element) {
        for (T t : element) {
            if (this.in(t))
                return true;
        }
        return false;
    }

    /**
     * Determine if none of the specified elements are in the bounds of this range
     * @param element The elements to test
     * @return True if none any of the elements are in the bounds defined in the range, false otherwise
     */
    @SafeVarargs
    public final boolean noneIn(T... element) {
        return !anyIn(element);
    }

    /**
     * Determine if the range is considered empty
     * @return True if empty, false otherwise
     */
    public boolean isEmpty() {
        return from == null && to == null;
    }

    /**
     * Determine if the range is set, meaning not empty
     * @return True if not empty, false otherwise
     */
    public boolean isPresent() {
        return !isEmpty();
    }

    /**
     * Map each number value in the range to a value R
     * @param mapper Function which maps the number to the type R
     * @param <R> Type of the value mapped by each number in the range
     * @return List of each mapped number
     */
    abstract public <R> List<R> map(Function<T, R> mapper);

    /**
     * Iterate through each number in the range applying the consumer on each value.
     * @param consumer Consumer to pass each value in the range to as an argument
     */
    abstract public void each(Consumer<T> consumer);

    /**
     * A list representing each value in the range
     * @return A new list
     */
    public List<T> toList() {
        return map(it->it);
    }

    /**
     * A list representing each value in the range
     * @return A new list
     */
    @SuppressWarnings("unchecked")
    public T[] toArray() {
        return (T[]) toList().toArray();
    }

    /**
     * Compares this object with the specified object for order.  Returns a
     * negative integer, zero, or a positive integer as this object is less
     * than, equal to, or greater than the specified object.
     * @param   o the object to be compared.
     * @return  a negative integer, zero, or a positive integer as this object
     *          is less than, equal to, or greater than the specified object.
     */
    @Override
    public int compareTo(T o) {
        if (comparator.compare(o, from) < 0)
            return -1;
        else if (inclusive ? comparator.compare(o, to) >= 0 : comparator.compare(o, to) > 0)
            return 1;
        else
            return 0;
    }
}
