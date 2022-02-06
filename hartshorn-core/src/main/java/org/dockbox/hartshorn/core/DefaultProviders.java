package org.dockbox.hartshorn.core;

import org.dockbox.hartshorn.core.annotations.activate.UseBootstrap;
import org.dockbox.hartshorn.core.annotations.activate.UseServiceProvision;
import org.dockbox.hartshorn.core.annotations.inject.Provider;
import org.dockbox.hartshorn.core.annotations.stereotype.Service;
import org.dockbox.hartshorn.core.context.ConcreteContextCarrier;
import org.dockbox.hartshorn.core.context.ContextCarrier;
import org.dockbox.hartshorn.core.proxy.DelegatorAccessor;
import org.dockbox.hartshorn.core.proxy.DelegatorAccessorImpl;

import javax.inject.Singleton;

@Service(activators = { UseBootstrap.class, UseServiceProvision.class })
public class DefaultProviders {

    @Provider
    @Singleton
    public Class<? extends ContextCarrier> contextCarrier() {
        return ConcreteContextCarrier.class;
    }

    @Provider
    public Class<? extends DelegatorAccessor> delegatorAccessor() {
        return DelegatorAccessorImpl.class;
    }
}
