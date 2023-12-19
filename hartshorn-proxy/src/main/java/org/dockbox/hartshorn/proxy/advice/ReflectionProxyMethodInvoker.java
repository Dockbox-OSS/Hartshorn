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

package org.dockbox.hartshorn.proxy.advice;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.dockbox.hartshorn.proxy.ProxyManager;
import org.dockbox.hartshorn.proxy.advice.intercept.CustomInvocation;
import org.dockbox.hartshorn.proxy.advice.intercept.Invokable;
import org.dockbox.hartshorn.proxy.advice.intercept.MethodInterceptor;
import org.dockbox.hartshorn.proxy.advice.intercept.MethodInterceptorContext;
import org.dockbox.hartshorn.proxy.advice.intercept.MethodInvokable;
import org.dockbox.hartshorn.proxy.advice.intercept.ProxyMethodInterceptor;
import org.dockbox.hartshorn.proxy.advice.stub.MethodStub;
import org.dockbox.hartshorn.proxy.advice.stub.MethodStubContext;
import org.dockbox.hartshorn.util.TypeUtils;
import org.dockbox.hartshorn.util.introspect.Introspector;
import org.dockbox.hartshorn.util.introspect.MethodInvoker;
import org.dockbox.hartshorn.util.introspect.view.MethodView;

/**
 * Standard implementation of {@link ProxyMethodInvoker} that uses reflection to invoke methods on the target
 * instance. Certain optimizations may be applied to improve performance, such as caching of {@link MethodHandle}s.
 *
 * @param <T> the type of the target instance
 *
 * @since 0.5.0
 * @author Guus Lieben
 */
public class ReflectionProxyMethodInvoker<T> implements ProxyMethodInvoker<T> {

    private static final Map<Invokable, MethodHandle> METHOD_HANDLE_CACHE = new ConcurrentHashMap<>();

    private final Introspector introspector;
    private final ProxyManager<T> manager;
    private final ProxyMethodInterceptor<T> interceptor;

    public ReflectionProxyMethodInvoker(ProxyMethodInterceptor<T> interceptor) {
        this.introspector = interceptor.manager().orchestrator().introspector();
        this.manager = interceptor.manager();
        this.interceptor = interceptor;
    }

    @Override
    public <R> R invokeInterceptor(T self, MethodView<T, R> source, Object[] args, MethodInterceptor<T, R> interceptor, CustomInvocation<R> customInvocation) throws Throwable {
        MethodInterceptorContext<T, R> context = new MethodInterceptorContext<>(source, args, self, customInvocation);
        return interceptor.intercept(context);
    }

    @Override
    public Object invokeDelegate(T self, Invokable target, Object[] args) throws Throwable {
        return this.invokeAccessible(self, target, args, (method, instance, interceptorArgs) -> method.invoke(this.manager.delegate().get(), interceptorArgs));
    }

    @Override
    public Object invokeReal(T self, Invokable source, Invokable target, Object[] args) throws Throwable {
        if (target != null) {
            return this.invokeTarget(self, source, target, args);
        }
        else {
            return this.invokeStub(self, source, target, args);
        }
    }

    /**
     * Invokes the given {@code target} method on the given {@code self} instance, using the given {@code args}. This
     * ensures the method is accessible before invoking it.
     *
     * @param self the instance on which the method is invoked
     * @param target the method that is invoked
     * @param args the arguments that are passed to the method
     * @return the result of the method invocation
     */
    protected Object invokeSelf(T self, Invokable target, Object[] args) throws Throwable {
        return this.invokeAccessible(self, target, args, (method, instance, interceptorArgs) -> method.invoke(self, interceptorArgs));
    }

