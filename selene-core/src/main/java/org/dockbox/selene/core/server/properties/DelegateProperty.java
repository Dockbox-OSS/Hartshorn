package org.dockbox.selene.core.server.properties;

import org.dockbox.selene.core.delegate.DelegateTarget;
import org.dockbox.selene.core.delegate.DelegationFunction;
import org.dockbox.selene.core.delegate.DelegationHolder;

import java.lang.reflect.Method;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

public final class DelegateProperty<T, R> implements InjectorProperty<Class<T>>{

    public static final String KEY = "SeleneInternalDelegateKey";
    private final Class<T> type;
    private final Method target;
    private final DelegationFunction<T, R> delegate;
    private DelegateTarget delegateTarget = DelegateTarget.OVERWRITE;
    private final DelegationHolder holder = new DelegationHolder();
    private boolean overwriteResult = true;

    private DelegateProperty(Class<T> type, Method target, DelegationFunction<T, R> delegate) {
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

    public DelegateTarget getTarget() {
        return this.delegateTarget;
    }

    public void setTarget(DelegateTarget delegateTarget) {
        this.delegateTarget = delegateTarget;
    }

    public R delegate(T instance, Object... args) {
        this.holder.setCancelled(false);
        return this.delegate.delegate(instance, args, this.holder);
    }

    public Method getTargetMethod() {
        return this.target;
    }

    public boolean isCancelled() {
        return this.holder.isCancelled();
    }

    public boolean overwriteResult() {
        return this.overwriteResult;
    }

    public void setOverwriteResult(boolean overwriteResult) {
        this.overwriteResult = overwriteResult;
    }

    public static <T, R> DelegateProperty<T, R> of(Class<T> type, Method target, BiFunction<T, Object[], R> delegate) {
        return new DelegateProperty<>(type, target, (instance, args, holder) -> delegate.apply(instance, args));
    }

    public static <T, R> DelegateProperty<T, R> of(Class<T> type, Method target, BiConsumer<T, Object[]> delegate) {
        return new DelegateProperty<>(type, target, (instance, args, holder) -> {
            delegate.accept(instance, args);
            //noinspection ReturnOfNull
            return null;
        });
    }

    public static <T, R> DelegateProperty<T, R> of(Class<T> type, Method target, DelegationFunction<T, R> delegate) {
        return new DelegateProperty<>(type, target, delegate);
    }
}
