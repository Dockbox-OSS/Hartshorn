package org.dockbox.hartshorn.proxy;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.context.DefaultContext;

public class MethodStubContext<T> extends DefaultContext {

    // TODO: Determine what other information is needed here

    private final T self;
    private final Invokable source;
    private final Invokable target;
    private final ProxyMethodInterceptor<T> interceptor;
    private final Object[] args;

    public MethodStubContext(final T self,
                             final Invokable source,
                             final Invokable target,
                             final ProxyMethodInterceptor<T> interceptor,
                             final Object[] args) {
        this.self = self;
        this.source = source;
        this.target = target;
        this.interceptor = interceptor;
        this.args = args;
    }

    public T self() {
        return this.self;
    }

    public Invokable source() {
        return this.source;
    }

    public Invokable target() {
        return this.target;
    }

    public ProxyMethodInterceptor<T> interceptor() {
        return this.interceptor;
    }

    public ProxyManager<T> manager() {
        return this.interceptor.manager();
    }

    public ApplicationContext applicationContext() {
        return this.interceptor.applicationContext();
    }

    public Object[] args() {
        return this.args;
    }
}
