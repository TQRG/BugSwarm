package org.semanticweb.owlapi.util;

import static java.util.stream.Collectors.toSet;
import static org.semanticweb.owlapi.util.OWLAPIPreconditions.checkNotNull;

import java.util.*;
import java.util.stream.Stream;

import org.semanticweb.owlapi.model.HasComponents;
import org.semanticweb.owlapi.model.OWLObject;

/** A few util methods for common stream operations. */
public class OWLAPIStreamUtils {

    private OWLAPIStreamUtils() {}

    /**
     * @param s
     *        stream to turn to set. The stream is consumed by this operation.
     * @return set including all elements in the stream
     */
    public static <T> Set<T> asSet(Stream<T> s) {
        Set<T> set = new LinkedHashSet<>();
        add(set, s);
        return set;
    }

    /**
     * @param s
     *        stream to turn to set. The stream is consumed by this operation.
     * @param type
     *        force return type to be exactly T
     * @param <T>
     *        type of return collection
     * @return set including all elements in the stream
     */
    @SuppressWarnings("unchecked")
    public static <T> Set<T> asSet(Stream<?> s, @SuppressWarnings("unused") Class<T> type) {
        Set<T> set = new LinkedHashSet<>();
        add(set, s.map(x -> (T) x));
        return set;
    }

    /**
     * @param s
     *        stream to turn to set. The stream is consumed by this operation.
     * @return set including all elements in the stream
     */
    public static <T> Set<T> asUnorderedSet(Stream<T> s) {
        return s.collect(toSet());
    }

    /**
     * @param s
     *        stream to turn to set. The stream is consumed by this operation.
     * @param type
     *        force return type to be exactly T
     * @param <T>
     *        type of return collection
     * @return set including all elements in the stream
     */
    @SuppressWarnings("unchecked")
    public static <T> Set<T> asUnorderedSet(Stream<?> s, @SuppressWarnings("unused") Class<T> type) {
        return s.map(x -> (T) x).collect(toSet());
    }

    /**
     * @param s
     *        stream to turn to list. The stream is consumed by this operation.
     * @return list including all elements in the stream
     */
    public static <T> List<T> asList(Stream<T> s) {
        List<T> set = new ArrayList<>();
        add(set, s);
        return set;
    }

    /**
     * @param s
     *        stream to turn to list. The stream is consumed by this operation.
     * @return list including all elements in the stream
     */
    public static <T> List<T> asListNullsForbidden(Stream<T> s) {
        return asList(s.map(x -> checkNotNull(x)));
    }

    /**
     * @param s
     *        stream to turn to list. The stream is consumed by this operation.
     * @param type
     *        force return type to be exactly T
     * @param <T>
     *        type of return collection
     * @return list including all elements in the stream
     */
    @SuppressWarnings("unchecked")
    public static <T> List<T> asList(Stream<?> s, @SuppressWarnings("unused") Class<T> type) {
        return asList(s.map(x -> (T) x));
    }

    /**
     * @param s
     *        stream to check for containment. The stream is consumed at least
     *        partially by this operation
     * @param o
     *        object to search
     * @return true if the stream contains the object
     */
    public static boolean contains(Stream<?> s, Object o) {
        return s.anyMatch(x -> x.equals(o));
    }

    /**
     * @param s
     *        stream of elements to add
     * @param c
     *        collection to add to
     * @return true if any element in the stream is added to the collection
     */
    public static <T> boolean add(Collection<? super T> c, Stream<T> s) {
        int size = c.size();
        s.forEach(c::add);
        return c.size() != size;
    }

    /**
     * @param set1
     *        collection to compare
     * @param set2
     *        collection to compare
     * @return negative value if set1 comes before set2, positive value if set2
     *         comes before set1, 0 if the two sets are equal or incomparable.
     */
    public static int compareCollections(Collection<? extends OWLObject> set1, Collection<? extends OWLObject> set2) {
        SortedSet<? extends OWLObject> ss1;
        if (set1 instanceof SortedSet) {
            ss1 = (SortedSet<? extends OWLObject>) set1;
        } else {
            ss1 = new TreeSet<>(set1);
        }
        SortedSet<? extends OWLObject> ss2;
        if (set2 instanceof SortedSet) {
            ss2 = (SortedSet<? extends OWLObject>) set2;
        } else {
            ss2 = new TreeSet<>(set2);
        }
        return compareIterators(ss1.iterator(), ss2.iterator());
    }

    /**
     * Compare streams through iterators (sensitive to order)
     * 
     * @param set1
     *        stream to compare
     * @param set2
     *        stream to compare
     * @return negative value if set1 comes before set2, positive value if set2
     *         comes before set1, 0 if the two sets are equal or incomparable.
     */
    public static int compareStreams(Stream<?> set1, Stream<?> set2) {
        return compareIterators(set1.sorted().iterator(), set2.sorted().iterator());
    }

