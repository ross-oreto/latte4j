package io.oreto.latte.map;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * MultiMap with lists of type Double
 * @param <K> The type of the map keys
 */
public class MultiDouble<K> extends MultiMap<K, Double> {
    /**
     * Sum all the double lists in the multimap and return a new Map with the summed values
     * @return Map with summed values of each mapped list
     */
    public Map<K, Double> sum() {
        return asMap().keySet().stream()
                .collect(Collectors.toMap(it -> it, it -> get(it).stream()
                        .mapToDouble(Double::doubleValue).sum()));
    }
}
