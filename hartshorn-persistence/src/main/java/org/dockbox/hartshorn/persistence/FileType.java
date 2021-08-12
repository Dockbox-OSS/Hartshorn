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

package org.dockbox.hartshorn.persistence;

import org.dockbox.hartshorn.api.domain.FileTypes;

import java.lang.annotation.Annotation;
import java.nio.file.Path;

import javax.inject.Named;

import lombok.AllArgsConstructor;
import lombok.Getter;

/** Enumerated values containing the file extensions for several commonly used file types. */
@AllArgsConstructor
public enum FileType {
    // Database types
    SQLITE(FileTypes.SQLITE, PersistenceType.DATABASE),

    // Raw/text types
    YAML(FileTypes.YAML, PersistenceType.RAW),
    JSON(FileTypes.JSON, PersistenceType.RAW),
    XML(FileTypes.XML, PersistenceType.RAW),
    TOML(FileTypes.TOML, PersistenceType.RAW),
    PROPERTIES(FileTypes.PROPERTIES, PersistenceType.RAW);

    @Getter private final String extension;
    @Getter private final PersistenceType type;

    /**
     * Converts a given filename (without the file extension present), combined with a {@link Path}
     * reference to a directory, to a new {@link Path} reference to a file. If the file did not yet
     * exist, it is created.
     *
     * <p>Assumes the parent {@link Path} already exists.
     *
     * @param parent
     *         The parent directory
     * @param file
     *         The filename without a file extension present
     *
     * @return The {@link Path} reference to a file
     */
    public Path asPath(Path parent, String file) {
        return parent.resolve(this.asFileName(file));
    }

    /**
     * Converts a given filename (without the file extension present) to a {@link String} value
     * holding the correct format.
     *
     * @param file
     *         The filename without a file extension present
     *
     * @return The generated filename with extension
     */
    public String asFileName(String file) {
        return file + '.' + this.extension;
    }

    public Class<? extends Annotation> format() {
        return Named.class;
    }
}
