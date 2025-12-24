package org.dockbox.hartshorn.properties.loader;

import java.util.Set;

/**
 * {@link PropertyRegistryPathLoader} that loads properties from a {@link java.io.File} or
 * equivalent type which represents a file on the filesystem, and is therefore aware of
 * supported file extensions.
 *
 * @since 0.7.0
 *
 * @author Guus Lieben
 */
public interface FilePropertyRegistryLoader extends PropertyRegistryPathLoader {

    /**
     * Returns a set of file extensions that this loader supports. The file extension of the file
     * that is being loaded must be one of the extensions in this set.
     *
     * @return a set of file extensions that this loader supports
     */
    Set<String> supportedExtensions();
}
