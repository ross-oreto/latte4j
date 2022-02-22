package io.oreto.latte.time;

import io.oreto.latte.Range;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Represents an LocalDateTime range
 */
public class LocalDateTimeRange extends Range<LocalDateTime> {
    /**
     * Create a new unbounded range
     * @return The new range
     */
    public static LocalDateTimeRange empty() {
        return new LocalDateTimeRange();
    }

    /**
     * Creates a new Range with bounds from and to
     * @param from the lower bound
     * @param to the upper bound
     * @return A new Range bounded between from and to
     */
    public static LocalDateTimeRange of(LocalDateTime from, LocalDateTime to) {
        return new LocalDateTimeRange().from(from).to(to);
    }

    /**
     * Creates a new Range with lower bound from
     * @param from the lower bound
     * @return A new range
     */
    public static LocalDateTimeRange From(LocalDateTime from) {
        return new LocalDateTimeRange().from(from);
    }

    /**
     * Creates a new Range with upper bound to
     * @param to the upper bound
     * @return A new range
     */
    public static LocalDateTimeRange To(LocalDateTime to) {
        return new LocalDateTimeRange().from(LocalDateTime.now()).to(to);
    }

    /**
     * Creates a new Range with upper bound to
     * @param to the upper bound
     * @return A new range
     */
    public static LocalDateTimeRange To(Duration to) {
        LocalDateTime date = LocalDateTime.now();
        return new LocalDateTimeRange().from(date).to(date.plus(to)).by(to);
    }

    /**
     * Creates a new Range with upper bound to, not inclusive
     * @param to the upper bound, not inclusive
     * @return A new range
     */
    public static LocalDateTimeRange Until(LocalDateTime to) {
        return new LocalDateTimeRange().from(LocalDateTime.now()).until(to);
    }

    /**
     * Creates a new Range with upper bound to, not inclusive
     * @param to the upper bound, not inclusive
     * @return A new range
     */
    public static LocalDateTimeRange Until(Duration to) {
        LocalDateTime date = LocalDateTime.now();
        return new LocalDateTimeRange().from(date).until(date.plus(to)).by(to);
    }

    protected Duration step;

    /**
     * Set the incremental step used to iterate through this range
     * @param step The duration size of each step
     * @return This date range
     */
    public LocalDateTimeRange by(Duration step) { this.step = step; return this; }

    /**
     * Get the incremental step used to iterate through this range
     * @return This date range
     */
    public Duration by() { return step == null ? (step = defaultStep()) : step; }

    /**
     * The default size of the step for iteration
     * @return The size of the step
     */
    protected Duration defaultStep() { return Duration.ofDays(1); }

    /**
     * Map each date value in the range to a value R
     * @param mapper Function which maps the date to the type R
     * @return List of each mapped date
     */
    public <T> List<T> map(Function<LocalDateTime, T> mapper) {
        List<T> l = new ArrayList<>();
        Duration step = by();

        LocalDateTime len = inclusive ? to().plus(step) : to();
        for (LocalDateTime i = from(); i.compareTo(len) < 0; i = i.plus(step)) {
            l.add(mapper.apply(i));
        }
        return l;
    }

    /**
     * Iterate through each date in the range applying the consumer on each value.
     * @param consumer Consumer to pass each value in the range to as an argument
     */
    public void each(Consumer<LocalDateTime> consumer) {
        Duration step = by();
        LocalDateTime len = inclusive ? to().plus(step) : to();
        for (LocalDateTime i = from(); i.compareTo(len) < 0; i = i.plus(step)) {
            consumer.accept(i);
        }
    }

    /**
     * Set the lower bound
     * @param from the lower bound
     * @return This range
     */
    @Override
    public LocalDateTimeRange from(LocalDateTime from){ super.from(from); return this; }

    /**
     * Set the upper bound
     * @param to the upper bound
     * @return This range
     */
    @Override
    public LocalDateTimeRange to(LocalDateTime to){ super.to(to); return this; }

    /**
     * Set the upper bound
     * @param to the upper bound
     * @return This range
     */
    public LocalDateTimeRange to(Duration to) {
        super.to(from().plus(to));
        return this;
    }

    /**
     * Set the upper bound, not inclusive
     * @param to the upper bound
     * @return This range
     */
    @Override
    public LocalDateTimeRange until(LocalDateTime to){ super.until(to); return this; }

    /**
     * Set the upper bound, not inclusive
     * @param to the upper bound
     * @return This range
     */
    public LocalDateTimeRange until(Duration to) {
        super.until(from().plus(to));
        return this;
    }
}
