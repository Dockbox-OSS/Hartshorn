/*
 * Copyright 2019-2025 the original author or authors.
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
