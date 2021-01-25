package org.dockbox.selene.core.server.inject;

import org.dockbox.selene.core.util.Reflect;

import java.util.function.Function;

public final class InjectionPoint<T> {

    private final Class<T> type;
    private final Function<T, T> point;

    private InjectionPoint(Class<T> type, Function<T, T> point) {
        this.type = type;
        this.point = point;
    }

    public boolean accepts(Class<?> type) {
        return Reflect.isAssignableFrom(this.type, type);
    }

    public T apply(T instance) {
        return this.point.apply(instance);
    }

    public static <T> InjectionPoint<T> of(Class<T> type, Function<T, T> point) {
        return new InjectionPoint<>(type, point);
    }

}
