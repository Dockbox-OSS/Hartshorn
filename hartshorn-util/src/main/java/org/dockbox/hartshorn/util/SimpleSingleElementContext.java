package org.dockbox.hartshorn.util;

public class SimpleSingleElementContext<I> extends AbstractSingleElementContext<I> {

    protected SimpleSingleElementContext(I input) {
        super(input);
    }

    public static <I> SimpleSingleElementContext<I> create(I input) {
        return new SimpleSingleElementContext<>(input);
    }

    @Override
    protected <T> SingleElementContext<T> clone(T input) {
        return new SimpleSingleElementContext<>(input);
    }
}
