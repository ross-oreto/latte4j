package io.oreto.latte.map;

import java.util.Map;
import java.util.stream.Collectors;

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
        return keySet().stream()
                .collect(Collectors.toMap(it -> it, it -> get(it).stream()
                        .mapToLong(Long::longValue).sum()));
    }
}
