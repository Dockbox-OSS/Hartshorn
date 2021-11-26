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

package org.dockbox.hartshorn.data.context;

import org.dockbox.hartshorn.data.FileFormats;
import org.dockbox.hartshorn.data.annotations.Deserialise;
import org.dockbox.hartshorn.data.annotations.File;
import org.dockbox.hartshorn.data.annotations.Serialise;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class PersistenceAnnotationContext {

    FileFormats filetype;
    File file;

    public PersistenceAnnotationContext(final Serialise serialise) {
        this.file = serialise.path();
        this.filetype = serialise.filetype();
    }

    public PersistenceAnnotationContext(final Deserialise deserialise) {
        this.file = deserialise.path();
        this.filetype = deserialise.filetype();
    }

}
