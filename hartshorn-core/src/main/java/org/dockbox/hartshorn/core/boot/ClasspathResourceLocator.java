package org.dockbox.hartshorn.core.boot;

import org.dockbox.hartshorn.core.domain.Exceptional;

import java.nio.file.Path;

/**
 * A classpath resource locator. This class is used to locate resources in the classpath, and make them available to
 * the application.
 */
public interface ClasspathResourceLocator {

    /**
     * Attempts to look up a resource file. If the file exists it is wrapped in a {@link Exceptional}
     * and returned. If the file does not exist or is a directory, {@link Exceptional#empty()} is
     * returned. If the requested file name is invalid, or {@code null}, a {@link Exceptional}
     * containing the appropriate exception is returned.
     *
     * @param name The name of the file to look up
     *
     * @return The resource file wrapped in a {@link Exceptional}, or an appropriate {@link Exceptional} (either none or providing the appropriate exception).
     */
    Exceptional<Path> resource(final String name);
}
