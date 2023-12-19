package org.dockbox.hartshorn.util.introspect.reflect;

import org.dockbox.hartshorn.util.option.Option;

@FunctionalInterface
public interface ReflectiveConstructorCall<T> {

    Option<T> invoke(Object[] args) throws Throwable;
}
