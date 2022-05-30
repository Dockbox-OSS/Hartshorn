package org.dockbox.hartshorn.proxy;

import org.dockbox.hartshorn.util.ApplicationException;

@FunctionalInterface
public interface ProxyConstructorFunction<T> {
    T create() throws ApplicationException;
}
