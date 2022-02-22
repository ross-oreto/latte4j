package io.oreto.latte.map;

import java.util.*;

/**
 * A multimap implementation.
 * @param <K> The type of the map keys
 * @param <V> The type of the map values
 */
public class MultiMap<K, V> {
    /**
     * Create new MultiMap
     * @param <K> The type of the map keys
     * @param <V> The type of the map list values
     * @return A new MultiMap
     */
    @SafeVarargs
    static <K, V> MultiMap<K, V> of(E<K, V>... entries) {
        MultiMap<K, V> multiMap = new MultiMap<>();
        for (E<K, V> e : entries)
            multiMap.put(e.key(), e.val());
        return multiMap;
    }

    private final Map<K, List<V>> map;
    private boolean allowEmptyValues = true;

    public MultiMap() {
        this.map = initMap();
    }

    /**
     * Creates the map which backs the multimap
     * Override this use a different type of map used by the multimap
     * @return A new Map implementation
     */
    protected Map<K, List<V>> initMap() {
       return new WeakHashMap<>();
    }

    /**
     * Do not allow null or empty values in the map
     * @return This multimap
     */
    public MultiMap<K, V> disallowEmptyValues(){ this.allowEmptyValues = false; return this; }

    /**
     * Allow null or empty values in the map
     * @return This multimap
     */
    public MultiMap<K, V> allowEmptyValues(){ this.allowEmptyValues = true; return this; }

    /**
     * Associates the specified value with the specified key in this map
     * If keys already exists the value is added to the existing list
     * If allowEmptyValues is false then the value cannot be null or empty string
     * @param k key with which the specified value is to be associated
     * @param v value to be associated with the specified key
     * @return The multimap being added to
     */
    public final MultiMap<K, V> put(K k, V v) {
        if (allowEmptyValues || (v instanceof String && !"".equals(v)) || (!(v instanceof String) && v != null)) {
            if (map.containsKey(k)) map.get(k).add(v);
            else map.put(k, new ArrayList<V>() {{
                add(v);
            }});
        }
        return this;
    }

    /**
     * Add key to the map and associate the key with an empty list.
     * @param k key with which the empty list is to be associated
     * @return The multimap being added to
     */
    public final MultiMap<K, V> put(K k){
        if (!map.containsKey(k)) map.put(k, new ArrayList<>());
        return this;
    }

    /**
     * Add the map entries in m to this multimap
     * @param m The map entries to add in
     * @return The multimap being added to
     */
    public final MultiMap<K, V> putAll(Map<? extends K, ? extends V> m) {
        m.forEach(this::put);
        return this;
    }

    /**
     * Add the map entries in m to this multimap
     * @param entries The entries to add in
     * @return The multimap being added to
     */
    public final MultiMap<K, V> putAll(Iterable<Map.Entry<K, V>> entries) {
        entries.forEach(it -> put(it.getKey(), it.getValue()));
        return this;
    }

    /**
     * Returns the list to which the specified key is mapped,
     * or null if this map contains no mapping for the key.
     * @param k the key whose associated value is to be returned
     * @return the value to which the specified key is mapped, or
     *         {@code null} if this map contains no mapping for the key
     */
    public final List<V> get(K k) {
        return map.get(k);
    }

    /**
     * Returns the optional list to which the specified key is mapped,
     * or Optional.empty if this map contains no mapping for the key.
     * @param k the key whose associated value is to be returned
     * @return optional value to which the specified key is mapped, or
     *         {@code Optional.empty()} if this map contains no mapping for the key
     */
    public final Optional<List<V>> listAt(K k) {
        List<V> l = get(k);
        return l == null ? Optional.empty() : Optional.of(l);
    }

    /**
     * Returns the number of key-value mappings in this map. If the
     * map contains more than <tt>Integer.MAX_VALUE</tt> elements, returns
     * <tt>Integer.MAX_VALUE</tt>.
     * @return the number of key-value mappings in this map
     */
    public final int size() {
        return map.size();
    }

    /**
     * Returns the number of elements in this list which the specified key is mapped. If this list contains
     * more than <tt>Integer.MAX_VALUE</tt> elements, returns
     * <tt>Integer.MAX_VALUE</tt>.
     * @param k the key whose associated list size is to be returned
     * @return the number of elements in the list which the specified key is mapped
     */
    public final Optional<Integer> sizeAt(K k) {
        return listAt(k).map(List::size);
    }

