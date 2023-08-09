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

package org.dockbox.hartshorn.proxy.advice.stub;

import org.dockbox.hartshorn.context.DefaultContext;
import org.dockbox.hartshorn.proxy.ProxyManager;
import org.dockbox.hartshorn.proxy.advice.intercept.Invokable;
import org.dockbox.hartshorn.proxy.advice.intercept.ProxyMethodInterceptor;

/**
 * A context type that is used to pass information to a {@link org.dockbox.hartshorn.proxy.advice.stub.MethodStub} that
 * is being invoked. This context is used to provide information about the stub, the method that is being invoked, and
 * the proxy instance on which the method is invoked.
 *
 * @param <T> The type of the proxy instance
 *
 * @since 0.5.0
 * @author Guus Lieben
 */
public class MethodStubContext<T> extends DefaultContext {

    private final T self;
    private final Invokable source;
    private final Invokable target;
    private final ProxyMethodInterceptor<T> interceptor;
    private final Object[] args;

    public MethodStubContext(final T self,
                             final Invokable source,
                             final Invokable target,
                             final ProxyMethodInterceptor<T> interceptor,
                             final Object[] args) {
        this.self = self;
        this.source = source;
        this.target = target;
        this.interceptor = interceptor;
        this.args = args;
    }

    /**
     * Returns the proxy instance on which the method is invoked.
     *
     * @return The proxy instance on which the method is invoked.
     */
    public T self() {
        return this.self;
    }

    /**
     * The real method that is being invoked. This is the method that is <b>being</b> proxied, but not the method that
     * <b>is</b> proxied.
     *
     * @return The real method that is being invoked.
     */
    public Invokable source() {
        return this.source;
    }

    /**
     * The proxy method that is being invoked. This is the method that <b>is</b> proxied, but may also be equal to the
     * {@link #source()} if the method does not require rewriting (e.g. for concrete methods).
     *
     * @return The proxy method that is being invoked.
     */
    public Invokable target() {
        return this.target;
    }

    /**
     * The interceptor that is used to invoke the method.
     *
     * @return The interceptor that is used to invoke the method.
     */
    public ProxyMethodInterceptor<T> interceptor() {
        return this.interceptor;
    }

    /**
     * Returns the {@link ProxyManager} that is used to manage the proxy instance.
     *
     * @return The {@link ProxyManager} that is used to manage the proxy instance.
     */
    public ProxyManager<T> manager() {
        return this.interceptor.manager();
    }

    /**
     * Returns the arguments that are passed to the method. These arguments may be equal to those provided by the
     * original caller, or may have been modified by an advisor.
     *
     * @return The arguments that are passed to the method.
     */
    public Object[] args() {
        return this.args;
    }
}
