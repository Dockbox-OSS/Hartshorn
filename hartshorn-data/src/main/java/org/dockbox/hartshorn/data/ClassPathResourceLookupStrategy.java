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

package org.dockbox.hartshorn.data;

import org.dockbox.hartshorn.core.context.ApplicationContext;

import java.net.URI;
import java.nio.file.Path;
import java.util.Set;
import java.util.stream.Collectors;

import lombok.Getter;

/**
 * Looks up a resource through the classpath. The packaged resource is copied to a temporary file, created and managed
 * by {@link org.dockbox.hartshorn.core.boot.ClasspathResourceLocator#resources(String)}. This requires the strategy name to be
 * configured to be equal to {@code classpath:{resource_name}}.
 */
public class ClassPathResourceLookupStrategy implements ResourceLookupStrategy {

    @Getter
    private final String name = "classpath";

    @Override
    public Set<URI> lookup(final ApplicationContext context, final String path) {
        return context.resourceLocator().resources(path).stream().map(Path::toUri).collect(Collectors.toSet());
    }

    @Override
    public URI baseUrl(final ApplicationContext context) {
        return context.resourceLocator().classpathUri();
    }
}
