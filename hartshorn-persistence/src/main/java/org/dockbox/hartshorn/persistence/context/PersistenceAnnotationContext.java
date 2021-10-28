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

package org.dockbox.hartshorn.persistence.context;

import org.dockbox.hartshorn.persistence.FileType;
import org.dockbox.hartshorn.persistence.annotations.Deserialise;
import org.dockbox.hartshorn.persistence.annotations.File;
import org.dockbox.hartshorn.persistence.annotations.Serialise;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class PersistenceAnnotationContext {

    FileType filetype;
    File file;

    public PersistenceAnnotationContext(final Serialise serialise) {
        this.file = serialise.value();
        this.filetype = serialise.filetype();
    }

    public PersistenceAnnotationContext(final Deserialise deserialise) {
        this.file = deserialise.value();
        this.filetype = deserialise.filetype();
    }

}
