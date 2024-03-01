package org.dockbox.hartshorn.profiles.loader;

import org.dockbox.hartshorn.profiles.ApplicationProfile;

@FunctionalInterface
public interface ApplicationProfileLoaderFactory {

    ApplicationProfileLoader createLoader(String profileName, ApplicationProfile parentProfile);

}
