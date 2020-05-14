package com.darwinreforged.server.core.tuple;

public class QuadTuple<K, V, T, F> extends Triple<K, V, T> {

    protected final F fourth;

    /**
     Creates a new {@link Tuple}.@param first
     The first object

     @param second
     The second object
     @param third
     The third object
     */
    public QuadTuple(K first, V second, T third, F fourth) {
        super(first, second, third);
        this.fourth = fourth;
    }

    public F getFourth() {
        return fourth;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof QuadTuple)) return false;
        if (!super.equals(o)) return false;

        QuadTuple<?, ?, ?, ?> quadTuple = (QuadTuple<?, ?, ?, ?>) o;

        if (getFirst() != null ? !getFirst().equals(quadTuple.getFirst()) : quadTuple.getFirst() != null) return false;
        if (getSecond() != null ? !getSecond().equals(quadTuple.getSecond()) : quadTuple.getSecond() != null)
            return false;
        if (getThird() != null ? !getThird().equals(quadTuple.getThird()) : quadTuple.getThird() != null) return false;
        return getFourth() != null ? getFourth().equals(quadTuple.getFourth()) : quadTuple.getFourth() == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (getFirst() != null ? getFirst().hashCode() : 0);
        result = 31 * result + (getSecond() != null ? getSecond().hashCode() : 0);
        result = 31 * result + (getThird() != null ? getThird().hashCode() : 0);
        result = 31 * result + (getFourth() != null ? getFourth().hashCode() : 0);
        return result;
    }
}
