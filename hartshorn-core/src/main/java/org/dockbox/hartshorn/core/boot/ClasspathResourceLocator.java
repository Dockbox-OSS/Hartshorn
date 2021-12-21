/*
 * Copyright (C) 2020 Guus Lieben
 *
 * This framework is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 2.1 of the
 * License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 * the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library. If not, see {@literal<http://www.gnu.org/licenses/>}.
 */

package org.dockbox.hartshorn.core.boot;

import org.dockbox.hartshorn.core.domain.Exceptional;

import java.nio.file.Path;

/**
 * A classpath resource locator. This class is used to locate resources in the classpath, and make them available to
 * the application.
 *
 * @author Guus Lieben
 * @since 4.2.5
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
