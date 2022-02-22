package io.oreto.latte.map;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * MultiMap with lists of type Integer
 * @param <K> The type of the map keys
 */
public class MultiInt<K> extends MultiMap<K, Integer> {
    /**
     * Sum all the integer lists in the multimap and return a new Map with the summed values
     * @return Map with summed values of each mapped list
     */
    public Map<K, Integer> sum() {
        return keySet().stream()
                .collect(Collectors.toMap(it -> it, it -> get(it).stream()
                        .mapToInt(Integer::intValue).sum()));
    }
}