    /**
     * Invokes the given {@code target} method on the given {@code self} instance, using the given {@code args}. This
     * implementation is optimized for invoking {@code default} methods on interfaces.
     *
     * @param self the instance on which the method is invoked
     * @param source the method that is invoked
     * @param args the arguments that are passed to the method
     * @param declaringType the type on which the method is declared
     * @return the result of the method invocation
     * @throws Throwable if the method invocation fails
     */
    protected Object invokeDefault(T self, Invokable source, Object[] args, Class<T> declaringType) throws Throwable {
        MethodHandle handle;
        if (METHOD_HANDLE_CACHE.containsKey(source)) {
            handle = METHOD_HANDLE_CACHE.get(source);
        }
        else {
            handle = MethodHandles.lookup().findSpecial(
                    declaringType,
                    source.name(),
                    MethodType.methodType(source.returnType(), source.parameterTypes()),
                    declaringType
            ).bindTo(self);
            METHOD_HANDLE_CACHE.put(source, handle);
        }
        return handle.invokeWithArguments(args);
    }

    /**
     * Attempts to invoke the target method on the given {@code self} instance, using the given {@code args}. If the
     * delegate object is available, it is used to invoke the method. If the method is not available on the delegate,
     * the method is invoked either as a default method on an interface, or using reflection. If no advisors are
     * available, the default stub is invoked.
     *
     * @param self the instance on which the method is invoked
     * @param source the method that is invoked
     * @param target the method that is invoked
     * @param args the arguments that are passed to the method
     * @return the result of the method invocation
     * @throws Throwable if the method invocation fails
     */
    protected Object invokeTarget(T self, Invokable source, Invokable target, Object[] args) throws Throwable {
        Class<T> targetClass = this.manager.targetClass();

        try {
            // If the proxy associated with this handler has a delegate, use it.
            if (this.manager.delegate().present()) {
                return this.invokeDelegate(this.manager.delegate().get(), target, args);
            }

                // If the method is default inside an interface, we cannot invoke it directly using a proxy instance. Instead, we
                // need to look up the method on the class and invoke it through the method handle directly.
            else if (source.isDefault()) {
                return this.invokeDefault(self, source, args, targetClass);
            }

                // If the current target instance (self) is not a proxy, we can invoke the method directly using reflections.
            else if (!(self instanceof Proxy || Proxy.isProxyClass(self.getClass()))) {
                return this.invokeSelf(self, target, args);
            }

                // If none of the above conditions are met, we have no way to handle the method.
            else {
                return this.invokeStub(self, source, target, args);
            }
        }
        catch (InvocationTargetException e) {
            throw e.getCause();
        }
    }

    /**
     * Invokes the default stub for the current proxy. This method is invoked if no advisors are available for the
     * current proxy.
     *
     * @param self the instance on which the method is invoked
     * @param source the real method that is invoked
     * @param target the proxied method that is invoked
     * @param args the arguments that are passed to the method
     * @return the result of the method invocation
     * @throws Throwable if the method invocation fails
     */
    protected Object invokeStub(T self, Invokable source, Invokable target, Object[] args) throws Throwable {
        MethodStub<T> stub = this.manager.advisor().resolver().defaultStub().get();
        MethodStubContext<T> stubContext = new MethodStubContext<>(self, source, target, this.interceptor, args);
        return stub.invoke(stubContext);
    }

    /**
     * Ensures the given method is accessible before invoking it. If the method is a {@link MethodInvokable}, the
     * given {@link MethodInvoker} is used to invoke the method. Otherwise, the method is invoked in whichever way
     * the given {@link Invokable} supports. If the invocation of either option throws an exception, the default value
     * for the method's return type is returned.
     *
     * @param self the instance on which the method is invoked
     * @param target the method that is invoked
     * @param args the arguments that are passed to the method
     * @param function the function that is used to invoke the method
     * @return the result of the method invocation
     */
    protected Object invokeAccessible(T self, Invokable target, Object[] args, MethodInvoker<Object, T> function) throws Throwable {
        target.setAccessible(true);

        Object result;
        if (target instanceof MethodInvokable methodInvokable) {
            result = function.invoke(TypeUtils.adjustWildcards(methodInvokable.toIntrospector(), MethodView.class), self, args)
                    .orElseGet(() -> this.introspector.introspect(target.returnType()).defaultOrNull());
        }
        else {
            try {
                result = target.invoke(self, args);
            }
            catch (Throwable e) {
                result = this.introspector.introspect(target.returnType()).defaultOrNull();
            }
        }

        target.setAccessible(false);
        return result;
    }
}
