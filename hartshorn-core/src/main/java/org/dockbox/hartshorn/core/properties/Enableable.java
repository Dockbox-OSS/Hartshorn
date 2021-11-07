package org.dockbox.hartshorn.core.properties;

import org.dockbox.hartshorn.core.exceptions.ApplicationException;

public interface Enableable {

    default boolean canEnable() {
        return true;
    }

    void enable() throws ApplicationException;
}
