package org.dockbox.hartshorn.profiles.loader;

import java.net.URI;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.dockbox.hartshorn.profiles.ApplicationProfile;
import org.dockbox.hartshorn.profiles.ProfileProperty;
import org.dockbox.hartshorn.profiles.ProfilePropertyRegistry;
import org.dockbox.hartshorn.profiles.SimpleApplicationProfile;
import org.dockbox.hartshorn.profiles.SimpleProfilePropertyRegistry;
import org.dockbox.hartshorn.util.ApplicationException;
import org.dockbox.hartshorn.util.collections.HashBiMap;
import org.dockbox.hartshorn.util.resources.ResourceLookup;

public abstract class ResourceLookupApplicationProfileLoader implements ApplicationProfileLoader {

    private final ResourceLookup resourceLookup;

    protected ResourceLookupApplicationProfileLoader(ResourceLookup resourceLookup) {
        this.resourceLookup = resourceLookup;
    }

    public ResourceLookup resourceLookup() {
        return resourceLookup;
    }

    protected abstract String fileName(ApplicationProfile parentProfile, String profileName);

    protected abstract ProfilePropertiesLoader propertiesLoader();

    @Override
    public Set<ApplicationProfile> loadProfile(ApplicationProfile parentProfile, String profileName) throws ApplicationException {
        String fileName = this.fileName(parentProfile, profileName);
        Set<URI> resources = resourceLookup.lookup(fileName);

        if (resources.isEmpty()) {
            return Collections.emptySet();
        }
        else if (resources.size() > 1) {
            throw new DuplicateProfileDefinitionException(profileName);
        }

        Map<String, ProfileProperty> propertiesByName = new HashBiMap<>();
        for(URI uri : resources) {
            Set<ProfileProperty> properties = this.propertiesLoader().loadProperties(uri);
            for(ProfileProperty property : properties) {
                if (propertiesByName.containsKey(property.name())) {
                    throw new ApplicationException("Duplicate property found: " + property.name());
                }
                propertiesByName.put(property.name(), property);
            }
        }

        Set<ProfilePropertyRegistry> inheritedRegistries = parentProfile == null ? Set.of() : Set.of(parentProfile.registry());
        SimpleProfilePropertyRegistry registry = new SimpleProfilePropertyRegistry(
                inheritedRegistries,
                Set.copyOf(propertiesByName.values())
        );

        return Set.of(new SimpleApplicationProfile(profileName, registry, parentProfile));
    }
}
