package io.oreto.latte.collections;

import io.oreto.latte.obj.Reflect;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Util methods for any type of collection. List, arrays, iterables, iterators, streams.
 */
public class Lists {
    public static final long DEFAULT_SKIP = 0;
    public static final long DEFAULT_LIMIT = 20;

    public static final List<String> EMPTY_STRING_LIST = new ArrayList<String>();

    /**
     * Add an index to each element value in the collection
     * @param iterable An iterable collection
     * @param <T> Type of elements in the iterable
     * @return New list which combines an index and a value into an indexed value.
     */
    public static <T> List<Indexed<T>> withIndex(Iterable<T> iterable) {
        List<Indexed<T>> l = new ArrayList<>();
        int i = 0;
        for (T t : iterable) { l.add(Indexed.index(i, t)); i++; }
        return l;
    }

    /**
     * Create a list from the arguments t
     * @param t Elements in the new list
     * @param <T> The type of the elements
     * @return A new list containing the elements in t.
     */
    @SafeVarargs
    public static <T> List<T> of(T... t) {
        List<T> list = new ArrayList<>();
        Collections.addAll(list, t);
        return list;
    }

    /**
     * Create a list from the iterable
     * @param iterable Elements in the new list
     * @param <T> The type of the elements
     * @return A new list containing the elements in iterable.
     */
    public static <T> List<T> of(Iterable<T> iterable) {
        List<T> list = new ArrayList<>();
        iterable.forEach(list::add);
        return list;
    }

    /**
     * Create a set from the arguments t
     * @param t Elements in the new set
     * @param <T> The type of the elements
     * @return A new set containing the elements in t.
     */
    @SafeVarargs
    public static <T> Set<T> set(T... t) {
        Set<T> set = new HashSet<>();
        Collections.addAll(set, t);
        return set;
    }

    /**
     * Convert varargs to an array
     * @param t The vararg array
     * @param <T> Type of elements in the array
     * @return An array cast as T[]
     */
    @SafeVarargs
    public static <T> T[] array(T... t) {
        return t;
    }

    /**
     * Collate: collect and combine (texts, information, or sets of figures) in proper order.
     * Collates this iterable into sub-lists of length <code>size</code>.
     * stepping through the code <code>step</code>
     * Any remaining elements in the iterable after the subdivision will be dropped if <code>keep</code> is false.
     * @param iterable A collection which can be iterated
     * @param size The length of each sub-list in the returned list
     * @param step The number of elements to step through for each sub-list
     * @param keep If true, any remaining elements are returned as sub-lists. Otherwise, they are discarded
     * @param <T> The type of the collection
     * @return A List containing the data collated into sub-lists
     */
    public static <T> List<List<T>> collate(Iterable<T> iterable, int size, int step, boolean keep) {
        List<List<T>> collated = new ArrayList<>();
        Iterator<T> iterator = iterable.iterator();
        int p = 0;
        int steps = 0;
        while (iterator.hasNext()) {
            T t = iterator.next();
            steps++;
            if (steps == step) {
                collated.add(new ArrayList<>());
                steps = 0;
            }
            for (int i = p; i < collated.size(); i++) {
                List<T> l = collated.get(i);
                l.add(t);
                if (l.size() == size) p++;
            }
        }
        return keep ? collated : collated.subList(0, p);
    }

    /**
     * Collate: collect and combine (texts, information, or sets of figures) in proper order.
     * Collates this iterable into sub-lists of length <code>size</code>.
     * stepping through the code <code>step</code>
     * Any remaining elements in the iterable after the subdivision will be dropped if <code>keep</code> is false.
     * @param iterable A collection which can be iterated
     * @param size The length of each sub-list in the returned list
     * @param step The number of elements to step through for each sub-list
     * @param <T> The type of the collection
     * @return A List containing the data collated into sub-lists
     */
    public static <T> List<List<T>> collate(Iterable<T> iterable, int size, int step) {
        return collate(iterable, size, step, true);
    }

