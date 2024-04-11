package org.dockbox.hartshorn.component.populate;

import org.dockbox.hartshorn.component.ComponentModificationException;

public class ComponentInjectionException extends ComponentModificationException {

    public ComponentInjectionException(String message) {
        super(message);
    }

    public ComponentInjectionException(String message, Throwable cause) {
        super(message, cause);
    }

    public ComponentInjectionException(Throwable cause) {
        super(cause);
    }
}
