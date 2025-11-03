package org.dockbox.hartshorn.profiles.support;

import org.dockbox.hartshorn.profiles.ProfilePropertyRegistry;
import org.dockbox.hartshorn.profiles.ProfileRegistry;
import org.dockbox.hartshorn.properties.ConfiguredProperty;
import org.dockbox.hartshorn.properties.MapPropertyRegistry;
import org.dockbox.hartshorn.properties.loader.path.PropertyPathStyle;

import java.util.Map;

/**
 * A {@link MapPropertyRegistry} that is also a {@link ProfilePropertyRegistry}, meaning it is aware of
 * an associated {@link ProfileRegistry}.
 *
 * @since 0.7.0
 *
 * @author Guus Lieben
 */
public class ProfileMapPropertyRegistry extends MapPropertyRegistry implements ProfilePropertyRegistry {

    private final ProfileRegistry profileRegistry;

    public ProfileMapPropertyRegistry(ProfileRegistry profileRegistry) {
        this.profileRegistry = profileRegistry;
    }

    public ProfileMapPropertyRegistry(Map<String, ConfiguredProperty> properties, ProfileRegistry profileRegistry) {
        super(properties);
        this.profileRegistry = profileRegistry;
    }

    public ProfileMapPropertyRegistry(PropertyPathStyle pathStyle, ProfileRegistry profileRegistry) {
        super(pathStyle);
        this.profileRegistry = profileRegistry;
    }

    public ProfileMapPropertyRegistry(Map<String, ConfiguredProperty> properties, PropertyPathStyle pathStyle, ProfileRegistry profileRegistry) {
        super(properties, pathStyle);
        this.profileRegistry = profileRegistry;
    }

    @Override
    public ProfileRegistry profileRegistry() {
        return profileRegistry;
    }
}
