package org.dockbox.hartshorn.profiles.loader;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.dockbox.hartshorn.profiles.ApplicationProfile;
import org.dockbox.hartshorn.profiles.ProfileProperty;
import org.dockbox.hartshorn.profiles.ProfilePropertyRegistry;
import org.dockbox.hartshorn.profiles.ValueProfileProperty;
import org.dockbox.hartshorn.profiles.parse.support.ListProfilePropertyParser;
import org.dockbox.hartshorn.profiles.parse.ProfilePropertyParser;
import org.dockbox.hartshorn.util.ApplicationException;
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
        return this.parent;
    }

    public String activeProfilesPropertyName() {
        return this.activeProfilesPropertyName;
    }

    public void activeProfilesPropertyName(String activeProfilesPropertyName) {
        this.activeProfilesPropertyName = activeProfilesPropertyName;
    }

    @Override
    public Set<ApplicationProfile> loadProfile(ApplicationProfile parentProfile, String profileName) throws ApplicationException {
        Option<ApplicationProfile> singleProfile = this.loadSingleProfile(parentProfile, profileName);
        if(singleProfile.present()) {
            return this.resolveProfiles(singleProfile.get());
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
        Option<ProfileProperty> property = registry.property(this.activeProfilesPropertyName);
        if(property.present()) {
            if(this.parent != null) {
                throw new ApplicationException("Child profiles cannot have active profiles");
            }
            else {
                ProfilePropertyParser<List<ProfileProperty>> parser = new ListProfilePropertyParser();
                List<ProfileProperty> activeProfiles = property.get().parseValue(parser, List.of());

                for(ProfileProperty activeProfile : activeProfiles) {
                    if (!(activeProfile instanceof ValueProfileProperty valueProfileProperty)) {
                        throw new ApplicationException("Active profiles must be value properties");
                    }

                    Option<String> profileName = valueProfileProperty.rawValue();
                    if (profileName.absent()) {
                        throw new ApplicationException("Active profiles must have a raw value");
                    }

                    Option<ApplicationProfile> profile = this.loadSingleProfile(applicationProfile, profileName.get());
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
