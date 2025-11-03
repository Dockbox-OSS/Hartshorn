package org.dockbox.hartshorn.profiles.support;

import org.dockbox.hartshorn.util.ApplicationRuntimeException;

import java.net.URI;

/**
 * Exception thrown when a profile could not be loaded from a given resource. This exception typically indicates
 * issues such as missing files, inaccessible resources, or parsing errors during profile loading.
 *
 * @since 0.7.0
 *
 * @author Guus Lieben
 */
public class ProfileLoadingFailedException extends ApplicationRuntimeException {
    public ProfileLoadingFailedException(String profile, URI resource, Throwable cause) {
        super("Profile with name '" + profile + "' could not be loaded from resource '" + resource + "'", cause);
    }
}
