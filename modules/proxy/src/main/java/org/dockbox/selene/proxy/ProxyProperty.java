/*
 * Copyright (C) 2020 Guus Lieben
 *
 * This framework is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 2.1 of the
 * License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 * the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library. If not, see {@literal<http://www.gnu.org/licenses/>}.
 */

package org.dockbox.selene.proxy;

import org.dockbox.selene.api.server.properties.InjectorProperty;
import org.dockbox.selene.proxy.handle.Phase;
import org.dockbox.selene.proxy.handle.ProxyFunction;
import org.dockbox.selene.proxy.handle.ProxyHolder;

import java.lang.reflect.Method;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

public final class ProxyProperty<T, R> implements InjectorProperty<Class<T>>
{

    public static final String KEY = "SeleneInternalProxyKey";
    private final Class<T> type;
    private final Method target;
    private final ProxyFunction<T, R> delegate;
    private final ProxyHolder holder = new ProxyHolder();
    private Phase phase = Phase.OVERWRITE;
    private boolean overwriteResult = true;
    private int priority = 10;

    private ProxyProperty(Class<T> type, Method target, ProxyFunction<T, R> delegate)
    {
        this.type = type;
        this.target = target;
        this.delegate = delegate;
    }

    public static <T, R> ProxyProperty<T, R> of(Class<T> type, Method target, BiFunction<T, Object[], R> proxyFunction)
    {
        return new ProxyProperty<>(type, target, (instance, args, holder) -> proxyFunction.apply(instance, args));
    }

    public static <T, R> ProxyProperty<T, R> of(Class<T> type, Method target, BiConsumer<T, Object[]> proxyFunction)
    {
        return new ProxyProperty<>(type, target, (instance, args, holder) -> {
            proxyFunction.accept(instance, args);
            //noinspection ReturnOfNull
            return null;
        });
    }

    public static <T, R> ProxyProperty<T, R> of(Class<T> type, Method target, ProxyFunction<T, R> proxyFunction)
    {
        return new ProxyProperty<>(type, target, proxyFunction);
    }

    @Override
    public String getKey()
    {
        return KEY;
    }

    @Override
    public Class<T> getObject()
    {
        return this.type;
    }

    public Class<?> getTargetClass()
    {
        return this.getTargetMethod().getDeclaringClass();
    }

    public Method getTargetMethod()
    {
        return this.target;
    }

    public Phase getTarget()
    {
        return this.phase;
    }

    public void setTarget(Phase phase)
    {
        this.phase = phase;
    }

    public R delegate(T instance, Object... args)
    {
        this.holder.setCancelled(false);
        return this.delegate.delegate(instance, args, this.holder);
    }

    public boolean isCancelled()
    {
        return this.holder.isCancelled();
    }

    public boolean overwriteResult()
    {
        return this.overwriteResult && !this.isVoid();
    }

    public boolean isVoid()
    {
        return Void.TYPE.equals(this.getTargetMethod().getReturnType());
    }

    public void setOverwriteResult(boolean overwriteResult)
    {
        this.overwriteResult = overwriteResult;
    }

    public int getPriority()
    {
        return this.priority;
    }

    public void setPriority(int priority)
    {
        this.priority = priority;
    }
}
