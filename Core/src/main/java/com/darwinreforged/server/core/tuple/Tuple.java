package com.darwinreforged.server.core.tuple;

/**
 A tuple of objects.

 @param <K>
 The key
 @param <V>
 The value
 */
public class Tuple<K, V> {

    /**
     Creates a new {@link Tuple} with the desired {@code first} and
     {@code second} objects.

     @param <K>
     The type of first object
     @param <V>
     The type of second object
     @param first
     The first object
     @param second
     The second object

     @return The new Tuple
     */
    public static <K, V> Tuple<K, V> of(K first, V second) {
        return new Tuple<>(first, second);
    }

    /**
     The First.
     */
    protected final K first;
    /**
     The Second.
     */
    protected final V second;

    /**
     Creates a new {@link Tuple}.

     @param first
     The first object
     @param second
     The second object
     */
    public Tuple(K first, V second) {
        this.first = first;
        this.second = second;
    }

    /**
     Gets the first object, otherwise known as "key".

     @return The first object
     */
    public K getFirst() {
        return this.first;
    }

    /**
     Gets the second object, otherwise known as "value".

     @return The value
     */
    public V getSecond() {
        return this.second;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final Tuple other = (Tuple) obj;
        return this.first.equals(other.first)
                && this.second.equals(other.second);
    }
}
