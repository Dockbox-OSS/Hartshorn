package org.dockbox.hartshorn.profiles.loader;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.dockbox.hartshorn.profiles.ApplicationProfile;
import org.dockbox.hartshorn.profiles.ComposableProfilePropertyRegistry;
import org.dockbox.hartshorn.profiles.ProfilePropertyRegistry;
import org.dockbox.hartshorn.profiles.SimpleApplicationProfile;
import org.dockbox.hartshorn.util.ApplicationException;
import org.dockbox.hartshorn.util.option.Option;

public final class CompositeProfileLoader extends ChildProfileCapableProfileLoader {

    private final List<ApplicationProfileLoader> loaders;

    public CompositeProfileLoader(List<ApplicationProfileLoader> loaders) {
        this.loaders = loaders;
    }

    public List<ApplicationProfileLoader> loaders() {
        return this.loaders;
    }

    @Override
    protected Option<ApplicationProfile> loadSingleProfile(ApplicationProfile parentProfile, String profileName) throws ApplicationException {
        Set<ApplicationProfile> profiles = new HashSet<>();
        for(ApplicationProfileLoader profileLoader : this.loaders) {
            Set<ApplicationProfile> applicationProfiles = profileLoader.loadProfile(parentProfile, profileName);
            profiles.addAll(applicationProfiles);
        }

        // TODO: This now does not respect priorities of profiles, but it should
        if(profiles.isEmpty()) {
            return Option.empty();
        }
        else if(profiles.size() == 1) {
            return Option.of(profiles.iterator().next());
        }
        else {
            Set<ProfilePropertyRegistry> registries = profiles.stream()
                    .map(ApplicationProfile::registry)
                    .collect(Collectors.toSet());
            ComposableProfilePropertyRegistry propertyRegistry = new ComposableProfilePropertyRegistry(registries);
            return Option.of(new SimpleApplicationProfile(profileName, propertyRegistry, parentProfile));
        }
    }
}
