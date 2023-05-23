package org.dockbox.hartshorn.proxy.advice;

import org.dockbox.hartshorn.proxy.ProxyManager;
import org.dockbox.hartshorn.proxy.ProxyObject;
import org.dockbox.hartshorn.proxy.advice.intercept.CustomInvocation;
import org.dockbox.hartshorn.proxy.advice.intercept.Invokable;
import org.dockbox.hartshorn.proxy.advice.intercept.MethodInterceptor;
import org.dockbox.hartshorn.proxy.advice.intercept.MethodInvokable;
import org.dockbox.hartshorn.proxy.advice.intercept.ProxyMethodInterceptor;
import org.dockbox.hartshorn.util.TypeUtils;
import org.dockbox.hartshorn.util.introspect.view.MethodView;
import org.dockbox.hartshorn.util.option.Option;

public class ReflectionProxyMethodInterceptHandler<T> implements ProxyMethodInterceptHandler<T>, ProxyObject<T> {

    private final ProxyMethodInvoker<T> methodInvoker;
    private final ProxyManager<T> manager;

    public ReflectionProxyMethodInterceptHandler(final ProxyMethodInterceptor<T> interceptor) {
        this.methodInvoker = new ReflectionProxyMethodInvoker<>(interceptor);
        this.manager = interceptor.manager();
    }

    @Override
    public Object handleNonInterceptedMethod(final T self, final MethodInvokable source, final Invokable proxy, final T callbackTarget, final Object[] arguments) throws Throwable {
        final Option<?> delegate = this.manager()
                .advisor()
                .resolver()
                .method(source.toMethod())
                .delegate();

        if (delegate.present()) {
            return this.handleDelegateMethod(delegate.get(), callbackTarget, source, arguments);
        }
        else {
            return this.handleNonDelegateMethod(self, callbackTarget, source, proxy, arguments);
        }
    }

    @Override
    public Object handleInterceptedMethod(final MethodInvokable source, final T callbackTarget, final CustomInvocation<?> customInvocation, final Object[] arguments, final MethodInterceptor<T, Object> interceptor) throws Throwable {
        return this.methodInvoker.invokeInterceptor(callbackTarget,
                TypeUtils.adjustWildcards(source.toIntrospector(), MethodView.class),
                arguments,
                interceptor,
                TypeUtils.adjustWildcards(customInvocation, CustomInvocation.class)
        );
    }

    @Override
    public ProxyMethodInvoker<T> methodInvoker() {
        return this.methodInvoker;
    }

    protected Object handleDelegateMethod(final Object delegate, final T self, final Invokable source, final Object[] args) throws Throwable {
        final Option<Object> defaultMethod = this.tryInvokeDefaultMethod(self, source, args);
        if (defaultMethod.present()) return defaultMethod.get();

        final Object result = source.invoke(delegate, args);
        if (result == delegate) {
            return self;
        }
        return result;
    }

    protected Object handleNonDelegateMethod(final T self, final T callbackTarget, final Invokable source, final Invokable proxy, final Object[] args) throws Throwable {
        final Option<Object> defaultMethod = this.tryInvokeDefaultMethod(self, source, args);
        if (defaultMethod.present()) return defaultMethod.get();

        final Object result;
        if (callbackTarget == self && proxy != null) {
            result = proxy.invoke(callbackTarget, args);
        }
        else {
            result = this.methodInvoker.invokeReal(self, source, source, args);
        }
        return result;
    }

    protected Option<Object> tryInvokeDefaultMethod(final T self, final Invokable target, final Object[] args) {
        return Option.of(() -> {
            if (this.isEqualsMethod(target)){
                return this.proxyEquals(args[0]);
            }
            if (this.isToStringMethod(target)) return this.proxyToString(self);
            if (this.isHashCodeMethod(target)) return this.proxyHashCode(self);

            throw new UnsupportedOperationException("Unsupported default method: " + target.qualifiedName());
        });
    }

    @Override
    public ProxyManager<T> manager() {
        return this.manager;
    }
}
