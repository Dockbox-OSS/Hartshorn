package org.dockbox.hartshorn.profiles;

import java.util.List;

import org.dockbox.hartshorn.util.option.Option;

public interface ProfilePropertyRegistry {

    List<ProfilePropertyRegistry> inherited();

    List<ValueProfileProperty> properties();

    List<ValueProfileProperty> allProperties();

    Option<ProfileProperty> property(String name);

    boolean has(String name);

    ProfilePropertyRegistry ignoreInherited();

}
