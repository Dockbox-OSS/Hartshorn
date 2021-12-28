package org.dockbox.hartshorn.core.factory;

import org.dockbox.hartshorn.core.annotations.inject.Binds;
import org.dockbox.hartshorn.core.annotations.inject.Bound;

@Binds(value = FactoryProvided.class, priority = 1)
public class HighPriorityFactoryBound implements FactoryProvided {
    @Bound
    public HighPriorityFactoryBound(final String name) {
        // Name is ignored
    }
}
