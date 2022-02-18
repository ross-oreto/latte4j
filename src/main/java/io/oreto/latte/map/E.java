package io.oreto.latte.map;

public class E<K, V> {
    public static <K, V> E<K, V> of(K k, V v) {
        return new E<>(k, v);
    }

    private final K key;
    private final V val;

    public E(K key, V val) {
        this.key = key;
        this.val = val;
    }

    public K key() {
        return key;
    }
    public V val() {
        return val;
    }
}
