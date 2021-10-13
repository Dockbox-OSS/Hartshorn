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
import org.dockbox.hartshorn.di.context.ApplicationContext;
import org.dockbox.hartshorn.di.context.element.TypeContext;
import org.dockbox.hartshorn.persistence.FileManager;
import org.dockbox.hartshorn.persistence.FileType;
import org.dockbox.hartshorn.persistence.FileTypeAttribute;

import java.net.URI;

import lombok.Getter;

/**
 * Looks up a resource through the local filesystem. The file directory is looked up based on the configuration path of
 * the {@link TypeContext owner}, typically this will be similar to {@code /config/{owner-id}/}.
 * <p>This strategy does not require the name to be present, as it is the default strategy used in
 * {@link ConfigurationServiceProcessor}.
 */
public class FileSystemLookupStrategy implements ResourceLookupStrategy {

    @Getter
    private final String name = "fs";

    @Override
    public Exceptional<URI> lookup(final ApplicationContext context, final String path, final TypeContext<?> owner, final FileType fileType) {
        return Exceptional.of(context.get(FileManager.class, FileTypeAttribute.of(fileType)).configFile(owner.type(), path).toUri());
    }
}
