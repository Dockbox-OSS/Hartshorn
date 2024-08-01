/*
 * Copyright 2019-2023 the original author or authors.
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

package org.dockbox.hartshorn.util.introspect.scan.classpath;

import java.nio.file.Path;

/**
 * Represents a resource on the classpath. Resources are provided by {@link ClassPathScanner}s and handed
 * over to {@link ResourceHandler}s for further processing.
 *
 * @see ClassPathScanner
 * @see ResourceHandler
 *
 * @since 0.4.13
 *
 * @author Guus Lieben
 */
public interface ClassPathResource {

    /**
     * The classloader from which this resource was, or should be, loaded.
     *
     * @return The classloader, never {@code null}.
     */
    ClassLoader classLoader();

    /**
     * The path to the resource. This is the path as it is found on the classpath, and may be inside
     * a directory or archive.
     *
     * @return The path, never {@code null}.
     */
    Path path();

    /**
     * The name of the resource. For classes this is the fully qualified class name, for other
     * resources this is the path to the resource.
     * @return The name, never {@code null}.
     */
    String resourceName();

    /**
     * Whether this resource is a class. If {@code true}, the resource can be loaded as a class.
     *
     * @return {@code true} if this resource is a class, {@code false} otherwise.
     */
    boolean isClassResource();
}
