package io.oreto.latte.collections;

import java.util.*;

/**
 * Represents a set that allows duplicates
 * @param <T>
 */
public class MultiSet<T extends Comparable<T>> implements Iterable<T> {

    protected static <T extends Comparable<T>> Comparator<T> defaultComparator() {
        return Comparator.nullsFirst(Comparator.naturalOrder());
    }

    // Iterator for the case of an empty set
    protected final Iterator<T> emptyIterator = new Iterator<T>() {
        @Override
        public boolean hasNext() { return false; }
        @Override
        public T next() { throw new NoSuchElementException(); }
    };

    /**
     * Class to keep track of the number of value duplicates using a count
     */
    protected static class IVal {
        private int i;

        public IVal() {
            this.i = 1;
        }
    }

    // back this data structure with a tree map
    private final TreeMap<T, IVal> tMap;
    private int size;

    public MultiSet(Comparator<T> comparator) {
        this.tMap = new TreeMap<>(comparator);
        this.size = 0;
    }

    public MultiSet() {
        this(defaultComparator());
    }

    /**
     * Returns the number of elements in this set (its cardinality).
     * @return the number of elements in this set (its cardinality)
     */
    public int size() {
        return size;
    }

    /**
     * Returns {@code true} if this set contains no elements.
     * @return {@code true} if this set contains no elements
     */
    public boolean isEmpty() {
        return tMap.isEmpty();
    }

    /**
     * Returns {@code true} if this set contains one element or more.
     * @return {@code true} if this set contains one element or more.
     */
    public boolean isNotEmpty() {
        return !tMap.isEmpty();
    }


    /**
     * Returns {@code true} if this set contains the specified element.
     * More formally, returns {@code true} if and only if this set
     * contains an element {@code e} such that
     * <tt>(o==null&nbsp;?&nbsp;e==null&nbsp;:&nbsp;o.equals(e))</tt>.
     *
     * @param t object to be checked for containment in this set
     * @return {@code true} if this set contains the specified element
     * @throws ClassCastException if the specified object cannot be compared
     *         with the elements currently in the set
     */
    public boolean contains(T t) {
        return tMap.containsKey(t);
    }

    /**
     * Adds the specified element to this set if it is not already present.
     * More formally, adds the specified element {@code e} to this set if
     * the set contains no element {@code e2} such that
     * <tt>(e==null&nbsp;?&nbsp;e2==null&nbsp;:&nbsp;e.equals(e2))</tt>.
     * If this set already contains the element, the call leaves the set
     * unchanged and returns {@code false}.
     *
     * @param t element to be added to this set
     * @return the multiset
     * @throws ClassCastException if the specified object cannot be compared
     *         with the elements currently in this set
     * @throws NullPointerException if the comparator does not permit null elements
     */
    public MultiSet<T> add(T t) {
        IVal iVal = tMap.get(t);
        if (iVal == null)
            tMap.put(t, new IVal());
        else
            iVal.i++;
        size++;
        return this;
    }

    /**
     * Adds all of the elements in the specified collection to this set.
     * @param collection containing elements to be added to this set
     * @return the multiset
     * @throws ClassCastException if the elements provided cannot be compared
     *         with the elements currently in the set
     * @throws NullPointerException if the comparator does not permit null elements
     */
    @SafeVarargs
    public final MultiSet<T> addAll(T... collection) {
        for (T t : collection)
            add(t);
        return this;
    }

    /**
     * Adds all of the elements in the specified collection to this set.
     * @param collection containing elements to be added to this set
     * @return the multiset
     * @throws ClassCastException if the elements provided cannot be compared
     *         with the elements currently in the set
     * @throws NullPointerException if the comparator does not permit null elements
     */
    public MultiSet<T> addAll(Collection<T> collection) {
        for (T t : collection)
            add(t);
        return this;
    }

    /**
     * Removes the specified element from this set if it is present.
     * More formally, removes an element {@code e} such that
     * <tt>(o==null&nbsp;?&nbsp;e==null&nbsp;:&nbsp;o.equals(e))</tt>,
     * if this set contains such an element.  Returns {@code true} if
     * this set contained the element (or equivalently, if this set
     * changed as a result of the call).  (This set will not contain the
     * element once the call returns.)
     *
     * @param t object to be removed from this set, if present
     * @return the multiset
     * @throws ClassCastException if the specified object cannot be compared
     *         with the elements currently in this set
     * @throws NullPointerException if the comparator does not permit null elements
     */
    public MultiSet<T> remove(T t) {
        IVal iVal = tMap.get(t);
        if (iVal != null) {
            iVal.i--;
            if (iVal.i == 0)
                tMap.remove(t);
            size--;
        }
        return this;
    }

