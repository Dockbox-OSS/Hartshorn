package org.dockbox.hartshorn.profiles;

import java.util.Set;

import org.dockbox.hartshorn.util.option.Option;

public class SimpleProfilePropertyRegistry implements ProfilePropertyRegistry {

    private final Set<ProfilePropertyRegistry> inheritedRegistries;
    private final Set<ProfileProperty> properties;

    public SimpleProfilePropertyRegistry(
            Set<ProfilePropertyRegistry> inheritedRegistries,
            Set<ProfileProperty> properties
    ) {
        this.inheritedRegistries = inheritedRegistries;
        this.properties = properties;
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
