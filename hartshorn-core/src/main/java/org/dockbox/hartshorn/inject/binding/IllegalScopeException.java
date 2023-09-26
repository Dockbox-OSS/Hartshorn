package org.dockbox.hartshorn.inject.binding;

import org.dockbox.hartshorn.util.ApplicationException;

/**
 * Thrown when a scope is not valid for a given binding. For example, when a singleton scope is installed
 * on a binding that does not allow for singletons.
 *
 * @author Guus Lieben
 * @since 0.5.0
 *
 * @see BindingFunction
 */
public class IllegalScopeException extends ApplicationException {

    public IllegalScopeException(String message) {
        super(message);
    }

    public IllegalScopeException(String message, Throwable cause) {
        super(message, cause);
    }
}
