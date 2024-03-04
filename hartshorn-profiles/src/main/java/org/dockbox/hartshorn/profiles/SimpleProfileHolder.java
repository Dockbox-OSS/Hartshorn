package org.dockbox.hartshorn.profiles;

import java.util.Set;

import org.dockbox.hartshorn.util.option.Option;

public class SimpleProfileHolder implements ProfileHolder {

    private final Set<ApplicationProfile> profiles;

    public SimpleProfileHolder(Set<ApplicationProfile> profiles) {
        this.profiles = Set.copyOf(profiles);
    }

    @Override
    public Option<ApplicationProfile> profile(String name) {
        for(ApplicationProfile profile : this.profiles) {
            if(profile.name().equals(name)) {
                return Option.of(profile);
            }
        }
        return Option.empty();
    }

    @Override
    public Set<ApplicationProfile> profiles() {
        return this.profiles;
    }
}
