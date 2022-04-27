package org.dockbox.hartshorn.inject.binding;

import org.dockbox.hartshorn.inject.Key;
import org.dockbox.hartshorn.util.Exceptional;

@FunctionalInterface
public interface ComponentInstanceFactory {
    <T>Exceptional<T> instantiate(Key<T> key);
}
