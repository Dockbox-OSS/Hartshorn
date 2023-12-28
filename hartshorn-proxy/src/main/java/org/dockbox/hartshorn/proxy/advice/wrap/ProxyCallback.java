/*
 * Copyright 2019-2023 the original author or authors.
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
 * A callback interface for a proxy method. This interface is used to provide a callback mechanism
 * for proxy methods, while remaining unaware of which phase of the proxy method invocation is
 * currently being executed. This is useful for component processors, and is typically used to create
 * a {@link MethodWrapper} for the proxy method.
 *
 * @param <T> the type of the proxy method
 * @author Guus Lieben
 * @since 0.4.10
 */
@FunctionalInterface
public interface ProxyCallback<T> {

    /**
     * Accepts a context for the proxy method invocation.
     *
     * @param context the context
     */
    void accept(ProxyCallbackContext<T> context);

    /**
     * Returns a new callback that will execute the current callback, and then the provided callback.
     *
     * @param next the callback to execute after the current callback
     * @return the new callback
     */
    default ProxyCallback<T> then(ProxyCallback<T> next) {
        return context -> {
            this.accept(context);
            next.accept(context);
        };
    }
}
