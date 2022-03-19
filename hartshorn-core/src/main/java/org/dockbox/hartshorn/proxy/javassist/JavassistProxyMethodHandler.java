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

package org.dockbox.hartshorn.proxy.javassist;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.context.ContextCarrier;
import org.dockbox.hartshorn.application.context.ParameterLoaderContext;
import org.dockbox.hartshorn.util.reflect.MethodContext;
import org.dockbox.hartshorn.util.reflect.MethodInvoker;
import org.dockbox.hartshorn.util.reflect.TypeContext;
import org.dockbox.hartshorn.util.Exceptional;
import org.dockbox.hartshorn.proxy.CustomInvocation;
import org.dockbox.hartshorn.proxy.MethodInterceptor;
import org.dockbox.hartshorn.proxy.MethodInterceptorContext;
import org.dockbox.hartshorn.proxy.MethodWrapper;
import org.dockbox.hartshorn.proxy.ProxyManager;
import org.dockbox.hartshorn.util.parameter.ParameterLoader;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;

import javassist.util.proxy.MethodHandler;
import javassist.util.proxy.ProxyFactory;

public class JavassistProxyMethodHandler<T> implements MethodHandler, ContextCarrier {

    private static final Map<Method, MethodHandle> METHOD_HANDLE_CACHE = new ConcurrentHashMap<>();

    private final ProxyManager<T> manager;
    private final ApplicationContext applicationContext;
    private final ParameterLoader<ParameterLoaderContext> parameterLoader = new UnproxyingParameterLoader();

    public JavassistProxyMethodHandler(final ProxyManager<T> manager, final ApplicationContext applicationContext) {
        this.manager = manager;
        this.applicationContext = applicationContext;
    }

    public ProxyManager<T> manager() {
        return this.manager;
    }

    @Override
    public Object invoke(final Object self, final Method thisMethod, final Method proceed, final Object[] args) throws Throwable {
        final T callbackTarget = this.manager.delegate().or((T) self);
        final MethodContext<?, T> methodContext = (MethodContext<?, T>) MethodContext.of(thisMethod);

        final CustomInvocation customInvocation = args$0 -> {
            if (this.manager.delegate().present()) {
                return this.invokeDelegate(this.manager.delegate().get(), thisMethod, args$0);
            }
            if (proceed == null) return methodContext.returnType().defaultOrNull();
            return proceed.invoke(callbackTarget, args$0);
        };

        final Callable<?> proceedCallable = () -> customInvocation.call(args);

        final Object[] arguments = this.resolveArgs(thisMethod, self, args);

        final Set<MethodWrapper<T>> wrappers = this.manager.wrappers(thisMethod);

        for (final MethodWrapper<T> wrapper : wrappers) wrapper.acceptBefore(methodContext, callbackTarget, arguments);

        try {
            final Object result;
            final Exceptional<MethodInterceptor<T>> interceptor = this.manager.interceptor(thisMethod);
            if (interceptor.present()) {
                result = this.invokeInterceptor(interceptor.get(), callbackTarget, thisMethod, proceedCallable, customInvocation, arguments);
            }
            else {
                final Exceptional<T> delegate = this.manager.delegate(thisMethod);
                if (delegate.present()) {
                    result = this.invokeDelegate(delegate.get(), callbackTarget, thisMethod, arguments);
                }
                else {
                    if (callbackTarget == self && proceed != null) {
                        result = proceed.invoke(callbackTarget, arguments);
                    }
                    else {
                        result = this.invokeUnregistered(self, thisMethod, proceed, arguments);
                    }
                }
            }

            for (final MethodWrapper<T> wrapper : wrappers) wrapper.acceptAfter(methodContext, callbackTarget, arguments);
            return result;
        }
        catch (final Throwable e) {
            for (final MethodWrapper<T> wrapper : wrappers) wrapper.acceptError(methodContext, callbackTarget, arguments, e);
            throw e;
        }
    }

    protected Object invokeInterceptor(final MethodInterceptor<T> interceptor, final Object self, final Method thisMethod, final Callable<?> proceed, final CustomInvocation customInvocation, final Object[] args) throws Throwable {
        final MethodInterceptorContext<T> context = new MethodInterceptorContext(thisMethod, args, self, proceed, customInvocation);
        return interceptor.intercept(context);
    }

    protected Object invokeDelegate(final T delegate, final Object self, final Method thisMethod, final Object[] args) throws Throwable {
        final Object result = thisMethod.invoke(delegate, args);
        if (result == delegate) return self;
        return result;
    }

    protected Object invokeUnregistered(final Object self, final Method thisMethod, final Method proceed, final Object[] args) throws Throwable {
        // If no handler is known, default to the original method. This is delegated to the instance
        // created, as it is typically created through Hartshorn's injectors and therefore DI dependent.
        Method target = thisMethod;
        if (this.manager.delegate().absent() && thisMethod == null)
            target = proceed;

        final Class<T> targetClass = this.manager.targetClass();

        if (target != null) {
            // If the target type is a class, whether it is abstract or not, we can invoke the native method directly. However, when
            // the target type is an interface, this method is not defined, requiring us to perform our own equality check.
            if (target.getName().equals("equals")
                    && target.getDeclaringClass().equals(Object.class)
                    && this.manager.delegate().absent()
                    && targetClass.isInterface()
            ) return this.proxyEquals(args[0]);

            return this.invokeTarget(self, thisMethod, target, args);
        }
        else {
            final StackTraceElement element = Thread.currentThread().getStackTrace()[3];
            final String name = element.getMethodName();
            final String className = targetClass == null ? "" : targetClass.getSimpleName() + ".";
            throw new AbstractMethodError("Cannot invoke method '" + className + name + "' because it is abstract. This type is proxied, but no proxy property was found for the method.");
        }
    }

