package org.dockbox.hartshorn.profiles;

import java.util.Set;

public class SimpleComposableProfileHolder extends SimpleProfileHolder implements ComposableProfileHolder {

    public SimpleComposableProfileHolder(Set<ApplicationProfile> profiles) {
        super(profiles);
    }

    @Override
    public ProfilePropertyRegistry registry() {
        return new ComposableProfilePropertyRegistry(this);
    }
}
