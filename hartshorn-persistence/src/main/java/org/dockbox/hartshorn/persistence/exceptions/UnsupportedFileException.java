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

package org.dockbox.hartshorn.persistence.exceptions;

import org.dockbox.hartshorn.persistence.FileFormats;

/**
 * A checked {@link Exception}, thrown if a file or {@link FileFormats} is
 * not supported by an implementation.
 */
public class UnsupportedFileException extends Exception {

    /**
     * Creates a new instance, providing a given filetype to the super type {@link Exception} as the
     * caught message.
     *
     * @param fileType
     *         The filetype providing information about the unsupported file or {@link FileFormats}
     */
    public UnsupportedFileException(final String fileType) {
        super(fileType);
    }
}
