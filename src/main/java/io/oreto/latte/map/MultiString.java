package io.oreto.latte.map;

import java.util.Map;
import java.util.stream.Collectors;

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
        return keySet().stream()
                .collect(Collectors.toMap(it -> it, it -> String.join(s, get(it))));
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
