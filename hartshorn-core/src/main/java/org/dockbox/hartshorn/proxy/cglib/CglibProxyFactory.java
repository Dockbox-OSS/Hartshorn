package org.dockbox.hartshorn.proxy.cglib;

import net.sf.cglib.core.NamingPolicy;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.proxy.DefaultProxyFactory;
import org.dockbox.hartshorn.proxy.Invokable;
import org.dockbox.hartshorn.proxy.JDKInterfaceProxyFactory;
import org.dockbox.hartshorn.proxy.MethodInvokable;
import org.dockbox.hartshorn.proxy.ProxyConstructorFunction;
import org.dockbox.hartshorn.proxy.StandardMethodInvocationHandler;

public class CglibProxyFactory<T> extends JDKInterfaceProxyFactory<T> {

    private static final NamingPolicy NAMING_POLICY = (prefix, className, key, names) -> DefaultProxyFactory.NAME_GENERATOR.get(className);

    public CglibProxyFactory(final Class<T> type, final ApplicationContext applicationContext) {
        super(type, applicationContext);
    }

    @Override
    protected ProxyConstructorFunction<T> concreteOrAbstractEnhancer(final StandardMethodInvocationHandler<T> invocationHandler) {
        final Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(this.type());
        enhancer.setInterfaces(this.proxyInterfaces(false));
        enhancer.setNamingPolicy(NAMING_POLICY);

        enhancer.setCallback((MethodInterceptor) (obj, method, args, proxy) -> {
            final MethodInvokable realMethod = new MethodInvokable(method);
            final Invokable proxyMethod = new ProxyMethodInvokable(proxy, obj, method.getReturnType(), method.getParameterTypes());
            return invocationHandler.invoke(obj, realMethod, proxyMethod, args);
        });
        return () -> this.type().cast(enhancer.create());
    }
}
