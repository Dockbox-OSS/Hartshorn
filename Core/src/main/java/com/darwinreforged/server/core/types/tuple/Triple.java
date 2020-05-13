package com.darwinreforged.server.core.types.tuple;

public class Triple<K, V, C> extends Tuple<K, V> {

    protected final C third;

    /**
     Creates a new {@link Tuple}.

     @param first
     The first object
     @param second
     The second object
     @param third
     The third object
     */
    public Triple(K first, V second, C third) {
        super(first, second);
        this.third = third;
    }

    public C getThird() {
        return third;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Triple)) return false;
        if (!super.equals(o)) return false;

        Triple<?, ?, ?> triple = (Triple<?, ?, ?>) o;

        if (getFirst() != null ? !getFirst().equals(triple.getFirst()) : triple.getFirst() != null) return false;
        if (getSecond() != null ? !getSecond().equals(triple.getSecond()) : triple.getSecond() != null) return false;
        return getThird() != null ? getThird().equals(triple.getThird()) : triple.getThird() == null;
    }

    @Override
    public int hashCode() {
        int result = getFirst() != null ? getFirst().hashCode() : 0;
        result = 31 * result + (getSecond() != null ? getSecond().hashCode() : 0);
        result = 31 * result + (getThird() != null ? getThird().hashCode() : 0);
        return result;
    }
}
