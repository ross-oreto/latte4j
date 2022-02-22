package io.oreto.latte.map;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
        Map<K, List<Double>> map = asMap();
        Map<K,Double> sums = new LinkedHashMap<>();
        for (K k : map.keySet()) {
            double total = 0;
            for (double n : map.get(k)) {
                total += n;
            }
            sums.put(k, total);
        }
        return sums;
    }
}
