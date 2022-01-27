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

package org.dockbox.hartshorn.config;

import org.dockbox.hartshorn.core.context.ApplicationContext;
import org.dockbox.hartshorn.core.context.element.TypeContext;
import org.dockbox.hartshorn.core.domain.Exceptional;
import org.dockbox.hartshorn.data.FileFormats;

import java.net.URI;
import java.nio.file.Path;

import lombok.Getter;

/**
 * Looks up a resource through the classpath. The packaged resource is copied to a temporary file, created and managed
 * by {@link org.dockbox.hartshorn.core.boot.ClasspathResourceLocator#resource(String)}. This requires the strategy name to be
 * configured to be equal to {@code classpath:{resource_name}}.
 */
public class ClassPathResourceLookupStrategy implements ResourceLookupStrategy {

    @Getter
    private final String name = "classpath";

    @Override
    public Exceptional<URI> lookup(final ApplicationContext context, final String path, final TypeContext<?> owner, final FileFormats fileFormat) {
        return context.resourceLocator().resource(fileFormat.asFileName(path)).map(Path::toUri);
    }
}
