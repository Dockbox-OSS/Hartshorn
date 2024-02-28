package org.dockbox.hartshorn.profiles.loader;

import java.net.URI;
import java.util.Set;

import org.dockbox.hartshorn.profiles.ProfileProperty;
import org.dockbox.hartshorn.util.ApplicationException;

@FunctionalInterface
public interface ProfilePropertiesLoader {

    Set<ProfileProperty> loadProperties(URI uri) throws ApplicationException;

}
