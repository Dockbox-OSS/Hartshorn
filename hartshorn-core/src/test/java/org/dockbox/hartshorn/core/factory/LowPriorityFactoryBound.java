package org.dockbox.hartshorn.core.factory;

import org.dockbox.hartshorn.core.annotations.inject.Binds;
import org.dockbox.hartshorn.core.annotations.inject.Bound;

@Binds(FactoryProvided.class)
public class LowPriorityFactoryBound implements FactoryProvided {
    @Bound
    public LowPriorityFactoryBound(final String name) {
        // Name is ignored
    }
}
