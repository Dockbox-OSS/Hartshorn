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

package org.dockbox.hartshorn.proxy.advice.wrap;

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
 * </ul>
 *
 * @param <T> The type of the proxy
 *
 * @since 0.4.10
 *
 * @author Guus Lieben
 */
public interface MethodWrapper<T> {

    /**
     * The action to perform before a method is visited.
     *
     * @param context The context of the method call
     */
    void acceptBefore(ProxyCallbackContext<T> context);

    /**
     * The action to perform after a visited method exits without errors.
     *
     * @param context The context of the method call
     */
    void acceptAfter(ProxyCallbackContext<T> context);

    /**
     * The action to perform after a visited method exits with errors.
     *
     * @param context The context of the method call
     */
    void acceptError(ProxyCallbackContext<T> context);

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
    static <T> MethodWrapper<T> of(ProxyCallback<T> before, ProxyCallback<T> after, ProxyCallback<T> afterThrowing) {
        return new CallbackMethodWrapper<>(before, after, afterThrowing);
    }
}
