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
 * Standard implementation of {@link MethodWrapperFactory}. This implementation allows for the creation of a
 * {@link MethodWrapper} by adding individual {@link ProxyCallback}s.
 *
 * @param <T> The type of the proxy instance
 *
 * @since 0.4.10
 *
 * @author Guus Lieben
 */
public class StandardMethodWrapperFactory<T> implements MethodWrapperFactory<T> {

    private ProxyCallback<T> before;
    private ProxyCallback<T> after;
    private ProxyCallback<T> onError;

    @Override
    public MethodWrapperFactory<T> before(ProxyCallback<T> callback) {
        this.before = this.before == null
                ? callback
                : this.before.then(callback);
        return this;
    }

    @Override
    public MethodWrapperFactory<T> after(ProxyCallback<T> callback) {
        this.after = this.after == null
                ? callback
                : this.after.then(callback);
        return this;
    }

    @Override
    public MethodWrapperFactory<T> onError(ProxyCallback<T> callback) {
        this.onError = this.onError == null
                ? callback
                : this.onError.then(callback);
        return this;
    }

    /**
     * Creates a new {@link MethodWrapper} instance based on the callbacks that were added to this factory.
     *
     * @return The created method wrapper
     */
    public MethodWrapper<T> create() {
        return new CallbackMethodWrapper<>(this.before, this.after, this.onError);
    }
}
