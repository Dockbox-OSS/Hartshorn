package org.dockbox.hartshorn.profiles.loader;

import java.util.Set;

import org.dockbox.hartshorn.profiles.ApplicationProfile;
import org.dockbox.hartshorn.util.ApplicationException;

@FunctionalInterface
public interface ApplicationProfileLoader {

    Set<ApplicationProfile> loadProfiles() throws ApplicationException;

}
