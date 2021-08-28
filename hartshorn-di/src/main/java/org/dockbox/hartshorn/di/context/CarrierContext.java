package org.dockbox.hartshorn.di.context;

import org.dockbox.hartshorn.api.domain.Exceptional;
import org.dockbox.hartshorn.di.ContextCarrier;

public interface CarrierContext extends Context, ContextCarrier {
    <C extends Context> Exceptional<C> first(final Class<C> context);
}
