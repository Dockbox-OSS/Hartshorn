/*
 * Copyright 2019-2024 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.dockbox.hartshorn.proxy;

import org.dockbox.hartshorn.proxy.advice.intercept.Invokable;

/**
 * Utility interface to provide common methods for all proxies. This may be implemented by any proxy, advisor, or
 * handler to provide common methods for proxy objects.
 *
 * @param <T> the type of the proxy object
 *
 * @since 0.5.0
 * @author Guus Lieben
 */
@FunctionalInterface
public interface ProxyObject<T> {

    /**
     * Returns whether the given {@link Invokable} is the {@link Object#equals(Object)} method.
     *
     * @param invokable the invokable to check
     * @return whether the given {@link Invokable} is the {@link Object#equals(Object)} method
     */
    default boolean isEqualsMethod(Invokable invokable) {
        return "equals".equals(invokable.name())
                && invokable.returnType().equals(boolean.class)
                && invokable.parameterTypes().length == 1
                && invokable.parameterTypes()[0].equals(Object.class);
    }

    /**
     * Returns whether the given {@link Invokable} is the {@link Object#toString()} method.
     *
     * @param invokable the invokable to check
     * @return whether the given {@link Invokable} is the {@link Object#toString()} method
     */
    default boolean isToStringMethod(Invokable invokable) {
        return "toString".equals(invokable.name())
                && invokable.parameterTypes().length == 0
                && invokable.returnType().equals(String.class);
    }

    /**
     * Returns whether the given {@link Invokable} is the {@link Object#hashCode()} method.
     *
     * @param invokable the invokable to check
     * @return whether the given {@link Invokable} is the {@link Object#hashCode()} method
     */
    default boolean isHashCodeMethod(Invokable invokable) {
        return "hashCode".equals(invokable.name())
                && invokable.parameterTypes().length == 0
                && invokable.returnType().equals(int.class);
    }

    /**
     * Returns whether the given object is equal to the proxy managed by the {@link #manager()} of this
     * proxy object.
     *
     * @param obj the object to compare
     * @return whether the given object is equal to the proxy managed by the {@link #manager()}
     */
    default boolean proxyEquals(Object obj) {
        if (obj == null) {
            return false;
        }
        return this.manager().delegate().test(delegate -> {
            if (this.manager().orchestrator().isProxy(obj)) {
                return this.manager().orchestrator().manager(obj)
                        .flatMap(ProxyManager::delegate)
                        .test(delegate::equals);
            }
            return delegate.equals(obj) || this.manager().proxy() == obj;
        });
    }

    /**
     * Returns a logical string representation of the proxy managed by the {@link #manager()} of this
     * proxy object.
     *
     * @param self the proxy to represent
     * @return a logical string representation of the proxy managed by the {@link #manager()}
     */
    default String proxyToString(T self) {
        if (self == null) {
            return "null";
        }
        String canonicalName = this.manager().targetClass().getCanonicalName();
        return "Proxy: " + canonicalName + "@" + Integer.toHexString(this.proxyHashCode(self));
    }

    /**
     * Returns a hash code for the proxy managed by the {@link #manager()} of this proxy object.
     *
     * @param self the proxy to hash
     * @return a hash code for the proxy managed by the {@link #manager()}
     */
    default int proxyHashCode(T self) {
        return System.identityHashCode(self);
    }

    /**
     * Returns the {@link ProxyManager} that is responsible for the proxy which is represented by
     * this proxy object.
     *
     * @return the {@link ProxyManager}
     */
    ProxyManager<T> manager();
}
