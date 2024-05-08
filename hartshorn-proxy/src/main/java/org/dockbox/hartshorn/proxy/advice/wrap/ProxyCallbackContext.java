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

import org.checkerframework.checker.nullness.qual.Nullable;
import org.dockbox.hartshorn.context.DefaultContext;
import org.dockbox.hartshorn.util.introspect.view.MethodView;

/**
 * A context type that is used to pass information to a {@link org.dockbox.hartshorn.proxy.advice.wrap.MethodWrapper} or
 * {@link ProxyCallback} that is being invoked. This context is used to provide information about the method that is
 * being invoked, and the proxy instance on which the method is invoked.
 *
 * @param <T> The type of the proxy instance
 *
 * @since 0.4.12
 *
 * @author Guus Lieben
 */
public class ProxyCallbackContext<T> extends DefaultContext {

    private final T delegate;
    private final T proxy;
    private final MethodView<T, ?> method;
    private final Object[] args;
    private Throwable error;

    public ProxyCallbackContext(T delegate, T proxy, MethodView<T, ?> method, Object[] args) {
        this(delegate, proxy, method, args, null);
    }

    public ProxyCallbackContext(T delegate, T proxy, MethodView<T, ?> method, Object[] args, Throwable error) {
        this.delegate = delegate;
        this.proxy = proxy;
        this.method = method;
        this.args = args;
        this.error = error;
    }

    /**
     * The immediate delegate of the proxy. This is the object that is proxied by the proxy instance. If no delegate is
     * available, this method returns {@code null}.
     *
     * @return The immediate delegate of the proxy, or {@code null} if no delegate is available.
     */
    @Nullable
    public T delegate() {
        return this.delegate;
    }

    /**
     * Returns the proxy instance on which the method is invoked.
     *
     * @return The proxy instance on which the method is invoked.
     */
    public T proxy() {
        return this.proxy;
    }

    /**
     * Returns the real method that is being invoked. This method is the method that is invoked on the delegate, and is
     * not the method that is invoked on the proxy instance.
     *
     * @return The real method that is being invoked.
     */
    public MethodView<T, ?> method() {
        return this.method;
    }

    /**
     * Returns the arguments that are passed to the method invocation. These arguments are the arguments that are passed
     * to the method invocation on the proxy instance.
     *
     * @return The arguments that are passed to the method invocation.
     */
    public Object[] args() {
        return this.args;
    }

    /**
     * Returns the error that was thrown during the method invocation. If no error was thrown, this method returns
     * {@code null}.
     *
     * @return The error that was thrown during the method invocation, or {@code null} if no error was thrown.
     */
    @Nullable
    public Throwable error() {
        return this.error;
    }

    /**
     * Sets the error that was thrown during the method invocation. This method is typically used by a
     * {@link org.dockbox.hartshorn.proxy.advice.ProxyAdvisor} to indicate that an error was thrown during the
     * invocation of the method on the proxy instance.
     *
     * @param error The error that was thrown during the method invocation.
     * @return This context instance.
     */
    public ProxyCallbackContext<T> acceptError(Throwable error) {
        this.error = error;
        return this;
    }
}