    /**
     * Collate: collect and combine (texts, information, or sets of figures) in proper order.
     * Collates this iterable into sub-lists of length <code>size</code>.
     * stepping through the code <code>step</code>
     * Any remaining elements in the iterable after the subdivision will be dropped if <code>keep</code> is false.
     * @param iterable A collection which can be iterated
     * @param size The length of each sub-list in the returned list
     * @param <T> The type of the collection
     * @return A List containing the data collated into sub-lists
     */
    public static <T> List<List<T>> collate(Iterable<T> iterable, int size) {
        return collate(iterable, size, 1);
    }

    /**
     * Collate: collect and combine (texts, information, or sets of figures) in proper order.
     * Collates this iterable into sub-lists of length <code>size</code>.
     * stepping through the code <code>step</code>
     * Any remaining elements in the iterable after the subdivision will be dropped if <code>keep</code> is false.
     * @param iterable A collection which can be iterated
     * @param size The length of each sub-list in the returned list
     * @param keep If true, any remaining elements are returned as sub-lists. Otherwise, they are discarded
     * @param <T> The type of the collection
     * @return A List containing the data collated into sub-lists
     */
    public static <T> List<List<T>> collate(Iterable<T> iterable, int size, boolean keep) {
        return collate(iterable, size, 1, keep);
    }

    /**
     * Collate: collect and combine (texts, information, or sets of figures) in proper order.
     * Collates this array into sub-lists of length <code>size</code>.
     * stepping through the code <code>step</code>
     * Any remaining elements in the array after the subdivision will be dropped if <code>keep</code> is false.
     * @param array An array
     * @param size The length of each sub-list in the returned list
     * @param step The number of elements to step through for each sub-list
     * @param keep If true, any remaining elements are returned as sub-lists. Otherwise, they are discarded
     * @param <T> The type of the collection
     * @return A List containing the data collated into sub-lists
     */
    public static <T> List<List<T>> collate(T[] array, int size, int step, boolean keep) {
        return collate(asIterable(array), size, step, keep);
    }

    /**
     * Collate: collect and combine (texts, information, or sets of figures) in proper order.
     * Collates this array into sub-lists of length <code>size</code>.
     * stepping through the code <code>step</code>
     * Any remaining elements in the array after the subdivision will be dropped if <code>keep</code> is false.
     * @param array An array
     * @param size The length of each sub-list in the returned list
     * @param step The number of elements to step through for each sub-list
     * @param <T> The type of the collection
     * @return A List containing the data collated into sub-lists
     */
    public static <T> List<List<T>> collate(T[] array, int size, int step) {
        return collate(asIterable(array), size, step);
    }

    /**
     * Collate: collect and combine (texts, information, or sets of figures) in proper order.
     * Collates this array into sub-lists of length <code>size</code>.
     * stepping through the code <code>step</code>
     * Any remaining elements in the array after the subdivision will be dropped if <code>keep</code> is false.
     * @param array An array
     * @param size The length of each sub-list in the returned list
     * @param <T> The type of the collection
     * @return A List containing the data collated into sub-lists
     */
    public static <T> List<List<T>> collate(T[] array, int size) {
        return collate(asIterable(array), size);
    }

    /**
     * Collate: collect and combine (texts, information, or sets of figures) in proper order.
     * Collates this array into sub-lists of length <code>size</code>.
     * stepping through the code <code>step</code>
     * Any remaining elements in the array after the subdivision will be dropped if <code>keep</code> is false.
     * @param array An array
     * @param size The length of each sub-list in the returned list
     * @param keep If true, any remaining elements are returned as sub-lists. Otherwise, they are discarded
     * @param <T> The type of the collection
     * @return A List containing the data collated into sub-lists
     */
    public static <T> List<List<T>> collate(T[] array, int size, boolean keep) {
        return collate(asIterable(array), size,  keep);
    }

