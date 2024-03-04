package org.dockbox.hartshorn.profiles;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.dockbox.hartshorn.util.option.Option;

public class SimpleProfilePropertyRegistry implements ProfilePropertyRegistry {

    private final Set<ProfilePropertyRegistry> inheritedRegistries;
    private final Set<ValueProfileProperty> properties;

    public SimpleProfilePropertyRegistry(
            Set<ProfilePropertyRegistry> inheritedRegistries,
            Set<ValueProfileProperty> properties
    ) {
        this.inheritedRegistries = inheritedRegistries;
        this.properties = properties;
    }

    @Override
    public Set<ProfilePropertyRegistry> inherited() {
        return Set.copyOf(this.inheritedRegistries);
    }

    @Override
    public List<ValueProfileProperty> properties() {
        return List.copyOf(this.properties);
    }

    @Override
    public List<ValueProfileProperty> allProperties() {
        Map<String, ValueProfileProperty> properties = new HashMap<>();
        for (ProfilePropertyRegistry registry : this.inherited()) {
            for (ValueProfileProperty property : registry.allProperties()) {
                properties.put(property.name(), property);
            }
        }
        for (ValueProfileProperty property : this.properties()) {
            properties.put(property.name(), property);
        }
        return List.copyOf(properties.values());
    }

    @Override
    public Option<ProfileProperty> property(String name) {
        for(ProfileProperty property : this.properties) {
            if(property.name().equals(name)) {
                return Option.of(property);
            }
        }
        for(ProfilePropertyRegistry inheritedRegistry : this.inheritedRegistries) {
            if (inheritedRegistry.has(name)) {
                return inheritedRegistry.property(name);
            }
        }
        return Option.of(new ComplexCompositeProfileProperty(name, this));
    }

    @Override
    public boolean has(String name) {
        for(ProfileProperty property : this.properties) {
            if(property.name().equals(name)) {
                return true;
            }
        }
        for(ProfilePropertyRegistry inheritedRegistry : this.inheritedRegistries) {
            if (inheritedRegistry.has(name)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public ProfilePropertyRegistry ignoreInherited() {
        return new SimpleProfilePropertyRegistry(Set.of(), this.properties);
    }
}
