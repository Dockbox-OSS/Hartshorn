package org.dockbox.hartshorn.profiles;

import java.util.List;
import java.util.Set;
import org.dockbox.hartshorn.util.option.Option;

public interface ProfilePropertyRegistry {

    Set<ProfilePropertyRegistry> inherited();

    List<ValueProfileProperty> properties();

    List<ValueProfileProperty> allProperties();

    Option<ProfileProperty> property(String name);

    boolean has(String name);

    ProfilePropertyRegistry ignoreInherited();

}
