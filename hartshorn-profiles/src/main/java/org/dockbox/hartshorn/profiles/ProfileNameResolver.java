package org.dockbox.hartshorn.profiles;

import org.dockbox.hartshorn.properties.PropertyRegistry;

import java.util.Set;

/**
 * Resolves profile names from a given property registry. This interface can be implemented to provide
 * custom logic for determining active profiles based on application properties or related components.
 *
 * @since 0.7.0
 *
 * @author Guus Lieben
 */
public interface ProfileNameResolver {

    /**
     * Resolves profile names from the provided root property registry.
     *
     * @param rootRegistry the root property registry to resolve profile names from
     * @return a set of resolved profile names
     */
    Set<String> resolveProfileNames(PropertyRegistry rootRegistry);
}
