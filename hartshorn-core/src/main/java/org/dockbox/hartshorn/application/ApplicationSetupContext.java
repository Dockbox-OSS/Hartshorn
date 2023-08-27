package org.dockbox.hartshorn.application;

import org.dockbox.hartshorn.context.DefaultContext;
import org.dockbox.hartshorn.inject.binding.ConcurrentHashSingletonCache;
import org.dockbox.hartshorn.inject.binding.SingletonCache;

public class ApplicationSetupContext extends DefaultContext {

    private final SingletonCache cache = new ConcurrentHashSingletonCache();

    public SingletonCache cache() {
        return this.cache;
    }
}
