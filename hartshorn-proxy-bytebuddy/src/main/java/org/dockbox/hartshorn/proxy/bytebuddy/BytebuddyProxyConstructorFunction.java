package org.dockbox.hartshorn.proxy.bytebuddy;

import java.lang.reflect.Constructor;

import org.dockbox.hartshorn.proxy.ProxyConstructorFunction;
import org.dockbox.hartshorn.proxy.advice.intercept.ProxyMethodInterceptor;
import org.dockbox.hartshorn.util.ApplicationException;
import org.jetbrains.annotations.NotNull;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.dynamic.DynamicType.Loaded;
import net.bytebuddy.dynamic.DynamicType.Unloaded;
import net.bytebuddy.matcher.ElementMatchers;

public class BytebuddyProxyConstructorFunction<T> implements ProxyConstructorFunction<T> {

    private final Class<T> baseClass;
    private final Class<?>[] interfaces;
    private final ProxyMethodInterceptor<T> interceptor;

    public BytebuddyProxyConstructorFunction(Class<T> baseClass, Class<?>[] interfaces, ProxyMethodInterceptor<T> interceptor) {
        this.baseClass = baseClass;
        this.interfaces = interfaces;

        this.interceptor = interceptor;
    }

    @Override
    public T create() throws ApplicationException {
        Class<? extends T> proxyClass = createProxyClass();
        try {
            Constructor<? extends T> constructor = proxyClass.getConstructor();
            return constructor.newInstance();
        }
        catch(ReflectiveOperationException | SecurityException | IllegalArgumentException e) {
            throw new ApplicationException(e);
        }
    }

    @Override
    public T create(Constructor<? extends T> constructor, Object[] args) throws ApplicationException {
        Class<? extends T> proxyClass = createProxyClass();
        try {
            Constructor<? extends T> proxyConstructor = proxyClass.getConstructor(constructor.getParameterTypes());
            return proxyConstructor.newInstance(args);
        }
        catch(ReflectiveOperationException | SecurityException | IllegalArgumentException e) {
            throw new ApplicationException(e);
        }
    }

    @NotNull
    private Class<? extends T> createProxyClass() {
        Unloaded<T> unloadedType = new ByteBuddy()
                .subclass(baseClass)
                .implement(interfaces)
                .implement(InterceptorAwareProxy.class)
                .method(ElementMatchers.isDeclaredBy(InterceptorAwareProxy.class).and(ElementMatchers.named("$$__interceptor")))
                .defaultValue(interceptor, ProxyMethodInterceptor.class)
                .method(ElementMatchers.any())
                .intercept(Advice.to(BytebuddyAdviceInterceptor.class))
                .make();
        Loaded<T> loadedType = unloadedType.load(BytebuddyProxyOrchestrator.class.getClassLoader());
        return loadedType.getLoaded();
    }
}
