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

package org.dockbox.hartshorn.proxy.advice.intercept;

import org.dockbox.hartshorn.context.DefaultContext;
import org.dockbox.hartshorn.util.introspect.view.MethodView;

import java.util.concurrent.Callable;

/**
 * The context of a {@link MethodInterceptor}. It contains the method to be intercepted, the arguments to be passed to the method,
 * the return type of the method, the {@link MethodView view} of the method, the instance of the object to be intercepted, and
 * utility callables to call the underlying method.
 *
 * @param <T> the type of the proxy object
 * @param <R> the return type of the method
 *
 * @since 0.4.10
 *
 * @author Guus Lieben
 */
public final class MethodInterceptorContext<T, R> extends DefaultContext {

    private final MethodView<T, R> method;
    private final Object[] args;
    private final T instance;
    private final Callable<R> callable;
    private final CustomInvocation<R> customInvocation;
    private final R result;

    private MethodInterceptorContext(MethodView<T, R> method, Object[] args, T instance, Callable<R> callable, CustomInvocation<R> customInvocation, R result) {
        this.method = method;
        this.args = args;
        this.instance = instance;
        this.callable = callable;
        this.customInvocation = customInvocation;
        this.result = result;
    }

    /**
     * Creates a new {@link MethodInterceptorContext} with the given method, arguments, instance, and custom invocation. The custom
     * invocation is used to call the underlying method with the given arguments. As there is no result at this point, the default
     * value of the method's return type is used, or {@code null} if no such default exists.
     *
     * @param method The method to be intercepted
     * @param args The arguments passed to the method
     * @param instance The instance on which the method is called
     * @param customInvocation The custom invocation to call the underlying method
     * @param <T> the type of the proxy object
     * @param <R> the return type of the method
     *
     * @return a new {@link MethodInterceptorContext} with the given method, arguments, instance, and custom invocation
     */
    public static <T, R> MethodInterceptorContext<T, R> of(MethodView<T, R> method, Object[] args, T instance, CustomInvocation<R> customInvocation) {
        return new MethodInterceptorContext<>(
                method,
                args,
                instance,
                customInvocation.toCallable(args),
                customInvocation,
                method.returnType().defaultOrNull()
        );
    }

    /**
     * Creates a new {@link MethodInterceptorContext} from the given existing {@link MethodInterceptorContext}, but with
     * the addition of a result value.
     *
     * @param context the existing context to copy
     * @param result the result to add to the context
     * @param <T> the type of the proxy object
     * @param <R> the return type of the method
     *
     * @return a new {@link MethodInterceptorContext} with the given result
     */
    public static <T, R> MethodInterceptorContext<T, R> copyWithResult(MethodInterceptorContext<T, R> context, R result) {
        var copiedContext = new MethodInterceptorContext<>(
                context.method,
                context.args,
                context.instance,
                context.callable,
                context.customInvocation,
                result
        );
        context.copyToContext(copiedContext);
        return copiedContext;
    }

    /**
     * Returns the intercepted method, as it was defined on the original class.
     * @return the intercepted method
     */
    public MethodView<T, R> method() {
        return this.method;
    }

    /**
     * Returns the arguments which were originally passed to the intercepted method.
     * @return the arguments which were originally passed to the intercepted method
     */
    public Object[] args() {
        return this.args;
    }

    /**
     * Returns the instance of the intercepted object. If an instance delegate exists for the active proxy, this delegate will be
     * returned. Otherwise, the proxy instance itself will be returned.
     * @return the instance of the intercepted object
     */
    public T instance() {
        return this.instance;
    }

    /**
     * Invokes the underlying method with the original arguments. This allows the intercepted method to be invoked without
     * any additional logic.
     *
     * @return the result of the underlying method
     * @throws Throwable if the underlying method throws an exception
     */
    public R invokeDefault() throws Throwable {
        if (this.callable != null) {
            return this.callable.call();
        }
        return this.result();
    }

    /**
     * Invokes the underlying method with the given arguments. This allows the intercepted method to be invoked without any
     * additional logic.
     *
     * @param args the arguments to pass to the underlying method
     * @return the result of the underlying method
     * @throws Throwable if the underlying method throws an exception
     */
    public R invokeDefault(Object... args) throws Throwable {
        if (this.customInvocation != null) {
            return this.customInvocation.call(args);
        }
        return this.result();
    }

    /**
     * The result of the previous interceptor, if any. If this is the first interceptor, the result will be the default value
     * for the return type of the intercepted method.
     *
     * @return the result of the previous interceptor, if any
     * @see org.dockbox.hartshorn.util.introspect.view.TypeView#defaultOrNull()
     */
    public R result() {
        return this.result;
    }
}
