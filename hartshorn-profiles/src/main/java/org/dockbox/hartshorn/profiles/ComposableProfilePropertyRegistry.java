package org.dockbox.hartshorn.profiles;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import org.dockbox.hartshorn.util.option.Option;

public class ComposableProfilePropertyRegistry implements ProfilePropertyRegistry {

    private final Set<ProfilePropertyRegistry> registries;

    public ComposableProfilePropertyRegistry(Set<ProfilePropertyRegistry> registries) {
        this.registries = Set.copyOf(registries);
    }

    public ComposableProfilePropertyRegistry(ProfileHolder profileHolder) {
        Set<ProfilePropertyRegistry> registries = new HashSet<>();
        Queue<ApplicationProfile> profiles = new ArrayDeque<>(profileHolder.profiles());
        while(!profiles.isEmpty()) {
            ApplicationProfile profile = profiles.poll();
            profile.parent().peek(profiles::add);
            registries.add(profile.registry());
        }
        this.registries = Set.copyOf(registries);
    }

    @Override
    public Set<ProfilePropertyRegistry> inherited() {
        return this.registries;
    }

    @Override
    public List<ValueProfileProperty> properties() {
        return this.registries.stream()
                .flatMap(registry -> registry.properties().stream())
                .toList();
    }

    @Override
    public List<ValueProfileProperty> allProperties() {
        return this.registries.stream()
                .flatMap(registry -> registry.allProperties().stream())
                .toList();
    }

    @Override
    public Option<ProfileProperty> property(String name) {
        for(ProfilePropertyRegistry registry : this.registries) {
            if (registry.has(name)) {
                Option<ProfileProperty> property = registry.property(name);
                if (property.present()) {
                    return property;
                }
            }
        }
        return Option.of(new ComplexCompositeProfileProperty(name, this));
    }

    @Override
    public boolean has(String name) {
        for(ProfilePropertyRegistry registry : this.registries) {
            if (registry.has(name)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public ProfilePropertyRegistry ignoreInherited() {
        return new SimpleProfilePropertyRegistry(Set.of(), Set.of());
    }
}