    /**
     * Convert this array into an Iterable
     * @param array The array
     * @param <T> Type of the array
     * @return A new Iterable for the elements in the array
     */
    public static <T> Iterable<T> asIterable(T[] array) {
        return () -> Arrays.stream(array).iterator();
    }

    /**
     * Convert list into all possible sequentially ordered sub-lists
     * {@code ["a", "b", "c", "d"] => [ [a], [a, b], [a, b, c], [a, b, c, d] ] }
     * @param list The full list
     * @param <T> Type of the list items
     * @return A list of lists with type T that represent all the possible sub-lists in order.
     */
    public static <T> List<List<T>> subLists(List<T> list) {
        List<List<T>> lists = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            lists.add(list.subList(0, i + 1));
        }
        return lists;
    }

    /**
     * Page a stream into a list
     * @param stream A stream of elements
     * @param skip The number of leading elements to skip
     * @param limit The number of elements the stream should be limited to
     * @param order The Comparator to use for sorting/ordering the list
     * @param <T> Type of the elements in the stream
     * @return The resulting paged list
     */
    public static <T> List<T> page(Stream<T> stream, long skip, long limit, Comparator<T> order) {
        return stream
                .sorted(order)
                .skip(skip)
                .limit(limit)
                .collect(Collectors.toList());
    }

    /**
     * Page a stream into a list
     * @param stream A stream of elements
     * @param skip The number of leading elements to skip
     * @param limit The number of elements the stream should be limited to
     * @param <T> Type of the elements in the stream
     * @return The resulting paged list
     */
    public static <T> List<T> page(Stream<T> stream, long skip, long limit) {
        return stream
                .skip(skip)
                .limit(limit)
                .collect(Collectors.toList());
    }

    /**
     * Page a stream into a list using default skip and limit values.
     * @param stream A stream of elements
     * @param <T> Type of the elements in the stream
     * @return The resulting paged list
     */
    public static <T> List<T> page(Stream<T> stream) {
        return page(stream, DEFAULT_SKIP, DEFAULT_LIMIT);
    }

    /**
     * Page a stream into a list
     * @param stream A stream of elements
     * @param skip The number of leading elements to skip
     * @param <T> Type of the elements in the stream
     * @return The resulting paged list
     */
    public static <T> List<T> page(Stream<T> stream, long skip) {
        return page(stream, skip, DEFAULT_LIMIT);
    }

    /**
     * Page a stream into a list
     * @param iterable An iterable collection of elements
     * @param skip The number of leading elements to skip
     * @param limit The number of elements the stream should be limited to
     * @param order The Comparator to use for sorting/ordering the list
     * @param <T> Type of the elements in the iterable
     * @return The resulting paged list
     */
    public static <T> List<T> page(Iterable<T> iterable, long skip, long limit, Comparator<T> order) {
        return page(asStream(iterable, limit), skip, limit, order);
    }

    /**
     * Page a stream into a list
     * @param iterable An iterable collection of elements
     * @param skip The number of leading elements to skip
     * @param limit The number of elements the stream should be limited to
     * @param order Represents the fields to order by in order
     * @param <T> Type of the elements in the iterable
     * @return The resulting paged list
     */
    public static <T> List<T> page(Iterable<T> iterable, long skip, long limit, Collection<String> order) {
        return page(orderStream(iterable, order), skip, limit);
    }

    /**
     * Page a stream into a list
     * @param iterable An iterable collection of elements
     * @param skip The number of leading elements to skip
     * @param limit The number of elements the stream should be limited to
     * @param <T> Type of the elements in the iterable
     * @return The resulting paged list
     */
    public static <T> List<T> page(Iterable<T> iterable, long skip, long limit) {
        return page(asStream(iterable, limit), skip, limit);
    }

    /**
     * Page a stream into a list using default skip and limit values
     * @param iterable An iterable collection of elements
     * @param <T> Type of the elements in the iterable
     * @return The resulting paged list
     */
    public static <T> List<T> page(Iterable<T> iterable) {
        return page(iterable, DEFAULT_SKIP, DEFAULT_LIMIT);
    }

    /**
     * Page a stream into a list
     * @param iterable An iterable collection of elements
     * @param skip The number of leading elements to skip
     * @param <T> Type of the elements in the iterable
     * @return The resulting paged list
     */
    public static <T> List<T> page(Iterable<T> iterable, long skip) {
        return page(iterable, skip, DEFAULT_LIMIT);
    }

    /**
     * Order iterable as a Stream
     * @param iterable The iterable collection
     * @param order Represents the fields to order by in order
     * @param <T> Type of the elements in the iterable
     * @return The resulting ordered stream 
     */
    public static <T> Stream<T> orderStream(Iterable<T> iterable, Collection<String> order) {
        if (iterable == null)
            return Stream.empty();
        Iterator<T> iterator = iterable.iterator();
        if (order == null || order.isEmpty() || !iterator.hasNext())
            return asStream(iterable);

        return asStream(iterable).sorted(orderToComparator(iterator.next(), order));
    }

    /**
     * Order iterable as a List
     * @param iterable The iterable collection
     * @param order Represents the fields to order by
     * @param <T> Type of the elements in the iterable
     * @return The resulting ordered List 
     */
    public static <T> List<T> orderBy(Iterable<T> iterable, Collection<String> order) {
        return orderStream(iterable, order).collect(Collectors.toList());
    }

    /**
     * Convert the order strings to Comparators
     * @param o The object you are comparing
     * @param order Represents the fields to order by
     * @param <T> Type of the objects being compared
     * @return The resulting Comparator
     */
    private static <T> Comparator<T> orderToComparator(T o, Collection<String> order) {
        return order.stream()
                .map(it -> orderToComparator(o, it))
                .reduce(Comparator::thenComparing)
                .orElse(null);
    }

    /**
     * Convert the order string to Comparators
     * @param o The object you are comparing
     * @param order Represents the field to order by
     * @param <T> Type of the objects being compared
     * @return The resulting Comparator
     */
    @SuppressWarnings("unchecked")
    private static <T> Comparator<T> orderToComparator(T o, String order) {
        String[] sort = order.split(",");
        String field = sort[0];
        boolean descending = sort.length > 1 && sort[1].trim().equalsIgnoreCase("desc");
        return new Comparator<T>() {
            @Override
            public int compare(T o1, T o2) {
                try {
                    Comparable<Object> v1 =
                            (Comparable<Object>) Reflect.getFieldValue(o1, field);
                    Comparable<Object> v2 =
                            (Comparable<Object>) Reflect.getFieldValue(o2, field);
                    return descending ? v2.compareTo(v1) : v1.compareTo(v2);
                } catch (Exception ignored) {
                    return 0;
                }
            }
            @Override
            public boolean equals(Object obj) {
                return o.getClass() == obj.getClass() && o.equals(obj);
            }
        };
    }

    /**
     * Convert iterable to a stream
     * Will use a parallel stream if the size is above a large threshold
     * @param iterable The iterable to convert
     * @param size The size of the iterable
     * @param <T> The type of elements in the iterable
     * @return The resulting Stream
     */
    public static <T> Stream<T> asStream(Iterable<T> iterable, long size) {
        return StreamSupport.stream(iterable.spliterator(), size >= 9000);
    }

    /**
     * Convert iterable to a stream.
     * Will use a parallel stream if the size is above a large threshold
     * @param iterable The iterable to convert
     * @param <T> The type of elements in the iterable
     * @return The resulting Stream
     */
    public static <T> Stream<T> asStream(Iterable<T> iterable) {
        Spliterator<T> spliterator = iterable.spliterator();
        return StreamSupport.stream(spliterator, spliterator.estimateSize() >= 9000);
    }
}
