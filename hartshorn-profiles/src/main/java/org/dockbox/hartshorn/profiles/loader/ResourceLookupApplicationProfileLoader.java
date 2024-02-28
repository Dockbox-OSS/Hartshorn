package org.dockbox.hartshorn.profiles.loader;

import java.net.URI;
import java.util.Map;
import java.util.Set;

import org.dockbox.hartshorn.profiles.ApplicationProfile;
import org.dockbox.hartshorn.profiles.ProfileProperty;
import org.dockbox.hartshorn.profiles.ProfilePropertyRegistry;
import org.dockbox.hartshorn.profiles.SimpleApplicationProfile;
import org.dockbox.hartshorn.profiles.SimpleProfilePropertyRegistry;
import org.dockbox.hartshorn.util.ApplicationException;
import org.dockbox.hartshorn.util.collections.HashBiMap;
import org.dockbox.hartshorn.util.option.Option;
import org.dockbox.hartshorn.util.resources.ResourceLookup;

public abstract class ResourceLookupApplicationProfileLoader extends ChildProfileCapableProfileLoader {

    private final ResourceLookup resourceLookup;
    private final String profileName;

    protected ResourceLookupApplicationProfileLoader(
            ResourceLookup resourceLookup,
            String profileName
    ) {
        this.resourceLookup = resourceLookup;
        this.profileName = profileName;
    }

    protected ResourceLookupApplicationProfileLoader(
            ResourceLookup resourceLookup,
            ApplicationProfile parent,
            String profileName
    ) {
        super(parent);
        this.resourceLookup = resourceLookup;
        this.profileName = profileName;
    }

    public ResourceLookup resourceLookup() {
        return resourceLookup;
    }

    public String profileName() {
        return profileName;
    }

    protected abstract String fileName(String profileName);

    protected abstract ProfilePropertiesLoader propertiesLoader();

    @Override
    public Option<ApplicationProfile> loadSingleProfile() throws ApplicationException {
        String fileName = this.fileName(this.profileName());
        Set<URI> resources = resourceLookup.lookup(fileName);

        if (resources.isEmpty()) {
            return Option.empty();
        }
        else if (resources.size() > 1) {
            throw new DuplicateProfileDefinitionException(this.profileName());
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

        Set<ProfilePropertyRegistry> inheritedRegistries = this.parent() == null ? Set.of() : Set.of(this.parent().registry());
        SimpleProfilePropertyRegistry registry = new SimpleProfilePropertyRegistry(
                inheritedRegistries,
                Set.copyOf(propertiesByName.values())
        );

        return Option.of(new SimpleApplicationProfile(this.profileName(), registry, this.parent()));
    }
}
