package org.dockbox.hartshorn.sponge;

import org.dockbox.hartshorn.di.ContextCarrier;
import org.dockbox.hartshorn.di.context.ApplicationContext;

public interface SpongeContextCarrier extends ContextCarrier {

    @Override
    default ApplicationContext applicationContext() {
        return Sponge8Application.context();
    }
}
