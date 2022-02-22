package io.oreto.latte.obj;

import java.util.Objects;
import java.util.function.Function;

/**
 * Allows safely accessing an object hierarchy without throwing a NullPointerException
 * @param <T> The type of object being accessed
 */
public class Safe<T> {
    /**
     * Create new Safe object
     * @param value The root of the safe value
     * @param <T> The type of the safe value
     * @return A new safe value
     */
    public static <T> Safe<T> of(T value) {
        return new Safe<>(value);
    }

    private final T value;

    private Safe(T value) {
        this.value = value;
    }

    /**
     * The method/field to get
     * @param function function that accepts one argument and produces a result.
     * @param <U> The type of value being queried
     * @return The safe object
     */
    public <U> Safe<U> q(Function<T, U> function) {
        U next = val(function);
        return of(next);
    }

    /**
     * Return the value of the safe object
     * @param function function that accepts one argument and produces a result.
     * @param <U> The type of value being queried
     * @return The value of the safe object
     */
    public <U> U val(Function<T, U> function) {
        Objects.requireNonNull(function);
        return (value == null) ? null : function.apply(value);
    }

    /**
     * Return the value of the safe object
     * @return The value of the safe object
     */
    public T val() {
        return value;
    }

    /**
     * Return the value of the safe object and if null the default value is returned
     * @param defaultVal The default value to use in case the safe object value is null
     * @return The value of the safe object
     */
    public T orElse(T defaultVal) {
        return (value == null) ? defaultVal : value;
    }
}
