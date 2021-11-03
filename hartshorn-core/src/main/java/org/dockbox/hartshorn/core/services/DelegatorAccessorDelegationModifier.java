package org.dockbox.hartshorn.core.services;

import org.dockbox.hartshorn.core.annotations.proxy.UseProxying;
import org.dockbox.hartshorn.core.context.ApplicationContext;
import org.dockbox.hartshorn.core.context.element.TypeContext;
import org.dockbox.hartshorn.core.properties.Attribute;
import org.dockbox.hartshorn.core.proxy.ProxyHandler;
import org.dockbox.hartshorn.core.proxy.DelegatorAccessor;

public class DelegatorAccessorDelegationModifier extends ProxyDelegationModifier<DelegatorAccessor, UseProxying> {

    @Override
    public Class<UseProxying> activator() {
        return UseProxying.class;
    }

    @Override
    protected Class<DelegatorAccessor> parentTarget() {
        return DelegatorAccessor.class;
    }

    @Override
    protected DelegatorAccessor concreteDelegator(final ApplicationContext context, final ProxyHandler<DelegatorAccessor> handler, final TypeContext<? extends DelegatorAccessor> parent, final Attribute<?>... attributes) {
        return context.get(DelegatorAccessor.class, handler);
    }
}
