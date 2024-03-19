package org.dockbox.hartshorn.profiles.loader;

import java.util.Comparator;
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

        if(profiles.isEmpty()) {
            return Option.empty();
        }
        else if(profiles.size() == 1) {
            return Option.of(profiles.iterator().next());
        }
        else {
            List<ProfilePropertyRegistry> registries = profiles.stream()
                    .sorted(Comparator.comparingInt(ApplicationProfile::priority))
                    .map(ApplicationProfile::registry)
                    .collect(Collectors.toList());
            ComposableProfilePropertyRegistry propertyRegistry = new ComposableProfilePropertyRegistry(registries);
            return Option.of(new SimpleApplicationProfile(profileName, propertyRegistry, parentProfile));
        }
    }
}
