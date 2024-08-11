/*
 * Copyright 2019-2024 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.dockbox.hartshorn.launchpad.environment;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;
import java.util.Set;

import org.dockbox.hartshorn.util.option.Option;

/**
 * A classpath resource locator. This class is used to locate resources in the classpath, and make them available to
 * the application.
 *
 * @since 0.4.9
 *
 * @author Guus Lieben
 */
public interface ClasspathResourceLocator {

    /**
     * Attempts to look up a resource file. If the file exists it is wrapped in a {@link Option}
     * and returned. If the file does not exist or is a directory, {@link Option#empty()} is
     * returned.
     *
     * @param name The name of the file to look up
     *
     * @return The resource file wrapped in a {@link Option} if present, otherwise {@link Option#empty()}
     *
     * @throws IOException if an I/O error occurs
     */
    Option<Path> resource(String name) throws IOException;

    /**
     * Attempts to look up all resources with the given name. If no resources are found or an I/O
     * error occurs, an empty set is returned.
     *
     * @param name The name of the resources to look up
     * @return A set of all resources with the given name
     */
    Set<Path> resources(String name);

    /**
     * Returns a URI pointing to the root of the classpath. This URI can be used to access resources
     * in the classpath. It is not ensured that the URI is a valid location if the classpath is
     * not a file system or contains multiple locations.
     *
     * @return A URI pointing to the root of the classpath
     */
    URI classpathUri();
}
