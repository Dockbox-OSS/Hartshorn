package org.dockbox.hartshorn.proxy.advice;

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

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ReflectionProxyMethodInvoker<T> implements ProxyMethodInvoker<T> {

    private static final Map<Invokable, MethodHandle> METHOD_HANDLE_CACHE = new ConcurrentHashMap<>();

    private final Introspector introspector;
    private final ProxyManager<T> manager;
    private final ProxyMethodInterceptor<T> interceptor;

    public ReflectionProxyMethodInvoker(final ProxyMethodInterceptor<T> interceptor) {
        this.introspector = interceptor.manager().applicationProxier().introspector();
        this.manager = interceptor.manager();
        this.interceptor = interceptor;
    }

    @Override
    public <R> R invokeInterceptor(final T self, final MethodView<T, R> source, final Object[] args, final MethodInterceptor<T, R> interceptor, final CustomInvocation<R> customInvocation) throws Throwable {
        final MethodInterceptorContext<T, R> context = new MethodInterceptorContext<>(source, args, self, customInvocation);
        return interceptor.intercept(context);
    }

    @Override
    public Object invokeDelegate(final T self, final Invokable target, final Object[] args) {
        return this.invokeAccessible(self, target, args, (method, instance, interceptorArgs) -> method.invoke(this.manager.delegate().get(), interceptorArgs));
    }

    @Override
    public Object invokeReal(final T self, final Invokable source, final Invokable target, final Object[] args) throws Throwable {
        if (target != null) {
            return this.invokeTarget(self, source, target, args);
        }
        else {
            return this.invokeStub(self, source, target, args);
        }
    }

    protected Object invokeSelf(final T self, final Invokable target, final Object[] args) {
        return this.invokeAccessible(self, target, args, (method, instance, interceptorArgs) -> method.invoke(self, interceptorArgs));
    }

    protected Object invokeDefault(final T self, final Invokable source, final Object[] args, final Class<T> declaringType) throws Throwable {
        final MethodHandle handle;
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

    protected Object invokeTarget(final T self, final Invokable source, final Invokable target, final Object[] args) throws Throwable {
        final Class<T> targetClass = this.manager.targetClass();

        try {
            // If the proxy associated with this handler has a delegate, use it.
            if (this.manager.delegate().present()) return this.invokeDelegate(this.manager.delegate().get(), target, args);

                // If the method is default inside an interface, we cannot invoke it directly using a proxy instance. Instead, we
                // need to look up the method on the class and invoke it through the method handle directly.
            else if (source.isDefault()) return this.invokeDefault(self, source, args, targetClass);

                // If the current target instance (self) is not a proxy, we can invoke the method directly using reflections.
            else if (!(self instanceof Proxy || Proxy.isProxyClass(self.getClass()))) return this.invokeSelf(self, target, args);

                // If none of the above conditions are met, we have no way to handle the method.
            else return this.invokeStub(self, source, target, args);
        }
        catch (final InvocationTargetException e) {
            throw e.getCause();
        }
    }

    protected Object invokeStub(final T self, final Invokable source, final Invokable target, final Object[] args) throws Throwable {
        final MethodStub<T> stub = this.manager.advisor().resolver().defaultStub().get();
        final MethodStubContext<T> stubContext = new MethodStubContext<>(self, source, target, this.interceptor, args);
        return stub.invoke(stubContext);
    }

    protected Object invokeAccessible(final T self, final Invokable target, final Object[] args, final MethodInvoker<Object, T> function) {
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
            catch (final Throwable e) {
                result = this.introspector.introspect(target.returnType()).defaultOrNull();
            }
        }

        target.setAccessible(false);
        return result;
    }
}
