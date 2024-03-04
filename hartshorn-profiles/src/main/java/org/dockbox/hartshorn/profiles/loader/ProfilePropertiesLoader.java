package org.dockbox.hartshorn.profiles.loader;

import java.net.URI;
import java.util.Set;
import org.dockbox.hartshorn.profiles.ValueProfileProperty;
import org.dockbox.hartshorn.util.ApplicationException;

@FunctionalInterface
public interface ProfilePropertiesLoader {

    Set<ValueProfileProperty> loadProperties(URI uri) throws ApplicationException;

}
