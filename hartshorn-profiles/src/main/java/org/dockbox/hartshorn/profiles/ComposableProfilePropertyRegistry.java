package org.dockbox.hartshorn.profiles;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

import org.dockbox.hartshorn.util.option.Option;

public class ComposableProfilePropertyRegistry implements ProfilePropertyRegistry {

    private final List<ProfilePropertyRegistry> registries;

    public ComposableProfilePropertyRegistry(List<ProfilePropertyRegistry> registries) {
        this.registries = List.copyOf(registries);
    }

    public ComposableProfilePropertyRegistry(ProfileHolder profileHolder) {
        List<ProfilePropertyRegistry> registries = new ArrayList<>();
        Queue<ApplicationProfile> profiles = new ArrayDeque<>(profileHolder.profiles());
        while(!profiles.isEmpty()) {
            ApplicationProfile profile = profiles.poll();
            profile.parent().peek(profiles::add);
            registries.add(profile.registry());
        }
        this.registries = List.copyOf(registries);
    }

    @Override
    public List<ProfilePropertyRegistry> inherited() {
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
        return new SimpleProfilePropertyRegistry(List.of(), List.of());
    }
}
