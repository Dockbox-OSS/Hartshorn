package org.dockbox.hartshorn.core.proxy.javassist;

import org.dockbox.hartshorn.core.context.ApplicationContext;
import org.dockbox.hartshorn.core.context.element.FieldContext;
import org.dockbox.hartshorn.core.context.element.TypeContext;
import org.dockbox.hartshorn.core.domain.Exceptional;
import org.dockbox.hartshorn.core.exceptions.ApplicationException;
import org.dockbox.hartshorn.core.proxy.DefaultProxyFactory;
import org.dockbox.hartshorn.core.proxy.LazyProxyManager;
import org.dockbox.hartshorn.core.proxy.Proxy;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javassist.util.proxy.MethodHandler;
import javassist.util.proxy.ProxyFactory;

public class JavassistProxyFactory<T> extends DefaultProxyFactory<T> {

    private static final Method managerAccessor;

    static {
        try {
            ProxyFactory.nameGenerator = classname -> DefaultProxyFactory.NAME_GENERATOR.get(classname);
            managerAccessor = Proxy.class.getDeclaredMethod("manager");
        }
        catch (final NoSuchMethodException e) {
            throw new IllegalStateException(e);
        }
    }

    public JavassistProxyFactory(final Class<T> type, final ApplicationContext applicationContext) {
        super(type, applicationContext);
    }

    @Override
    public Exceptional<T> proxy() throws ApplicationException {
        final LazyProxyManager<T> manager = new LazyProxyManager<>(this.applicationContext(), this);
        final MethodHandler methodHandler = new JavassistProxyMethodHandler(manager, this.applicationContext());
        this.intercept(managerAccessor, context -> manager);

        final Exceptional<T> proxy = this.type().isInterface()
                ? this.interfaceProxy(methodHandler)
                : this.concreteOrAbstractProxy(methodHandler);

        proxy.present(manager::proxy);
        return proxy;
    }

    protected Exceptional<T> concreteOrAbstractProxy(final MethodHandler methodHandler) throws ApplicationException {
        final ProxyFactory factory = new ProxyFactory();
        factory.setSuperclass(this.type());
        factory.setInterfaces(new Class[] { Proxy.class});

        try {
            final T proxy = (T) factory.create(new Class<?>[0], new Object[0], methodHandler);
            if (this.typeDelegate() != null) this.restoreFields(this.typeDelegate(), proxy);
            return Exceptional.of(proxy);
        }
        catch (final InvocationTargetException | NoSuchMethodException | InstantiationException | IllegalAccessException e) {
            throw new ApplicationException(e);
        }
    }

    protected Exceptional<T> interfaceProxy(final MethodHandler methodHandler) throws ApplicationException {
        final T proxy = (T) java.lang.reflect.Proxy.newProxyInstance(
                this.type().getClassLoader(),
                new Class[] { this.type(), Proxy.class },
                (final var self, final var method, final var args) -> methodHandler.invoke(self, method, null, args));
        return Exceptional.of(proxy);
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
}
