package org.dockbox.selene.core.server.properties;

import java.lang.reflect.Method;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

public final class DelegateProperty<T, R> implements InjectorProperty<Class<T>>{

    public static final String KEY = "SeleneInternalDelegateKey";
    private final Class<T> type;
    private final Method target;
    private final BiFunction<T, Object[], R> delegate;

    private DelegateProperty(Class<T> type, Method target, BiFunction<T, Object[], R> delegate) {
        this.type = type;
        this.target = target;
        this.delegate = delegate;
    }

    @Override
    public String getKey() {
        return KEY;
    }

    @Override
    public Class<T> getObject() {
        return this.type;
    }

    public BiFunction<T, Object[], R> getDelegate() {
        return this.delegate;
    }

    public Method getTarget() {
        return this.target;
    }

    public static <T, R> DelegateProperty<T, R> of(Class<T> type, Method target, BiFunction<T, Object[], R> delegate) {
        return new DelegateProperty<>(type, target, delegate);
    }

    public static <T, R> DelegateProperty<T, R> of(Class<T> type, Method target, BiConsumer<T, Object[]> delegate) {
        return new DelegateProperty<>(type, target, (instance, args) -> {
            delegate.accept(instance, args);
            //noinspection ReturnOfNull
            return null;
        });
    }
}
