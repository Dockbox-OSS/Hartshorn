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

package org.dockbox.hartshorn.proxy;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.application.context.ParameterLoaderContext;
import org.dockbox.hartshorn.proxy.loaders.UnproxyingParameterLoader;
import org.dockbox.hartshorn.util.Result;
import org.dockbox.hartshorn.util.parameter.ParameterLoader;
import org.dockbox.hartshorn.util.reflect.MethodContext;
import org.dockbox.hartshorn.util.reflect.MethodInvoker;
import org.dockbox.hartshorn.util.reflect.TypeContext;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javassist.util.proxy.ProxyFactory;

public class StandardMethodInterceptor<T> {

    private static final Map<Invokable, MethodHandle> METHOD_HANDLE_CACHE = new ConcurrentHashMap<>();

    private final ProxyManager<T> manager;
    private final ApplicationContext applicationContext;
    private final ParameterLoader<ParameterLoaderContext> parameterLoader = new UnproxyingParameterLoader();

    public StandardMethodInterceptor(final ProxyManager<T> manager, final ApplicationContext applicationContext) {
        this.manager = manager;
        this.applicationContext = applicationContext;
    }

    public ProxyManager<T> manager() {
        return this.manager;
    }

    public Object intercept(final Object self, final MethodInvokable source, final Invokable proxy, final Object[] args) throws Throwable {
        final T callbackTarget = this.manager.delegate().or((T) self);
        final MethodContext<?, T> methodContext = (MethodContext<?, T>) source.toMethodContext();

        final CustomInvocation customInvocation = this.createDefaultInvocation(source, proxy, callbackTarget);
        final Object[] arguments = this.resolveArgs(source, self, args);

        final Set<MethodWrapper<T>> wrappers = this.manager.wrappers(source.toMethod());

        return this.interceptAndNotify(self, source, proxy, callbackTarget, methodContext, customInvocation, arguments, wrappers);
    }

    private Object interceptAndNotify(final Object self, final MethodInvokable source, final Invokable proxy, final T callbackTarget,
                                      final MethodContext<?, T> methodContext, final CustomInvocation customInvocation,
                                      final Object[] arguments, final Set<MethodWrapper<T>> wrappers
    ) throws Throwable {
        for (final MethodWrapper<T> wrapper : wrappers) {
            wrapper.acceptBefore(methodContext, callbackTarget, arguments);
        }

        try {
            final Object result;
            final Result<MethodInterceptor<T>> interceptor = this.manager.interceptor(source.toMethod());
            if (interceptor.present()) {
                result = this.invokeInterceptor(interceptor.get(), callbackTarget, source.toMethod(), customInvocation, arguments);
            }
            else {
                final Result<T> delegate = this.manager.delegate(source.toMethod());
                if (delegate.present())
                    result = this.invokeDelegate(delegate.get(), callbackTarget, source.toMethod(), arguments);
                else
                    result = this.interceptWithoutDelegate(self, callbackTarget, source, proxy, arguments);
            }

            for (final MethodWrapper<T> wrapper : wrappers) {
                wrapper.acceptAfter(methodContext, callbackTarget, arguments);
            }
            return result;
        }
        catch (final Throwable e) {
            for (final MethodWrapper<T> wrapper : wrappers) {
                wrapper.acceptError(methodContext, callbackTarget, arguments, e);
            }
            throw e;
        }
    }

    protected Object interceptWithoutDelegate(final Object self, final T callbackTarget, final Invokable source, final Invokable proxy, final Object[] args) throws Throwable {
        // If no handler is known, default to the original method. This is delegated to the instance
        // created, as it is typically created through Hartshorn's injectors and therefore DI dependent.
        Invokable target = source;
        if (this.manager.delegate().absent() && source == null)
            target = proxy;

        if (this.isEqualsMethod(target)) {
            return this.proxyEquals(args[0]);
        }

        final Object result;
        if (callbackTarget == self && proxy != null) {
            result = proxy.invoke(callbackTarget, args);
        }
        else {
            result = this.invokeUnregistered(self, source, target, args);
        }
        return result;
    }

    protected CustomInvocation createDefaultInvocation(final Invokable source, final Invokable proxy, final T callbackTarget) {
        return args$0 -> {
            if (this.manager.delegate().present()) {
                return this.invokeDelegate(this.manager.delegate().get(), source, args$0);
            }
            if (proxy == null) return TypeContext.of(proxy.getReturnType()).defaultOrNull();
            return proxy.invoke(callbackTarget, args$0);
        };
    }

    protected Object invokeInterceptor(final MethodInterceptor<T> interceptor, final Object self, final Method source, final CustomInvocation customInvocation, final Object[] args) throws Throwable {
        final MethodInterceptorContext<T> context = new MethodInterceptorContext(source, args, self, customInvocation);
        return interceptor.intercept(context);
    }

    protected Object invokeDelegate(final T delegate, final Object self, final Method source, final Object[] args) throws Throwable {
        final Object result = source.invoke(delegate, args);
        if (result == delegate) return self;
        return result;
    }

