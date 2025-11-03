package org.dockbox.hartshorn.profiles;

import org.dockbox.hartshorn.properties.PropertyRegistry;

/**
 * A {@link PropertyRegistry} that is aware of an associated {@link ProfileRegistry}, and therefore its
 * {@link EnvironmentProfile}s.
 *
 * @since 0.7.0
 *
 * @author Guus Lieben
 */
public interface ProfilePropertyRegistry extends PropertyRegistry {

    /**
     * The associated {@link ProfileRegistry}.
     *
     * @return The profile registry
     */
    ProfileRegistry profileRegistry();
}
