package org.dockbox.hartshorn.profiles.aggregation;

import org.dockbox.hartshorn.profiles.EnvironmentProfile;
import org.dockbox.hartshorn.profiles.ProfilePropertyRegistry;
import org.dockbox.hartshorn.profiles.ProfileRegistry;
import org.dockbox.hartshorn.properties.PropertyRegistry;

/**
 * Aggregates multiple {@link PropertyRegistry} instances from different {@link EnvironmentProfile}s into a single
 * {@link ProfilePropertyRegistry}, respecting profile priorities.
 *
 * @since 0.7.0
 *
 * @author Guus Lieben
 */
public interface ProfilePropertyRegistryAggregator {

    /**
     * Aggregates the {@link PropertyRegistry} instances from the given {@link ProfileRegistry} into a single
     * {@link ProfilePropertyRegistry}.
     *
     * @param profileRegistry the profile registry containing the profiles to aggregate
     * @return the aggregated profile property registry
     */
    ProfilePropertyRegistry aggregate(ProfileRegistry profileRegistry);
}
