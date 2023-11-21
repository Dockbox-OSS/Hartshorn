package org.dockbox.hartshorn.profiles.loader;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.dockbox.hartshorn.profiles.ApplicationProfile;
import org.dockbox.hartshorn.util.ApplicationException;

public class CompositeProfileLoader implements ApplicationProfileLoader {

    private final Set<ApplicationProfileLoader> loaders;

    public CompositeProfileLoader(Set<ApplicationProfileLoader> loaders) {
        this.loaders = loaders;
    }

    @Override
    public Set<ApplicationProfile> loadProfiles() throws ApplicationException {
        Map<String, ApplicationProfile> profiles = new HashMap<>();
        for (ApplicationProfileLoader loader : this.loaders) {
            Set<ApplicationProfile> applicationProfiles = loader.loadProfiles();
            for (ApplicationProfile profile : applicationProfiles) {
                if (profiles.put(profile.name(), profile) != null) {
                    throw new DuplicateProfileDefinitionException(profile.name());
                }
            }
        }
        return Set.copyOf(profiles.values());
    }
}
