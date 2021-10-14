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

package org.dockbox.hartshorn.config;

import org.dockbox.hartshorn.api.domain.Exceptional;
import org.dockbox.hartshorn.boot.Hartshorn;
import org.dockbox.hartshorn.di.context.ApplicationContext;
import org.dockbox.hartshorn.di.context.element.TypeContext;
import org.dockbox.hartshorn.persistence.FileType;

import java.net.URI;
import java.nio.file.Path;

import lombok.Getter;

/**
 * Looks up a resource through the classpath. The packaged resource is copied to a temporary file, created and managed
 * by {@link Hartshorn#resource(String)}. This requires the strategy name to be configured to be equal to
 * {@code classpath:{resource_name}}.
 */
public class ClassPathResourceLookupStrategy implements ResourceLookupStrategy {

    @Getter
    private final String name = "classpath";

    @Override
    public Exceptional<URI> lookup(final ApplicationContext context, final String path, final TypeContext<?> owner, final FileType fileType) {
        return Hartshorn.resource(fileType.asFileName(path)).map(Path::toUri);
    }
}
