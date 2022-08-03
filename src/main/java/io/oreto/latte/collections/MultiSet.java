package io.oreto.latte.collections;

import java.util.*;

public class MultiSet<T extends Comparable<T>> implements Iterable<T> {
    protected static <T extends Comparable<T>> int nullsFirst(T value, Key<T> key) {
        if (value == key.value) return 0;
        if (value == null) return -1;
        if (key.value == null) return 1;
        return value.compareTo(key.value);
    }

    protected final Iterator<T> emptyIterator = new Iterator<T>() {
        @Override
        public boolean hasNext() { return false; }
        @Override
        public T next() { throw new NoSuchElementException(); }
    };

    protected static class Key<T extends Comparable<T>> implements Comparable<Key<T>> {
        private int i;
        private final T value;

        public Key(T value) {
            this.i = 1;
            this.value = value;
        }

        @Override
        public int compareTo(Key<T> o) {
            return nullsFirst(value, o);
        }
    }

    private final TreeSet<Key<T>> tSet;
    private int size;

    public MultiSet() {
       this.tSet = new TreeSet<>();
       this.size = 0;
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return tSet.isEmpty();
    }

    public boolean isNotEmpty() {
        return !tSet.isEmpty();
    }

    public boolean contains(T t) {
        for (Key<T> key : tSet) {
            int i = nullsFirst(t, key);
            if (i == 0)
                return true;
            else if (i < 0)
                return false;
        }
        return false;
    }

    public MultiSet<T> add(T t) {
        for (Key<T> key : tSet) {
            int i = nullsFirst(t, key);
            if (i == 0) {
                key.i++;
                size++;
                return this;
            } else if (i < 0)
                break;
        }
        tSet.add(new Key<>(t));
        size++;
        return this;
    }

    @SafeVarargs
    public final MultiSet<T> addAll(T... collection) {
        for (T t : collection)
            add(t);
        return this;
    }

    public MultiSet<T> addAll(Collection<T> collection) {
        for (T t : collection)
            add(t);
        return this;
    }

    public MultiSet<T> remove(T t) {
        for (Key<T> key : tSet) {
            int i = nullsFirst(t, key);
            if (i == 0) {
                key.i--;
                if (key.i == 0) {
                    tSet.remove(key);
                }
                size--;
                return this;
            } else if (i < 0)
                break;
        }
        return this;
    }

    @SafeVarargs
    public final MultiSet<T> removeAll(T... collection) {
        for (T t : collection)
            remove(t);
        return this;
    }

    public MultiSet<T> removeAll(Collection<T> collection) {
        for (T t : collection)
            remove(t);
        return this;
    }

    public List<T> toList() {
        List<T> list = new ArrayList<>();
        iterator().forEachRemaining(list::add);
        return list;
    }

    public T[] toArray(T[] t) {
        return toList().toArray(t);
    }

    public MultiSet<T> clear() {
        tSet.clear();
        size = 0;
        return this;
    }

    @Override
    public Iterator<T> iterator() {
        Iterator<Key<T>> iterator = tSet.iterator();
        if (iterator.hasNext()) {
            return new Iterator<T>() {
                Key<T> key = iterator.next();
                int i = 0;

                @Override
                public boolean hasNext() {
                    return i < key.i || iterator.hasNext();
                }
                @Override
                public T next() {
                    i++;
                    if (i > key.i) {
                        key = iterator.next();
                        i = 1;
                    }
                    return key.value;
                }
            };
        } else
            return emptyIterator;
    }
}
