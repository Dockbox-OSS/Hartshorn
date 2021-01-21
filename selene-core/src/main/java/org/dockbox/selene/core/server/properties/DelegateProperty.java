package org.dockbox.selene.core.server.properties;

import org.dockbox.selene.core.delegate.Phase;
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
    private Phase phase = Phase.OVERWRITE;
    private final DelegationHolder holder = new DelegationHolder();
    private boolean overwriteResult = true;
    private int priority = 10;

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

    public Class<?> getTargetClass() {
        return this.getTargetMethod().getDeclaringClass();
    }

    public Phase getTarget() {
        return this.phase;
    }

    public void setTarget(Phase phase) {
        this.phase = phase;
    }

    public R delegate(T instance, Object... args) {
        this.holder.setCancelled(false);
        return this.delegate.delegate(instance, args, this.holder);
    }

    public boolean isVoid() {
        return Void.TYPE.equals(this.getTargetMethod().getReturnType());
    }

    public Method getTargetMethod() {
        return this.target;
    }

    public boolean isCancelled() {
        return this.holder.isCancelled();
    }

    public boolean overwriteResult() {
        return this.overwriteResult && !this.isVoid();
    }

    public void setOverwriteResult(boolean overwriteResult) {
        this.overwriteResult = overwriteResult;
    }

    public int getPriority() {
        return this.priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
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