    /**
     * Removes from this set all of its elements that are contained in the
     * specified collection (optional operation).  If the specified
     * collection is also a set, this operation effectively modifies this
     * set so that its value is the <i>asymmetric set difference</i> of
     * the two sets.
     *
     * @param  collection containing elements to be removed from this set
     * @return The multiset

     * @throws ClassCastException if the class of an element of this set
     *         is incompatible with the specified collection
     * (<a href="Collection.html#optional-restrictions">optional</a>)
     * @throws NullPointerException if this set contains a null element and the
     *         specified collection does not permit null elements
     * @see #remove(T)
     * @see #contains(T)
     */
    @SafeVarargs
    public final MultiSet<T> removeAll(T... collection) {
        for (T t : collection)
            remove(t);
        return this;
    }

    /**
     * Removes from this set all of its elements that are contained in the
     * specified collection (optional operation).  If the specified
     * collection is also a set, this operation effectively modifies this
     * set so that its value is the <i>asymmetric set difference</i> of
     * the two sets.
     *
     * @param  collection containing elements to be removed from this set
     * @return The multiset

     * @throws ClassCastException if the class of an element of this set
     *         is incompatible with the specified collection
     * (<a href="Collection.html#optional-restrictions">optional</a>)
     * @throws NullPointerException if this set contains a null element and the
     *         specified collection does not permit null elements
     * @see #remove(T)
     * @see #contains(T)
     */
    public MultiSet<T> removeAll(Collection<T> collection) {
        for (T t : collection)
            remove(t);
        return this;
    }

    /**
     * Return the set elements as a list
      * @return A list of elements of type t
     */
    public List<T> toList() {
        List<T> list = new ArrayList<>();
        iterator().forEachRemaining(list::add);
        return list;
    }

    /**
     * Returns an array containing all of the elements in this list in
     * proper sequence (from first to last element); the runtime type of
     * the returned array is that of the specified array.  If the list fits
     * in the specified array, it is returned therein.  Otherwise, a new
     * array is allocated with the runtime type of the specified array and
     * the size of this list.
     *
     * <p>Suppose <tt>x</tt> is a list known to contain only strings.
     * The following code can be used to dump the list into a newly
     * allocated array of <tt>String</tt>:
     *
     * <pre>{@code
     *     String[] y = x.toArray(new String[0]);
     * }</pre>
     *
     * @param t the array into which the elements of this list are to
     *          be stored, if it is big enough; otherwise, a new array of the
     *          same runtime type is allocated for this purpose.
     * @return an array containing the elements of this list
     * @throws ArrayStoreException if the runtime type of the specified array
     *         is not a supertype of the runtime type of every element in
     *         this list
     * @throws NullPointerException if the specified array is null
     */
    public T[] toArray(T[] t) {
        return toList().toArray(t);
    }

    /**
     * Removes all of the elements from this set.
     * The set will be empty after this call returns.
     */
    public MultiSet<T> clear() {
        tMap.clear();
        size = 0;
        return this;
    }

    /**
     * Return a unique set
     * @return A set with all duplicates removed
     */
    public Set<T> unique() {
        return tMap.keySet();
    }

    /**
     * Returns an iterator over the elements in this set in order
     * @return an iterator over the elements in this set in order
     */
    @Override
    public Iterator<T> iterator() {
        Iterator<Map.Entry<T, IVal>> iterator = tMap.entrySet().iterator();
        if (iterator.hasNext()) {
            return new Iterator<T>() {
                Map.Entry<T, IVal> entry = iterator.next();
                int i = 0;

                @Override
                public boolean hasNext() {
                    return i < entry.getValue().i || iterator.hasNext();
                }
                @Override
                public T next() {
                    i++;
                    if (i > entry.getValue().i) {
                        entry = iterator.next();
                        i = 1;
                    }
                    return entry.getKey();
                }
            };
        } else
            return emptyIterator;
    }
}
