package org.dockbox.hartshorn.proxy.bytebuddy;

import org.dockbox.hartshorn.proxy.Proxy;
import org.dockbox.hartshorn.proxy.advice.intercept.ProxyMethodInterceptor;

public interface InterceptorAwareProxy<T> extends Proxy<T> {

    ProxyMethodInterceptor<T> $$__interceptor();
}
