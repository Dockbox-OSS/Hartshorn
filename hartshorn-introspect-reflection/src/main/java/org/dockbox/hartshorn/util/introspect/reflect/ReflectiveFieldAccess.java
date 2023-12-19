package org.dockbox.hartshorn.util.introspect.reflect;

import org.dockbox.hartshorn.util.option.Option;

@FunctionalInterface
public interface ReflectiveFieldAccess<T, P> {
    Option<T> get(P instance) throws Throwable;
}
