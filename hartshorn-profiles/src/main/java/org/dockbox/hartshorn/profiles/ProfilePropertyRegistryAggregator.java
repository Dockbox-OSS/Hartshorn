package org.dockbox.hartshorn.profiles;

import org.dockbox.hartshorn.properties.PropertyRegistry;

public interface ProfilePropertyRegistryAggregator {

    PropertyRegistry aggregate(ProfileRegistry profileRegistry);
}
