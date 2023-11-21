package org.dockbox.hartshorn.profiles;

import java.util.Set;

import org.dockbox.hartshorn.util.option.Option;

public interface ProfilePropertyRegistry {

    Set<ProfilePropertyRegistry> inherited();

    Set<ProfileProperty> properties();

    Option<ProfileProperty> property(String name);

    boolean has(String name);

}
