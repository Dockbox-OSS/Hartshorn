package org.dockbox.hartshorn.beans;

import org.dockbox.hartshorn.util.reflect.TypeContext;

public interface BeanCollector {
    <T> BeanReference<T> register(T bean, TypeContext<T> type, String id);

    void unregister(BeanReference<?> beanReference);
}
