package org.dockbox.hartshorn.inject;

import org.dockbox.hartshorn.util.Tristate;

/**
 * Utility class for injector-related operations.
 *
 * @since 0.7.0
 *
 * @author Guus Lieben
 */
public final class InjectorUtilities {

    private InjectorUtilities() {
    }

    /**
     * Determines whether the given key should be treated in strict mode. If the key has a defined
     * strictness, that value is used. Otherwise, the global configuration is used.
     *
     * @param key the component key
     * @param configuration the injector configuration
     *
     * @return true if the key should be treated in strict mode, false otherwise
     */
    public static boolean isStrict(ComponentKey<?> key, InjectorConfiguration configuration) {
        Tristate strict = key.strict();
        if (strict == Tristate.UNDEFINED) {
            return configuration.isStrictMode();
        }
        else {
            return strict.booleanValue();
        }
    }
}
