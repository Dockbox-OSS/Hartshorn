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

package org.dockbox.hartshorn.util.resources;

import java.io.File;
import java.net.URI;
import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

import org.dockbox.hartshorn.application.context.ApplicationContext;

/**
 * Looks up a resource through the local filesystem. The file directory is looked up based on the configuration path of
 * the path representation, typically this will be similar to {@code /config/{owner-id}/}.
 *
 * <p>This strategy does not require the name to be present, as it is the default strategy used in
 * {@link Resources#getResourceURIs(ApplicationContext, String, ResourceLookupStrategy...)}.
 */
public class FileSystemLookupStrategy implements ResourceLookupStrategy {

    public static final String NAME = "fs";

    @Override
    public String name() {
        return NAME;
    }

    @Override
    public Set<URI> lookup(final ApplicationContext context, final String path) {
        final File resolved = context.environment().fileSystem().applicationPath().resolve(path).toFile();
        if (resolved.exists()) return Collections.singleton(resolved.toURI());

        final File parent = resolved.getParentFile();
        if (parent == null) return Collections.emptySet();

        final File[] children = parent.listFiles((dir, file) -> file.startsWith(path));
        if (children != null) {
            return Arrays.stream(children).map(File::toURI).collect(Collectors.toUnmodifiableSet());
        }

        return Collections.emptySet();
    }

    @Override
    public URI baseUrl(final ApplicationContext context) {
        return context.environment().fileSystem().applicationPath().toUri();
    }
}