    protected Object invokeUnregistered(final Object self, final Invokable source, final Invokable target, final Object[] args) throws Throwable {
        final Class<T> targetClass = this.manager.targetClass();

        if (target != null) {
            return this.invokeTarget(self, source, target, args);
        }
        else {
            final StackTraceElement element = Thread.currentThread().getStackTrace()[3];
            final String name = element.getMethodName();
            final String className = targetClass == null ? "" : targetClass.getSimpleName() + ".";
            throw new AbstractMethodError("Cannot invoke method '" + className + name + "' because it is abstract. This type is proxied, but no proxy property was found for the method.");
        }
    }

    protected boolean isEqualsMethod(final Invokable invokable) {
        return invokable.getName().equals("equals")
                && invokable.getDeclaringClass().equals(Object.class)
                && this.manager.delegate().absent();
    }

    protected boolean proxyEquals(final Object obj) {
        if (obj == null) return false;
        if (this.manager.delegate().map(instance -> instance.equals(obj)).or(false)) return true;
        return this.manager.proxy() == obj;
    }

    protected Object invokeTarget(final Object self, final Invokable source, final Invokable target, final Object[] args) throws Throwable {
        final Class<T> targetClass = this.manager.targetClass();
        final TypeContext<T> targetType = TypeContext.of(targetClass);

        try {
            // If the proxy associated with this handler has a delegate, use it.
            if (this.manager.delegate().present()) return this.invokeDelegate(this.manager.delegate().get(), target, args);

            // If the method is default inside an interface, we cannot invoke it directly using a proxy instance. Instead, we
            // need to look up the method on the class and invoke it through the method handle directly.
            else if (source.isDefault()) return this.invokeDefault(targetClass, source, self, args);

            // If the current target instance (self) is not a proxy, we can invoke the method directly using reflections.
            else if (!(self instanceof Proxy || ProxyFactory.isProxyClass(self.getClass()))) return this.invokeSelf(self, target, args);

            // If none of the above conditions are met, we have no way to handle the method.
            else throw this.invokeFailed(self, source, target, args, targetType);
        }
        catch (final InvocationTargetException e) {
            throw e.getCause();
        }
    }

    protected IllegalStateException invokeFailed(final Object self, final Invokable source, final Invokable target, final Object[] args, final TypeContext<T> targetType) {
        final TypeContext<Object> selfContext = TypeContext.of(self);
        return new IllegalStateException("Could not invoke local method " + source.getQualifiedName()
                + " (targeting " + target.getQualifiedName() + ") on proxy "
                + targetType.qualifiedName() + " of qualified type " + selfContext.qualifiedName() + "(isProxy=" + this.applicationContext().environment().manager().isProxy(self) + ")"
                + " with arguments " + Arrays.toString(args) + ". " +
                "This typically indicates that there is no appropriate proxy property (delegate or interceptor for the method.");
    }

    protected Object[] resolveArgs(final MethodInvokable method, final Object instance, final Object[] args) {
        final MethodContext<?, ?> methodContext = method.toMethodContext();
        final ParameterLoaderContext context = new ParameterLoaderContext(methodContext, methodContext.parent(), instance, this.applicationContext());
        return this.parameterLoader.loadArguments(context, args).toArray();
    }

    protected Object invokeDelegate(final Object self, final Invokable target, final Object[] args) {
        return this.invokeAccessible(self, target, args, (method, instance, args$0) -> method.invoke(this.manager.delegate().get(), args$0));
    }

    protected Object invokeSelf(final Object self, final Invokable target, final Object[] args) {
        return this.invokeAccessible(self, target, args, (method, instance, args$0) -> method.invoke(self, args$0));
    }

    protected Object invokeAccessible(final Object self, final Invokable target, final Object[] args, final MethodInvoker function) {
        target.setAccessible(true);

        Object result;
        if (target instanceof MethodInvokable methodInvokable) {
            result =  function.invoke(methodInvokable.toMethodContext(), self, args)
                    .orElse(() -> TypeContext.of(target.getReturnType()).defaultOrNull())
                    .orNull();
        }
        else {
            try {
                result = target.invoke(self, args);
            }
            catch (final Throwable e) {
                result = TypeContext.of(target.getReturnType()).defaultOrNull();
            }
        }

        target.setAccessible(false);
        return result;
    }

    protected Object invokeDefault(final Class<T> declaringType, final Invokable source, final Object self, final Object[] args) throws Throwable {
        final MethodHandle handle;
        if (METHOD_HANDLE_CACHE.containsKey(source)) {
            handle = METHOD_HANDLE_CACHE.get(source);
        }
        else {
            handle = MethodHandles.lookup().findSpecial(
                    declaringType,
                    source.getName(),
                    MethodType.methodType(source.getReturnType(), source.getParameterTypes()),
                    declaringType
            ).bindTo(self);
            METHOD_HANDLE_CACHE.put(source, handle);
        }
        return handle.invokeWithArguments(args);
    }

    public ApplicationContext applicationContext() {
        return this.applicationContext;
    }
}