    protected boolean proxyEquals(final Object obj) {
        if (obj == null) return false;
        if (this.manager.delegate().map(instance -> instance.equals(obj)).or(false)) return true;
        return this.manager.proxy() == obj;
    }

    protected Object invokeTarget(final Object self, final Method thisMethod, final Method target, final Object[] args) throws Throwable {
        final Class<T> targetClass = this.manager.targetClass();
        final TypeContext<T> targetType = TypeContext.of(targetClass);

        try {
            // If the proxy associated with this handler has a delegate, use it.
            if (this.manager.delegate().present()) return this.invokeDelegate(this.manager.delegate().get(), target, args);

                // If the method is default inside an interface, we cannot invoke it directly using a proxy instance. Instead we
                // need to lookup the method on the class and invoke it through the method handle directly.
            else if (thisMethod.isDefault()) return this.invokeDefault(targetClass, thisMethod, self, args);

                // If the current target instance (self) is not a proxy, we can invoke the method directly using reflections.
            else if (!(self instanceof Proxy || ProxyFactory.isProxyClass(self.getClass()))) return this.invokeSelf(self, target, args);

                // If the target method is concrete in an abstract class, we cannot invoke the method directly using reflections.
                // This solution uses private lookups to invoke the method, unreflecting the method first so it can be invoked using
                // proxy instances.
            else if (targetType.isAbstract() && !MethodContext.of(thisMethod).isAbstract()) return this.invokePrivate(targetClass, thisMethod, self, args);

                // If none of the above conditions are met, we have no way to handle the method.
            else {
                final MethodContext<?, ?> localMethodContext = MethodContext.of(thisMethod);
                final MethodContext<?, ?> targetMethodContext = MethodContext.of(target);
                final TypeContext<Object> selfContext = TypeContext.of(self);
                throw new IllegalStateException("Could not invoke local method " + localMethodContext.qualifiedName()
                        + " (targeting " + targetMethodContext.qualifiedName() + ") on proxy "
                        + targetType.qualifiedName() + " of qualified type " + selfContext.qualifiedName() + "(isProxy=" + this.applicationContext().environment().manager().isProxy(self) + ")"
                        + " with arguments " + Arrays.toString(args) + ". " +
                        "This typically indicates that there is no appropriate proxy property (delegate or interceptor for the method.");
            }
        }
        catch (final InvocationTargetException e) {
            throw e.getCause();
        }
    }

    protected Object[] resolveArgs(final Method method, final Object instance, final Object[] args) {
        final MethodContext<?, ?> methodContext = MethodContext.of(method);
        final ParameterLoaderContext context = new ParameterLoaderContext(methodContext, methodContext.parent(), instance, this.applicationContext());
        return this.parameterLoader.loadArguments(context, args).toArray();
    }

    protected Object invokeDelegate(final Object self, final Method target, final Object[] args) {
        return this.invokeAccessible(self, target, args, (method, instance, args$0) -> method.invoke(this.manager.delegate().get(), args$0));
    }

    protected Object invokeSelf(final Object self, final Method target, final Object[] args) {
        return this.invokeAccessible(self, target, args, (method, instance, args$0) -> method.invoke(self, args$0));
    }

    protected Object invokeAccessible(final Object self, final Method target, final Object[] args, final MethodInvoker function) {
        target.setAccessible(true);
        final Object result = function.invoke(MethodContext.of(target), self, args)
                .orElse(() -> TypeContext.of(target.getReturnType()).defaultOrNull())
                .orNull();
        target.setAccessible(false);
        return result;
    }

    protected Object invokeDefault(final Class<T> declaringType, final Method thisMethod, final Object self, final Object[] args) throws Throwable {
        final MethodHandle handle;
        if (METHOD_HANDLE_CACHE.containsKey(thisMethod)) {
            handle = METHOD_HANDLE_CACHE.get(thisMethod);
        }
        else {
            handle = MethodHandles.lookup().findSpecial(
                    declaringType,
                    thisMethod.getName(),
                    MethodType.methodType(thisMethod.getReturnType(), thisMethod.getParameterTypes()),
                    declaringType
            ).bindTo(self);
            METHOD_HANDLE_CACHE.put(thisMethod, handle);
        }
        return handle.invokeWithArguments(args);
    }

    protected Object invokePrivate(final Class<T> declaringType, final Method thisMethod, final Object self, final Object[] args) throws Throwable {
        final MethodHandle handle;
        if (METHOD_HANDLE_CACHE.containsKey(thisMethod)) {
            handle = METHOD_HANDLE_CACHE.get(thisMethod);
        }
        else {
            handle = MethodHandles.privateLookupIn(declaringType, MethodHandles.lookup())
                    .in(declaringType)
                    .unreflectSpecial(thisMethod, declaringType)
                    .bindTo(self);
            METHOD_HANDLE_CACHE.put(thisMethod, handle);
        }
        return handle.invokeWithArguments(args);
    }

    @Override
    public ApplicationContext applicationContext() {
        return this.applicationContext;
    }
}
