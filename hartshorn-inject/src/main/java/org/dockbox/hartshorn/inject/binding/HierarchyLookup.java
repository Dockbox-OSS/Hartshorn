package org.dockbox.hartshorn.inject.binding;

import org.dockbox.hartshorn.inject.ComponentKey;
import org.dockbox.hartshorn.inject.scope.Scope;
import org.dockbox.hartshorn.util.collections.MultiMap;

public interface HierarchyLookup {

    /**
     * Returns the binding hierarchy for the given key.
     *
     * @param key the key to return the hierarchy for
     * @param <T> the type of the component
     *
     * @return the binding hierarchy
     */
    <T> BindingHierarchy<T> hierarchy(ComponentKey<T> key);

    /**
     * Returns all currently known binding hierarchies.
     *
     * @return all binding hierarchies
     */
    MultiMap<Scope, BindingHierarchy<?>> hierarchies();
}
