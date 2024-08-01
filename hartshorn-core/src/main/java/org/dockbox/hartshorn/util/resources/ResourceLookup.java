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

package org.dockbox.hartshorn.util.resources;

import java.net.URI;
import java.util.Set;

/**
 * A resource lookup is responsible for finding resources based on a given path. This may be a file, a classpath
 * resource or any other type of resource.
 *
 * @since 0.4.1
 *
 * @author Guus Lieben
 */
@FunctionalInterface
public interface ResourceLookup {

    /**
     * Looks up all resources that match the given path. The path may be a file path, a classpath resource path or any
     * other type of path.
     *
     * @param path the path to the resource
     * @return a set of {@link URI URIs} pointing to the resource
     */
    Set<URI> lookup(String path);
}
