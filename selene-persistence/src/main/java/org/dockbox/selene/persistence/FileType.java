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

package org.dockbox.selene.persistence;

import org.dockbox.selene.api.domain.FileTypes;
import org.dockbox.selene.di.annotations.BindingMeta;

import java.lang.annotation.Annotation;
import java.nio.file.Path;

import lombok.AllArgsConstructor;
import lombok.Getter;

/** Enumerated values containing the file extensions for several commonly used file types. */
@AllArgsConstructor
public enum FileType {
    // Compiled Java formats
    CLASS(FileTypes.CLASS),
    JAR(FileTypes.JAR),

    // Formats with file/web utilities
    SQLITE(FileTypes.SQLITE),
    YAML(FileTypes.YAML),
    JSON(FileTypes.JSON),
    XML(FileTypes.XML),
    MOD_CONFIG(FileTypes.MOD_CONFIG),
    CONFIG(FileTypes.CONFIG)
    ;

    @Getter
    private final String extension;
    private final Class<? extends Annotation> format;

    FileType(String extension) {
        this.extension = extension;
        this.format = null;
    }

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

    public Class<? extends Annotation> getFormat() {
        return BindingMeta.class;
    }
}
