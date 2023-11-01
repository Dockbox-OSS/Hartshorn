package org.dockbox.hartshorn.profiles;

import java.util.Set;

import org.dockbox.hartshorn.util.option.Option;

public class SimpleProfilePropertyRegistry implements ProfilePropertyRegistry {

    private final ApplicationProfile applicationProfile;
    private final Set<ProfilePropertyRegistry> inheritedRegistries;
    private final Set<ProfileProperty> properties;

    public SimpleProfilePropertyRegistry(
            ApplicationProfile applicationProfile,
            Set<ProfilePropertyRegistry> inheritedRegistries,
            Set<ProfileProperty> properties
    ) {
        this.applicationProfile = applicationProfile;
        this.inheritedRegistries = inheritedRegistries;
        this.properties = properties;
    }

    @Override
    public ApplicationProfile profile() {
        return this.applicationProfile;
    }

    @Override
    public Set<ProfilePropertyRegistry> inherited() {
        return Set.copyOf(inheritedRegistries);
    }

    @Override
    public Set<ProfileProperty> properties() {
        return Set.copyOf(properties);
    }

    @Override
    public Option<ProfileProperty> property(String name) {
        for(ProfileProperty property : properties) {
            if(property.name().equals(name)) {
                return Option.of(property);
            }
        }
        for(ProfilePropertyRegistry inheritedRegistry : inheritedRegistries) {
            if (inheritedRegistry.has(name)) {
                return inheritedRegistry.property(name);
            }
        }
        return Option.empty();
    }

    @Override
    public boolean has(String name) {
        for(ProfileProperty property : properties) {
            if(property.name().equals(name)) {
                return true;
            }
        }
        for(ProfilePropertyRegistry inheritedRegistry : inheritedRegistries) {
            if (inheritedRegistry.has(name)) {
                return true;
            }
        }
        return false;
    }
}
