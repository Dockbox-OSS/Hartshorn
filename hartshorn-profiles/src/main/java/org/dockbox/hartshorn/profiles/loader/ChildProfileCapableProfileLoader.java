package org.dockbox.hartshorn.profiles.loader;

import java.util.HashSet;
import java.util.Set;

import org.dockbox.hartshorn.profiles.ApplicationProfile;
import org.dockbox.hartshorn.profiles.ProfileProperty;
import org.dockbox.hartshorn.profiles.ProfilePropertyRegistry;
import org.dockbox.hartshorn.profiles.parse.ConverterProfilePropertyParser;
import org.dockbox.hartshorn.util.ApplicationException;
import org.dockbox.hartshorn.util.introspect.convert.support.StringToArrayConverter;
import org.dockbox.hartshorn.util.option.Option;
import org.jetbrains.annotations.NotNull;

public abstract class ChildProfileCapableProfileLoader implements ApplicationProfileLoader {

    private final ApplicationProfile parent;

    private String activeProfilesPropertyName = "hartshorn.profiles.active";

    public ChildProfileCapableProfileLoader() {
        this(null);
    }

    public ChildProfileCapableProfileLoader(ApplicationProfile parent) {
        this.parent = parent;
    }

    public ApplicationProfile parent() {
        return parent;
    }

    public String activeProfilesPropertyName() {
        return activeProfilesPropertyName;
    }

    public void activeProfilesPropertyName(String activeProfilesPropertyName) {
        this.activeProfilesPropertyName = activeProfilesPropertyName;
    }

    @Override
    public Set<ApplicationProfile> loadProfile(ApplicationProfile parentProfile, String profileName) throws ApplicationException {
        Option<ApplicationProfile> singleProfile = loadSingleProfile(parentProfile, profileName);
        if(singleProfile.present()) {
            return resolveProfiles(singleProfile.get());
        }
        else {
            return Set.of();
        }
    }

    @NotNull
    private Set<ApplicationProfile> resolveProfiles(ApplicationProfile applicationProfile) throws ApplicationException {
        Set<ApplicationProfile> profiles = new HashSet<>();
        profiles.add(applicationProfile);

        ProfilePropertyRegistry registry = applicationProfile.registry().ignoreInherited();
        Option<ProfileProperty> property = registry.property(activeProfilesPropertyName);
        if(property.present()) {
            if(this.parent != null) {
                throw new ApplicationException("Child profiles cannot have active profiles");
            }
            else {
                StringToArrayConverter converter = new StringToArrayConverter();
                ConverterProfilePropertyParser<String[]> parser = new ConverterProfilePropertyParser<>(converter);

                String[] activeProfiles = property.get().parseValue(parser, new String[0]);
                for(String activeProfile : activeProfiles) {
                    Option<ApplicationProfile> profile = this.loadSingleProfile(applicationProfile, activeProfile);
                    if (profile.present()) {
                        ApplicationProfile childProfile = profile.get();
                        profiles.add(childProfile);
                    }
                }
            }
        }
        return profiles;
    }

    protected abstract Option<ApplicationProfile> loadSingleProfile(ApplicationProfile parentProfile, String profileName) throws ApplicationException;
}
