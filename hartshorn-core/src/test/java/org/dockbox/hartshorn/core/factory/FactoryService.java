package org.dockbox.hartshorn.core.factory;

import org.dockbox.hartshorn.core.annotations.Factory;
import org.dockbox.hartshorn.core.annotations.stereotype.Service;

@Service
public interface FactoryService {
    @Factory
    FactoryProvided provide(String name);
}
