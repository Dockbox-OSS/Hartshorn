package org.dockbox.hartshorn.profiles.loader;

import java.util.Set;

import org.dockbox.hartshorn.profiles.ApplicationProfile;
import org.dockbox.hartshorn.util.ApplicationException;

@FunctionalInterface
public interface ApplicationProfileLoader {

    default Set<ApplicationProfile> loadProfile(String rootProfileName) throws ApplicationException {
        return this.loadProfile(null, rootProfileName);
    }

    Set<ApplicationProfile> loadProfile(ApplicationProfile parentProfile, String profileName) throws ApplicationException;

}
