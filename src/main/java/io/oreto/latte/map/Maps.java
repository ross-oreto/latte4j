package io.oreto.latte.map;

import java.util.LinkedHashMap;
import java.util.Map;

public class Maps {
    @SafeVarargs
    public static <K, V> Map<K, V> of(E<K, V>... entries) {
        Map<K, V> kvMap = new LinkedHashMap<>();
        for (E<K, V> entry : entries)
            kvMap.put(entry.key(), entry.val());
        return kvMap;
    }
}