    /**
     * Compare iterators element by element (sensitive to order)
     * 
     * @param set1
     *        iterator to compare
     * @param set2
     *        iterator to compare
     * @return negative value if set1 comes before set2, positive value if set2
     *         comes before set1, 0 if the two sets are equal or incomparable.
     */
    public static int compareIterators(Iterator<?> set1, Iterator<?> set2) {
        while (set1.hasNext() && set2.hasNext()) {
            Object o1 = set1.next();
            Object o2 = set2.next();
            int diff;
            if (o1 instanceof Stream && o2 instanceof Stream) {
                diff = compareIterators(((Stream<?>) o1).iterator(), ((Stream<?>) o2).iterator());
            } else if (o1 instanceof Collection && o2 instanceof Collection) {
                diff = compareIterators(((Collection<?>) o1).iterator(), ((Collection<?>) o2).iterator());
            } else if (o1 instanceof Comparable && o2 instanceof Comparable) {
                diff = ((Comparable) o1).compareTo(o2);
            } else {
                throw new IllegalArgumentException("Incomparable types: '" + o1 + "' with class " + o1.getClass()
                    + ", '" + o2 + "' with class " + o2.getClass() + " found while comparing iterators");
            }
            if (diff != 0) {
                return diff;
            }
        }
        return Boolean.compare(set1.hasNext(), set2.hasNext());
    }

    /**
     * Check iterator contents for equality (sensitive to order)
     * 
     * @param set1
     *        iterator to compare
     * @param set2
     *        iterator to compare
     * @return true if the iterators have the same content, false otherwise.
     */
    public static boolean equalIterators(Iterator<?> set1, Iterator<?> set2) {
        while (set1.hasNext() && set2.hasNext()) {
            Object o1 = set1.next();
            Object o2 = set2.next();
            if (o1 instanceof Stream && o2 instanceof Stream) {
                if (!equalStreams((Stream<?>) o1, (Stream<?>) o2)) {
                    return false;
                }
            } else {
                if (!o1.equals(o2)) {
                    return false;
                }
            }
        }
        return set1.hasNext() == set2.hasNext();
    }

    /**
     * Check streams for equality (sensitive to order)
     * 
     * @param set1
     *        stream to compare
     * @param set2
     *        stream to compare
     * @return true if the streams have the same content, false otherwise.
     */
    public static boolean equalStreams(Stream<?> set1, Stream<?> set2) {
        return equalIterators(set1.iterator(), set2.iterator());
    }

    /**
     * Check lists for equality (sensitive to order)
     * 
     * @param set1
     *        list to compare
     * @param set2
     *        list to compare
     * @return true if the lists have the same content, false otherwise.
     */
    public static int compareLists(List<? extends OWLObject> set1, List<? extends OWLObject> set2) {
        return compareIterators(set1.iterator(), set2.iterator());
    }

    /**
     * Annotated wrapper for Stream.empty()
     * 
     * @return empty stream
     */
    public static <T> Stream<T> empty() {
        return Stream.empty();
    }

    /**
     * @param root
     *        the root for the invisit
     * @return recursive invisit of all components included in the root
     *         component; includes the root and all intermediate nodes.
     *         Annotations and other groups of elements will be represented as
     *         streams or collections, same as if the accessor method on the
     *         object was used.
     */
    public static Stream<?> allComponents(HasComponents root) {
        List<Stream<?>> streams = new ArrayList<>();
        streams.add(Stream.of(root));
        root.components().forEach(o -> {
            if (o != root) {
                if (o instanceof HasComponents) {
                    streams.add(allComponents((HasComponents) o));
                } else {
                    streams.add(Stream.of(o));
                }
            }
        });
        return streams.stream().flatMap(x -> x);
    }

    /**
     * @param root
     *        the root for the invisit
     * @return recursive invisit of all components included in the root
     *         component; includes the root and all intermediate nodes. Streams
     *         will be flattened.
     */
    public static Stream<?> flatComponents(HasComponents root) {
        List<Stream<?>> streams = new ArrayList<>();
        streams.add(Stream.of(root));
        root.components().filter(o -> o != root).forEach(o -> flatIteration(streams, o));
        return streams.stream().flatMap(x -> x);
    }

    protected static void flatIteration(List<Stream<?>> streams, Object o) {
        if (o instanceof Stream) {
            ((Stream<?>) o).forEach(o1 -> flatIteration(streams, o1));
        } else if (o instanceof Collection) {
            ((Collection<?>) o).forEach(o1 -> flatIteration(streams, o1));
        } else if (o instanceof HasComponents) {
            ((HasComponents) o).components().forEach(o1 -> flatIteration(streams, o1));
        } else {
            streams.add(Stream.of(o));
        }
    }
}
