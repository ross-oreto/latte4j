package io.oreto.latte.map;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;

/**
 * Util methods for Maps
 */
public class Maps {
    /**
     * Create new Map with entries
     * @param entries The entries in the new Map
     * @param <K> Type of map key
     * @param <V> Type of map value
     * @return A new map with the specified entries and types
     */
    @SafeVarargs
    public static <K, V> Map<K, V> of(E<K, V>... entries) {
        return hash(entries);
    }

    /**
     * Add entries to the provided map
     * @param map Map to add entries to
     * @param entries Entries to add to the map
     * @param <K> Type of the map keys
     * @param <V> Type of the map values
     */
    @SafeVarargs
    public static <K, V> void addEntries(Map<K, V> map, E<K, V>... entries) {
        for (E<K, V> entry : entries)
            map.put(entry.key(), entry.val());
    }

    /**
     * Create new LinkedHashMap with entries
     * @param entries The entries in the new Map
     * @param <K> Type of map key
     * @param <V> Type of map value
     * @return A new map with the specified entries and types
     */
    @SafeVarargs
    public static <K, V> LinkedHashMap<K, V> linked(E<K, V>... entries) {
        LinkedHashMap<K, V> kvMap = new LinkedHashMap<>();
        addEntries(kvMap, entries);
        return kvMap;
    }

    /**
     * Create new HashMap with entries
     * @param entries The entries in the new Map
     * @param <K> Type of map key
     * @param <V> Type of map value
     * @return A new HashMap with the specified entries and types
     */
    @SafeVarargs
    public static <K, V> HashMap<K, V> hash(E<K, V>... entries) {
        HashMap<K, V> kvMap = new HashMap<>();
        addEntries(kvMap, entries);
        return kvMap;
    }

    /**
     * Create new WeakHashMap with entries
     * @param entries The entries in the new Map
     * @param <K> Type of map key
     * @param <V> Type of map value
     * @return A new WeakHashMap with the specified entries and types
     */
    @SafeVarargs
    public static <K, V> WeakHashMap<K, V> weak(E<K, V>... entries) {
        WeakHashMap<K, V> kvMap = new WeakHashMap<>();
        addEntries(kvMap, entries);
        return kvMap;
    }

    /**
     * Create new TreeMap with entries
     * @param entries The entries in the new Map
     * @param <K> Type of map key
     * @param <V> Type of map value
     * @return A new TreeMap with the specified entries and types
     */
    @SafeVarargs
    public static <K extends Comparable<K>, V> TreeMap<K, V> tree(E<K, V>... entries) {
        TreeMap<K, V> kvMap = new TreeMap<>();
        addEntries(kvMap, entries);
        return kvMap;
    }

    /**
     * Create new ConcurrentHashMap with entries
     * @param entries The entries in the new Map
     * @param <K> Type of map key
     * @param <V> Type of map value
     * @return A new ConcurrentHashMap with the specified entries and types
     */
    @SafeVarargs
    public static <K, V> ConcurrentHashMap<K, V> concurrent(E<K, V>... entries) {
        ConcurrentHashMap<K, V> kvMap = new ConcurrentHashMap<>();
        addEntries(kvMap, entries);
        return kvMap;
    }

    /**
     * Create new ConcurrentSkipListMap with entries
     * @param entries The entries in the new Map
     * @param <K> Type of map key
     * @param <V> Type of map value
     * @return A new ConcurrentSkipListMap with the specified entries and types
     */
    @SafeVarargs
    public static <K extends Comparable<K>, V> ConcurrentSkipListMap<K, V> concurrentSkip(E<K, V>... entries) {
        ConcurrentSkipListMap<K, V> kvMap = new ConcurrentSkipListMap<>();
        addEntries(kvMap, entries);
        return kvMap;
    }

    /**
     * Create new ConcurrentNavigableMap with entries
     * @param entries The entries in the new Map
     * @param <K> Type of map key
     * @param <V> Type of map value
     * @return A new ConcurrentNavigableMap with the specified entries and types
     */
    @SafeVarargs
    public static <K extends Comparable<K>, V> ConcurrentNavigableMap<K, V> concurrentNavigable(E<K, V>... entries) {
        return concurrentSkip(entries);
    }

    /**
     * Create new IdentityHashMap with entries
     * @param entries The entries in the new Map
     * @param <K> Type of map key
     * @param <V> Type of map value
     * @return A new IdentityHashMap with the specified entries and types
     */
    @SafeVarargs
    public static <K, V> IdentityHashMap<K, V> identity(E<K, V>... entries) {
        IdentityHashMap<K, V> kvMap = new IdentityHashMap<>();
        addEntries(kvMap, entries);
        return kvMap;
    }

    /**
     * Create new EnumMap with entries
     * @param kClass Class of type K
     * @param entries The entries in the new Map
     * @param <K> Type of map key
     * @param <V> Type of map value
     * @return A new EnumMap with the specified entries and types
     */
    @SafeVarargs
    public static <K extends Enum<K>, V> EnumMap<K, V> enumMap(Class<K> kClass, E<K, V>... entries) {
        EnumMap<K, V> kvMap = new EnumMap<>(kClass);
        addEntries(kvMap, entries);
        return kvMap;
    }

    /**
     * Create new Set from iterable
     * @param iterable The iterable
     * @param <K> The type of elements in the iterable
     * @return A new Set
     */
    public static <K> Set<K> set(Iterable<K> iterable) {
        Set<K> set = new HashSet<>();
        iterable.forEach(set::add);
        return set;
    }
}
