package org.dockbox.hartshorn.proxy.bytebuddy;

import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.BindingPriority;
import net.bytebuddy.implementation.bind.annotation.Morph;
import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.SuperCall;
import net.bytebuddy.implementation.bind.annotation.This;

import org.dockbox.hartshorn.proxy.CustomInvocation;
import org.dockbox.hartshorn.proxy.MethodInterceptor;
import org.dockbox.hartshorn.proxy.MethodInterceptorContext;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;

public class BytebuddyInterceptor {

    private final MethodInterceptor<Object> interceptor;

    public BytebuddyInterceptor(final MethodInterceptor<?> interceptor) {
        this.interceptor = (MethodInterceptor<Object>) interceptor;
    }

    @RuntimeType
    @BindingPriority(10)
    public Object intercept(
            @AllArguments final Object[] args,
            @This final Object instance,
            @Origin final Method method,
            @Morph final CustomInvocation morph,
            @SuperCall(nullIfImpossible = true) final Callable<Object> defaultMethod
    ) throws Throwable {
        final MethodInterceptorContext<Object> context = new MethodInterceptorContext(method, args, instance, defaultMethod, morph, null);
        return this.interceptor.intercept(context);
    }

    @RuntimeType
    @BindingPriority(5)
    public Object intercept(
            @AllArguments final Object[] args,
            @This(optional = true) final Object instance,
            @Origin final Method method,
            @SuperCall(nullIfImpossible = true) final Callable<Object> defaultMethod
    ) throws Throwable {
        // Do not provide `(args0) -> null` as alternative custom invocation. By setting it to null, we allow the interceptor context to use
        // the previous result if it is available.
        final MethodInterceptorContext<Object> context = new MethodInterceptorContext(method, args, instance, defaultMethod, null, null);
        return this.interceptor.intercept(context);
    }
}
