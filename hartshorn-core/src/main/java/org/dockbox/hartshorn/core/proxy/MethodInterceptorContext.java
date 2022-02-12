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

import java.lang.reflect.Method;
import java.util.concurrent.Callable;

public class MethodInterceptorContext<T> {

    private final MethodContext<?, T> method;
    private final Object[] args;
    private final T instance;
    private final Callable<Object> callable;
    private final CustomInvocation customInvocation;
    private final Object result;

    public MethodInterceptorContext(final Method method, final Object[] args, final T instance, final Callable<Object> callable, final CustomInvocation customInvocation, final Object result) {
        this.method = (MethodContext<?, T>) MethodContext.of(method);
        this.args = args;
        this.instance = instance;
        this.callable = callable;
        this.customInvocation = customInvocation;
        this.result = result;
    }

    public MethodInterceptorContext(final MethodInterceptorContext<T> context, final Object result) {
        this(context.method.method(), context.args, context.instance, context.callable, context.customInvocation, result);
    }

    public MethodInterceptorContext(final Method method, final Object[] args, final T instance, final Callable<Object> callable, final CustomInvocation customInvocation) {
        this(method, args, instance, callable, customInvocation, MethodContext.of(method).returnType().defaultOrNull());
    }

    public MethodContext<?, T> method() {
        return this.method;
    }

    public Object[] args() {
        return this.args;
    }

    public T instance() {
        return this.instance;
    }

    public Object invokeDefault() throws Throwable {
        if (this.callable != null) {
            return this.callable.call();
        }
        return this.result();
    }

    public Object invokeDefault(final Object... args) throws Throwable {
        if (this.customInvocation != null) {
            return this.customInvocation.call(args);
        }
        return this.result();
    }

    public Object result() {
        return this.result;
    }
}
