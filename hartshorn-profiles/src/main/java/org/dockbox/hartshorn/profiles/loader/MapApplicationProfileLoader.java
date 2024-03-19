package org.dockbox.hartshorn.profiles.loader;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.dockbox.hartshorn.profiles.ApplicationProfile;
import org.dockbox.hartshorn.profiles.ProfilePropertyRegistry;
import org.dockbox.hartshorn.profiles.SimpleApplicationProfile;
import org.dockbox.hartshorn.profiles.SimpleProfileProperty;
import org.dockbox.hartshorn.profiles.SimpleProfilePropertyRegistry;
import org.dockbox.hartshorn.profiles.ValueProfileProperty;

public class MapApplicationProfileLoader implements ApplicationProfileLoader {

    private final Map<String, String> properties;

    public MapApplicationProfileLoader(Map<String, String> properties) {
        this.properties = properties;
    }

    @Override
    public Set<ApplicationProfile> loadProfile(ApplicationProfile parentProfile, String profileName) {
        List<ValueProfileProperty> profileProperties = new ArrayList<>();
        for(Entry<String, String> entry : properties.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            profileProperties.add(new SimpleProfileProperty(key, value));
        }
        ProfilePropertyRegistry registry = new SimpleProfilePropertyRegistry(
                parentProfile != null ? List.of(parentProfile.registry()) : List.of(),
                profileProperties
        );
        return Set.of(new SimpleApplicationProfile(profileName, registry, parentProfile));
    }
}
