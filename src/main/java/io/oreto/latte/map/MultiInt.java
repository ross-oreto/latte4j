package io.oreto.latte.map;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
        Map<K, List<Integer>> map = asMap();
        Map<K, Integer> sums = new LinkedHashMap<>();
        for (K k : map.keySet()) {
            int total = 0;
            for (int n : map.get(k)) {
                total += n;
            }
            sums.put(k, total);
        }
        return sums;
    }
}
