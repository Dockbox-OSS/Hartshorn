package org.dockbox.hartshorn.proxy;

import org.dockbox.hartshorn.application.context.ApplicationContext;
import org.dockbox.hartshorn.proxy.javassist.JavassistProxyFactory;
import org.dockbox.hartshorn.util.ApplicationException;
import org.dockbox.hartshorn.util.CollectionUtilities;
import org.dockbox.hartshorn.util.Result;
import org.dockbox.hartshorn.util.reflect.FieldContext;
import org.dockbox.hartshorn.util.reflect.TypeContext;

import java.lang.reflect.InvocationHandler;

public abstract class JDKInterfaceProxyFactory<T> extends DefaultProxyFactory<T> {

    protected JDKInterfaceProxyFactory(final Class<T> type, final ApplicationContext applicationContext) {
        super(type, applicationContext);
    }

    @Override
    public Result<T> proxy() throws ApplicationException {
        final LazyProxyManager<T> manager = new LazyProxyManager<>(this.applicationContext(), this);
        final StandardMethodInvocationHandler<T> invocationHandler = new StandardMethodInvocationHandler<>(manager, this.applicationContext());

        final Result<T> proxy = this.type().isInterface()
                ? this.interfaceProxy(invocationHandler)
                : this.concreteOrAbstractProxy(invocationHandler);

        proxy.present(manager::proxy);
        return proxy;
    }

    protected InvocationHandler invocationHandler(final StandardMethodInvocationHandler<T> invocationHandler) {
        return (self, method, args) -> invocationHandler.invoke(self, new MethodInvokable(method), null, args);
    }

    protected abstract ProxyConstructorFunction<T> concreteOrAbstractEnhancer(StandardMethodInvocationHandler<T> invocationHandler);

    protected Result<T> concreteOrAbstractProxy(final StandardMethodInvocationHandler<T> invocationHandler) throws ApplicationException {
        final ProxyConstructorFunction<T> enhancer = this.concreteOrAbstractEnhancer(invocationHandler);
        try {
            final T proxy = enhancer.create();
            if (this.typeDelegate() != null) this.restoreFields(this.typeDelegate(), proxy);
            return Result.of(proxy);
        }
        catch (final RuntimeException e) {
            throw new ApplicationException(e);
        }
    }

    protected Class<?>[] proxyInterfaces(final boolean includeType) {
        final Class<?>[] standardInterfaces = includeType
                ? new Class<?>[] { Proxy.class, this.type() }
                : new Class<?>[] { Proxy.class };
        return CollectionUtilities.merge(standardInterfaces, this.interfaces().toArray(new Class[0]));
    }

    protected Result<T> interfaceProxy(final StandardMethodInvocationHandler invocationHandler) {
        final T proxy = (T) java.lang.reflect.Proxy.newProxyInstance(
                this.defaultClassLoader(),
                this.proxyInterfaces(true),
                (final var self, final var method, final var args) -> invocationHandler.invoke(self, new MethodInvokable(method), null, args));
        return Result.of(proxy);
    }

    protected void restoreFields(final T existing, final T proxy) {
        final TypeContext<T> typeContext = this.applicationContext().environment().manager().isProxy(existing)
                ? TypeContext.of(this.type())
                : TypeContext.of(this.typeDelegate());
        for (final FieldContext<?> field : typeContext.fields()) {
            if (field.isStatic()) continue;
            field.set(proxy, field.get(existing).orNull());
        }
    }

    protected ClassLoader defaultClassLoader() {
        return Result.of(Thread.currentThread()::getContextClassLoader)
                .orElse(JavassistProxyFactory.class::getClassLoader)
                .orElse(ClassLoader::getSystemClassLoader)
                .orElse(this.type()::getClassLoader)
                .orNull();
    }
}
