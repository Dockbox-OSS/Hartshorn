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

/**
 * A handler for {@link ClassPathResource}s. Implementations of this interface are provided to
 * {@link ClassPathScanner}s, which will invoke the {@link #handle(ClassPathResource)} method for each resource
 * that is found on the classpath.
 *
 * @see ClassPathScanner
 * @see ClassPathResource
 *
 * @since 0.4.13
 *
 * @author Guus Lieben
 */
@FunctionalInterface
public interface ResourceHandler {

    /**
     * Handles the provided {@link ClassPathResource}. Implementations of this method are expected to process the
     * resource in a way that is appropriate for the implementation.
     *
     * @param resource The resource to handle, never {@code null}.
     */
    void handle(ClassPathResource resource);
}
