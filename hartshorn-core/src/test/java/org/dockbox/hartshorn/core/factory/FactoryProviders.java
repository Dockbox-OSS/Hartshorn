package org.dockbox.hartshorn.core.factory;

import org.dockbox.hartshorn.component.Service;
import org.dockbox.hartshorn.component.processing.Provider;

@Service
public class FactoryProviders {

    @Provider(priority = 1)
    public Class<? extends FactoryProvided> highPriority = HighPriorityFactoryBound.class;

    @Provider
    public Class<? extends FactoryProvided> lowPriority = LowPriorityFactoryBound.class;


}
