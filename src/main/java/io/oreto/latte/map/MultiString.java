package io.oreto.latte.map;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * MultiMap with lists of type String
 * @param <K> The type of the map keys
 */
public class MultiString<K> extends MultiMap<K, String> {

    /**
     * Join all the string lists in the multimap and return a new Map with the joined values
     * @param s The delimiter to join each list with
     * @return Map with joined string values of each mapped list
     */
    public Map<K, String> join(CharSequence s) {
        Map<K, List<String>> map = asMap();
        Map<K, String> joins = new LinkedHashMap<>();
        for (K k : map.keySet()) {
            joins.put(k, String.join(s, map.get(k)));
        }
        return joins;
    }

    /**
     * Join all the string lists in the multimap and return a new Map with the joined values
     * The delimiter to join each list with is a ','
     * @return Map with joined string values of each mapped list
     */
    public Map<K, String> join() {
        return join(",");
    }
}
