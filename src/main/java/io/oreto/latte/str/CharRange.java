package io.oreto.latte.str;

import io.oreto.latte.Range;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public class CharRange extends Range<Character> {
    /**
     * Map each character in the range to a value R
     * @param mapper Function which maps the character to the type R
     * @return List of each mapped character
     */
    @Override
    public <R> List<R> map(Function<Character, R> mapper) {
        List<R> l = new ArrayList<>();
        int step = 1;
        int len = inclusive ? to() + step : to();
        for (int i = from(); i < len; i+=step) {
            l.add(mapper.apply((char) i));
        }
        return l;
    }

    /**
     * Iterate through each character in the range applying the consumer on each value.
     * @param consumer Consumer to pass each value in the range to as an argument
     */
    @Override
    public void each(Consumer<Character> consumer) {
        int step = 1;
        int len = inclusive ? to() + step : to();
        for (int i = from(); i < len; i+=step) {
            consumer.accept((char) i);
        }
    }

    /**
     * Set the lower bound
     * @param from the lower bound
     * @return This range
     */
    @Override
    public CharRange from(Character from){ super.from(from); return this; }

    /**
     * Set the upper bound
     * @param to the upper bound
     * @return This range
     */
    @Override
    public CharRange to(Character to){ super.to(to); return this; }

    /**
     * Set the upper bound, not inclusive
     * @param to the upper bound
     * @return This range
     */
    @Override
    public CharRange until(Character to){ super.until(to); return this; }
}
