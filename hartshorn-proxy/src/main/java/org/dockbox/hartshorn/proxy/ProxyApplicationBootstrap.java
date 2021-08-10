package org.dockbox.hartshorn.proxy;

import org.dockbox.hartshorn.api.HartshornBootstrap;
import org.dockbox.hartshorn.api.domain.Exceptional;

public abstract class ProxyApplicationBootstrap extends HartshornBootstrap {

    @Override
    public <T> Exceptional<T> proxy(Class<T> type, T instance) {
        return Exceptional.of(() -> ProxyUtil.handler(type, instance).proxy());
    }
}
