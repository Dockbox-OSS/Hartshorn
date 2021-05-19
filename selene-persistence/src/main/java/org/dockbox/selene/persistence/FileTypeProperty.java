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

import org.dockbox.selene.di.binding.Bindings;
import org.dockbox.selene.di.properties.BindingMetaProperty;

import java.lang.annotation.Annotation;

public final class FileTypeProperty<T extends Annotation> extends BindingMetaProperty {

    private final FileType fileType;

    private FileTypeProperty(FileType fileType) {
        super(Bindings.named(fileType.getExtension()));
        this.fileType = fileType;
    }

    public static FileTypeProperty<?> of(FileType fileType) {
        return new FileTypeProperty<>(fileType);
    }

    public FileType getFileType() {
        return this.fileType;
    }
}
