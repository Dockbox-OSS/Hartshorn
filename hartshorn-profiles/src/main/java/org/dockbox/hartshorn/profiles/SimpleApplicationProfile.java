package org.dockbox.hartshorn.profiles;

import org.dockbox.hartshorn.util.option.Option;

public final class SimpleApplicationProfile implements ApplicationProfile {

    private final String name;
    private final ProfilePropertyRegistry registry;
    private final ApplicationProfile parent;

    public SimpleApplicationProfile(
            String name,
            ProfilePropertyRegistry registry,
            ApplicationProfile parent
    ) {
        this.name = name;
        this.registry = registry;
        this.parent = parent;
    }

    @Override
    public String name() {
        return this.name;
    }

    @Override
    public ProfilePropertyRegistry registry() {
        return this.registry;
    }

    @Override
    public Option<ApplicationProfile> parent() {
        return Option.of(this.parent);
    }
}
