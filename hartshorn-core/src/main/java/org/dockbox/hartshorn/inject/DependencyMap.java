package org.dockbox.hartshorn.inject;

import java.util.Collection;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Map;

import org.dockbox.hartshorn.component.ComponentKey;
import org.dockbox.hartshorn.util.collections.StandardMultiMap;

public class DependencyMap extends StandardMultiMap<DependencyResolutionType, ComponentKey<?>> {

    public DependencyMap() {
    }

    @Override
    protected Map<DependencyResolutionType, Collection<ComponentKey<?>>> createEmptyMap() {
        return new EnumMap<>(DependencyResolutionType.class);
    }

    @Override
    protected Collection<ComponentKey<?>> createEmptyCollection() {
        return new HashSet<>();
    }

    public static DependencyMap create() {
        return new DependencyMap();
    }

    public DependencyMap immediate(final ComponentKey<?> key) {
        this.put(DependencyResolutionType.IMMEDIATE, key);
        return this;
    }

    public DependencyMap immediate(final Collection<ComponentKey<?>> keys) {
        this.putAll(DependencyResolutionType.IMMEDIATE, keys);
        return this;
    }

    public DependencyMap delayed(final ComponentKey<?> key) {
        this.put(DependencyResolutionType.DELAYED, key);
        return this;
    }

    public DependencyMap delayed(final Collection<ComponentKey<?>> keys) {
        this.putAll(DependencyResolutionType.DELAYED, keys);
        return this;
    }
}
