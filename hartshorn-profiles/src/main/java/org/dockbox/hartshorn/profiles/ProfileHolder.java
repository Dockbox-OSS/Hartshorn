package org.dockbox.hartshorn.profiles;

import java.util.Set;

import org.dockbox.hartshorn.util.option.Option;

public interface ProfileHolder {

    Set<ApplicationProfile> activeProfiles();

    Set<ApplicationProfile> profiles();

    Option<ApplicationProfile> profile(String name);

    boolean has(String name);

}
