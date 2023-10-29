package org.dockbox.hartshorn.component;

import java.util.Objects;

import org.dockbox.hartshorn.util.TypeUtils;

public class ScopeAdapterKey<T> extends ScopeKey<ScopeAdapter<T>> {

    private final Class<T> adapteeType;

    protected ScopeAdapterKey(Class<ScopeAdapter<T>> type, Class<T> adapteeType) {
        super(type);
        this.adapteeType = adapteeType;
    }

    public Class<T> adapteeType() {
        return adapteeType;
    }

    public static <T> ScopeAdapterKey<T> of(ScopeAdapter<T> adapter) {
        Class<ScopeAdapter<T>> adapterType = TypeUtils.adjustWildcards(adapter.getClass(), Class.class);
        Class<T> adapteeType = TypeUtils.adjustWildcards(adapter.adaptee().getClass(), Class.class);
        return new ScopeAdapterKey<>(adapterType, adapteeType);
    }

    @Override
    public boolean equals(Object object) {
        if(this == object) {
            return true;
        }
        if(!(object instanceof ScopeAdapterKey<?> that)) {
            return false;
        }
        if(!super.equals(object)) {
            return false;
        }
        return Objects.equals(adapteeType, that.adapteeType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), adapteeType);
    }
}
