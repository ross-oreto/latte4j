package io.oreto.latte.map;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * MultiMap with lists of type Long
 * @param <K> The type of the map keys
 */
public class MultiLong<K> extends MultiMap<K, Long> {
    /**
     * Sum all the long lists in the multimap and return a new Map with the summed values
     * @return Map with summed values of each mapped list
     */
    public Map<K, Long> sum() {
        Map<K, List<Long>> map = asMap();
        Map<K, Long> sums = new LinkedHashMap<>();
        for (K k : map.keySet()) {
            long total = 0;
            for (long n : map.get(k)) {
                total += n;
            }
            sums.put(k, total);
        }
        return sums;
    }
}
