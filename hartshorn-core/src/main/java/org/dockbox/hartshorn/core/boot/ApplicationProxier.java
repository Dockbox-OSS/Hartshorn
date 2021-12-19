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

package org.dockbox.hartshorn.core.boot;

import org.dockbox.hartshorn.core.context.ApplicationContext;
import org.dockbox.hartshorn.core.context.element.TypeContext;
import org.dockbox.hartshorn.core.domain.Exceptional;
import org.dockbox.hartshorn.core.proxy.ProxyHandler;
import org.dockbox.hartshorn.core.proxy.ProxyLookup;
import org.dockbox.hartshorn.core.services.ComponentPostProcessor;

/**
 * The {@link ApplicationProxier} is responsible for creating proxies of components. It is used by the
 * {@link ApplicationContext} to create proxies of components, as well as allowing {@link ComponentPostProcessor}s
 * to modify components.
 *
 * @author Guus Lieben
 * @since 4.2.4
 */
public interface ApplicationProxier extends ProxyLookup {

    /**
     * Creates a new proxy of the given type. If no proxy could be created, an empty {@link Exceptional} is
     * returned. Proxies created using this method are not able to delegate functionality to an underlying
     * instance.
     *
     * @param type The type of the proxy.
     * @param <T> The type of the proxy.
     * @return The proxy.
     */
    <T> Exceptional<T> proxy(TypeContext<T> type);

    /**
     * Creates a new proxy of the given type. If no proxy could be created, an empty {@link Exceptional} is
     * returned. Proxies created using this method are able to delegate functionality to the provided instance.
     * The original instance itself will not be modified.
     *
     * @param type The type of the proxy.
     * @param instance The instance to delegate to.
     * @param <T> The type of the proxy.
     * @return The proxy.
     */
    <T> Exceptional<T> proxy(TypeContext<T> type, T instance);

    /**
     * Gets the real type of the given proxy instance. If the given instance is not a proxy, the returned
     * type is the same as the given type. This method is used to determine the type of a proxy, without
     * having to unproxy it manually.
     *
     * @param instance The instance to get the type of.
     * @param <T> The type of the instance.
     * @return The type of the instance.
     * @see ProxyLookup#unproxy(Object)
     */
    <T> Exceptional<TypeContext<T>> real(T instance);

    /**
     * Gets the delegated instance of the given proxy. If the given instance is not a proxy, an empty
     * {@link Exceptional} is returned. This method does not return the original instance, but the instance
     * that is delegated to for a specific type.
     *
     * <p>For example, a {@link org.dockbox.hartshorn.core.proxy.DelegatorAccessor} can be implemented by
     * any proxy, and usages of e.g. {@link org.dockbox.hartshorn.core.proxy.DelegatorAccessor#delegator(Class)}
     * will be delegated to {@link org.dockbox.hartshorn.core.proxy.DelegatorAccessorImpl} or the highest
     * priority binding for {@link org.dockbox.hartshorn.core.proxy.DelegatorAccessor}.
     *
     * @param type The type of the delegator.
     * @param instance The instance to get the delegator of.
     * @param <T> The type of the delegator.
     * @param <P> The type of the instance.
     * @return The delegator.
     */
    <T, P extends T> Exceptional<T> delegator(final TypeContext<T> type, P instance);

    /**
     * Gets the delegated instance of the given proxy. This method does not return the original instance, but
     * the instance that is delegated to for a specific type. The handler should be the {@link ProxyHandler}
     * responsible for a given proxy instance.
     *
     * <p>For example, a {@link org.dockbox.hartshorn.core.proxy.DelegatorAccessor} can be implemented by
     * any proxy, and usages of e.g. {@link org.dockbox.hartshorn.core.proxy.DelegatorAccessor#delegator(Class)}
     * will be delegated to {@link org.dockbox.hartshorn.core.proxy.DelegatorAccessorImpl} or the highest
     * priority binding for {@link org.dockbox.hartshorn.core.proxy.DelegatorAccessor}.
     *
     * @param type The type of the delegator.
     * @param handler The handler of a proxy instance.
     * @param <T> The type of the delegator.
     * @param <P> The type of the instance.
     * @return The delegator.
     * @see #handler(TypeContext, Object)
     */
    <T, P extends T> Exceptional<T> delegator(final TypeContext<T> type, ProxyHandler<P> handler);

    /**
     * Gets or creates a {@link ProxyHandler} for the given type and instance. This does not create a new proxy,
     * but returns the {@link ProxyHandler} responsible for creating a proxy.
     *
     * @param type The type of the proxy.
     * @param instance The instance to delegate to.
     * @param <T> The type of the proxy.
     * @return The handler.
     * @see ProxyHandler
     */
    <T> ProxyHandler<T> handler(final TypeContext<T> type, final T instance);

    <T> Exceptional<ProxyHandler<T>> handler(T instance);
}
