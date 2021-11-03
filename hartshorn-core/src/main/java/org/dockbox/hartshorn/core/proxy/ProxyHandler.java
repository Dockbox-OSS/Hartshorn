package org.dockbox.hartshorn.core.proxy;

import org.dockbox.hartshorn.core.context.Context;
import org.dockbox.hartshorn.core.context.element.TypeContext;
import org.dockbox.hartshorn.core.exceptions.ApplicationException;
import org.dockbox.hartshorn.core.proxy.ProxyAttribute;

public interface ProxyHandler<T> extends Context {
    TypeContext<T> type();

    void delegate(final ProxyAttribute<T, ?>... properties);

    void delegate(final ProxyAttribute<T, ?> property);

    T proxy() throws ApplicationException;

    T proxy(T existing) throws ApplicationException;
}