    /**
     * Get all entries in which the list value contains the value v
      * @param v The value to search lists for
     * @return A collection of entries with a list value that contains v
     */
    public Collection<Map.Entry<K, List<V>>> entriesWith(V v) {
        Collection<Map.Entry<K, List<V>>> entries = new ArrayList<>();
        for (K k : map.keySet()) {
            if (get(k).contains(v))
                entries.add(new AbstractMap.SimpleEntry<>(k, get(k)));
        }
        return entries;
    }

    /**
     * Returns <tt>true</tt> if this map contains a mapping for the specified
     * key.  More formally, returns <tt>true</tt> if and only if
     * this map contains a mapping for a key <tt>k</tt> such that
     * <tt>(key==null ? k==null : key.equals(k))</tt>.  (There can be
     * at most one such mapping.)
     * @param k key whose presence in this map is to be tested
     * @return <tt>true</tt> if this map contains a mapping for the specified
     *         key
     */
    public final boolean containsKey(K k) {
        return map.containsKey(k);
    }

    /**
     * Returns <tt>true</tt> if this map contains no key-value mappings.
     * @return <tt>true</tt> if this map contains no key-value mappings
     */
    public final boolean isEmpty() {
        return map.isEmpty();
    }

    /**
     * Returns <tt>true</tt> if any list in the map contains the value v
     * @param v The value to search for
     * @return <tt>true</tt> if any list contains the specified value
     */
    public final boolean containsValue(V v) {
        for (K k : map.keySet()) {
            if (get(k).contains(v))
                return true;
        }
        return false;
    }

    /**
     * Removes the mapping for a key from this map if it is present
     * (optional operation).   More formally, if this map contains a mapping
     * from key <tt>k</tt> to value <tt>v</tt> such that
     * <code>(key==null ?  k==null : key.equals(k))</code>, that mapping
     * is removed.  (The map can contain at most one such mapping.)
     * @param key key whose mapping is to be removed from the map
     * @return This multimap
     */
    public final MultiMap<K, V> remove(K key) {
        map.remove(key);
        return this;
    }

    /**
     * Removes all the elements from the list mapped by the specified key k
     * The list will be empty after this call returns.
     * @param key The key in which the associated list is cleared.
     * @return This multimap
     */
    public final MultiMap<K, V> clear(K key) {
        map.get(key).clear();
        return this;
    }

    /**
     * Removes all the mappings from this map (optional operation).
     * The map will be empty after this call returns.
     * @return This multimap
     */
    public final MultiMap<K, V> clear() {
        map.clear();
        return this;
    }

    /**
     * Returns a {@link Set} view of the keys contained in this map.
     * @return a set view of the keys contained in this map
     */
    public final Set<K> keySet() {
        return map.keySet();
    }

    /**
     * Returns a {@link Collection} view of the lists contained in this map
     * @return a collection view of the list contained in this map
     */
    public final Collection<List<V>> values() {
        return map.values();
    }

    @SuppressWarnings("unchecked")
    private final Comparator<V> comparator = (o1, o2) -> ((Comparable<V>)o1).compareTo(o2);
    @SuppressWarnings("unchecked")
    private final Comparator<V> descending = (o1, o2) -> ((Comparable<V>)o2).compareTo(o1);

    /**
     * Sort each list in the map
     * @param c The comparator used by sort.
     * @return A self referencing MultiMap to support a fluent api.
     */
    public MultiMap<K, V> sort(Comparator<? super V> c) {
        map.forEach(((k, vs) -> vs.sort(c)));
        return this;
    }

    /**
     * Sort using the default ascending comparator which expects the value V to implement Comparable[V]
     * @return  A self referencing MultiMap to support a fluent api.
     */
    public MultiMap<K, V> sort() {
        return sort(comparator);
    }

    /**
     * Sort using the default descending order comparator which expects the value V to implement Comparable[V]
     * @return  A self referencing MultiMap to support a fluent api.
     */
    public MultiMap<K, V> sortDescending() {
        return sort(descending);
    }

    /**
     * Get the underlying map which backs the multimap implementation
     * @return The underlying map which backs the multimap implementation
     */
    public Map<K, List<V>> asMap() { return map; }
}
