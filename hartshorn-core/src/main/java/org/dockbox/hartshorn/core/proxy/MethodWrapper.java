/*
 * Copyright 2019-2022 the original author or authors.
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

package org.dockbox.hartshorn.core.proxy;

import org.dockbox.hartshorn.core.context.element.MethodContext;

/**
 * A wrapper for a method. This is used to intercept method calls, without modifying the original method, though
 * it is possible to use this on intercepted or delegated methods as well.
 *
 * <p>Method wrappers are always called for the method(s) they wrap in the order they are added to the proxy.
 * Wrappers are able to listen to three events:
 * <ul>
 *     <li>When a method is called, this is performed <b>before</b> the method is visited</li>
 *     <li><b>After</b> a method finished. This is performed after a method exits without errors</li>
 *     <li>When a method <b>throws an exception</b></li>
 * <ul>
 *
 * @param <T> The type of the proxy
 * @author Guus Lieben
 * @since 22.2
 */
public interface MethodWrapper<T> {

    /**
     * The action to perform before a method is visited.
     *
     * @param method The method that is being visited
     * @param instance The instance that is being visited
     * @param args The arguments that are being passed to the method
     */
    void acceptBefore(MethodContext<?, T> method, T instance, Object[] args);

    /**
     * The action to perform after a visited method exits without errors.
     *
     * @param method The method that is being visited
     * @param instance The instance that is being visited
     * @param args The arguments that are being passed to the method
     */
    void acceptAfter(MethodContext<?, T> method, T instance, Object[] args);

    /**
     * The action to perform after a visited method exits with errors.
     *
     * @param method The method that is being visited
     * @param instance The instance that is being visited
     * @param args The arguments that are being passed to the method
     * @param error The error that was thrown
     */
    void acceptError(MethodContext<?, T> method, T instance, Object[] args, Throwable error);

    /**
     * A utility method to construct a simple method wrapper that calls different {@link ProxyCallback}s for each
     * of the three events. This is useful for simple component processors.
     *
     * <p>If a callback is null, it will not be called.
     *
     * @param before The callback to call before a method is visited
     * @param after The callback to call after a method is visited
     * @param afterThrowing The callback to call after a method throws an exception
     * @param <T> The type of the proxy
     * @return A method wrapper that calls the given callbacks
     */
    static <T> MethodWrapper<T> of(final ProxyCallback<T> before, final ProxyCallback<T> after, final ProxyCallback<T> afterThrowing) {
        return new MethodWrapper<>() {
            @Override
            public void acceptBefore(final MethodContext<?, T> method, final T instance, final Object[] args) {
                if (before != null) before.accept(method, instance, args);
            }

            @Override
            public void acceptAfter(final MethodContext<?, T> method, final T instance, final Object[] args) {
                if (after != null) after.accept(method, instance, args);
            }

            @Override
            public void acceptError(final MethodContext<?, T> method, final T instance, final Object[] args, final Throwable error) {
                if (afterThrowing != null) afterThrowing.accept(method, instance, args);
            }
        };
    }
}
