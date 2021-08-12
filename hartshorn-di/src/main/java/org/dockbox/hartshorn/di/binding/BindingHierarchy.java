package org.dockbox.hartshorn.di.binding;

import org.dockbox.hartshorn.api.domain.Exceptional;
import org.dockbox.hartshorn.di.Key;

import java.util.Map.Entry;
import java.util.Set;

public interface BindingHierarchy<C> extends Iterable<Entry<Integer, Provider<C>>> {

    Set<Provider<C>> providers();

    BindingHierarchy<C> add(Provider<C> provider);

    BindingHierarchy<C> add(int priority, Provider<C> provider);

    BindingHierarchy<C> merge(BindingHierarchy<C> hierarchy);

    int size();

    Exceptional<Provider<C>> get(int priority);

    Key<C> key();
}
