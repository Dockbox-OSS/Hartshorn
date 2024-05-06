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
 * A factory that can be used to create {@link MethodWrapper}s. This factory can be used to add individual
 * {@link ProxyCallback}s to a single {@link MethodWrapper}.
 *
 * @param <T> The type of the proxy instance
 *
 * @since 0.5.0
 *
 * @author Guus Lieben
 */
public interface MethodWrapperFactory<T> {

    /**
     * Adds a {@link ProxyCallback} that is invoked before the method invocation. If multiple callbacks are added, they
     * are invoked in the order in which they were added.
     *
     * @param callback The callback to invoke before the method invocation
     * @return This factory
     */
    MethodWrapperFactory<T> before(ProxyCallback<T> callback);

    /**
     * Adds a {@link ProxyCallback} that is invoked after a successful method invocation. If multiple callbacks are
     * added, they are invoked in the order in which they were added.
     *
     * @param callback The callback to invoke after the method invocation
     * @return This factory
     */
    MethodWrapperFactory<T> after(ProxyCallback<T> callback);

    /**
     * Adds a {@link ProxyCallback} that is invoked after a failed method invocation. If multiple callbacks are added,
     * they are invoked in the order in which they were added.
     *
     * @param callback The callback to invoke after the method invocation
     * @return This factory
     */
    MethodWrapperFactory<T> onError(ProxyCallback<T> callback);

}
