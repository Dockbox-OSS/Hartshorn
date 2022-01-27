/*
 * Copyright 2019-2022 the original author or authors.
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

package org.dockbox.hartshorn.core.boot;

import org.dockbox.hartshorn.core.domain.Exceptional;

import java.nio.file.Path;

/**
 * A classpath resource locator. This class is used to locate resources in the classpath, and make them available to
 * the application.
 *
 * @author Guus Lieben
 * @since 22.1
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
