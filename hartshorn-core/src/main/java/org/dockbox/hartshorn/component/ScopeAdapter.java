package org.dockbox.hartshorn.component;

import java.util.Objects;

public class ScopeAdapter<T> implements Scope {

    private final T adaptee;

    private ScopeAdapter(final T adaptee) {
        this.adaptee = adaptee;
    }

    public T adaptee() {
        return this.adaptee;
    }

    public static <T> ScopeAdapter<T> of(final T adaptee) {
        return new ScopeAdapter<>(adaptee);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        final ScopeAdapter<?> that = (ScopeAdapter<?>) o;
        return this.adaptee.equals(that.adaptee);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.adaptee);
    }

    @Override
    public Class<? extends Scope> installableScopeType() {
        return Scope.class;
    }
}
