package org.dockbox.hartshorn.proxy.bytebuddy;

import org.dockbox.hartshorn.proxy.JDKInterfaceProxyFactory;
import org.dockbox.hartshorn.proxy.ProxyConstructorFunction;
import org.dockbox.hartshorn.proxy.advice.intercept.ProxyMethodInterceptor;

public class BytebuddyProxyFactory<T> extends JDKInterfaceProxyFactory<T> {

    protected BytebuddyProxyFactory(Class<T> type, BytebuddyProxyOrchestrator proxyOrchestrator) {
        super(type, proxyOrchestrator);
    }

    @Override
    protected ProxyConstructorFunction<T> concreteOrAbstractEnhancer(ProxyMethodInterceptor<T> interceptor) {
        return new BytebuddyProxyConstructorFunction<>(this.type(), this.proxyInterfaces(false), interceptor);
    }
}
