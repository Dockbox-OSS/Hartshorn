package org.dockbox.hartshorn.inject;

import org.dockbox.hartshorn.inject.component.ComponentRegistry;

public interface ManagedComponentEnvironment extends InjectorEnvironment {

    /**
     * @return the {@link ComponentRegistry} that is used by this {@link ManagedComponentEnvironment} to locate components
     */
    ComponentRegistry componentRegistry();
}
