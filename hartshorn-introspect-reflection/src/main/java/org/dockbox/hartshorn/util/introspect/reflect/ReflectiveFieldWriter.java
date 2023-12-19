package org.dockbox.hartshorn.util.introspect.reflect;

@FunctionalInterface
public interface ReflectiveFieldWriter<T, P> {

    void set(P instance, T value) throws Throwable;
}
