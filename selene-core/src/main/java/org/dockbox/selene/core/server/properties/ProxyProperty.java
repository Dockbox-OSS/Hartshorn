package org.dockbox.selene.core.server.properties;

import org.dockbox.selene.core.proxy.Phase;
import org.dockbox.selene.core.proxy.ProxyFunction;
import org.dockbox.selene.core.proxy.ProxyHolder;

import java.lang.reflect.Method;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

public final class ProxyProperty<T, R> implements InjectorProperty<Class<T>>{

    public static final String KEY = "SeleneInternalProxyKey";
    private final Class<T> type;
    private final Method target;
    private final ProxyFunction<T, R> delegate;
    private Phase phase = Phase.OVERWRITE;
    private final ProxyHolder holder = new ProxyHolder();
    private boolean overwriteResult = true;
    private int priority = 10;

    private ProxyProperty(Class<T> type, Method target, ProxyFunction<T, R> delegate) {
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

    public static <T, R> ProxyProperty<T, R> of(Class<T> type, Method target, BiFunction<T, Object[], R> proxyFunction) {
        return new ProxyProperty<>(type, target, (instance, args, holder) -> proxyFunction.apply(instance, args));
    }

    public static <T, R> ProxyProperty<T, R> of(Class<T> type, Method target, BiConsumer<T, Object[]> proxyFunction) {
        return new ProxyProperty<>(type, target, (instance, args, holder) -> {
            proxyFunction.accept(instance, args);
            //noinspection ReturnOfNull
            return null;
        });
    }

    public static <T, R> ProxyProperty<T, R> of(Class<T> type, Method target, ProxyFunction<T, R> proxyFunction) {
        return new ProxyProperty<>(type, target, proxyFunction);
    }
}
