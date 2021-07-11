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

package org.dockbox.hartshorn.persistence.mapping;

import org.dockbox.hartshorn.api.domain.Exceptional;
import org.dockbox.hartshorn.di.properties.InjectableType;
import org.dockbox.hartshorn.persistence.FileType;

import java.nio.file.Path;

public interface ObjectMapper extends InjectableType {
    
    <T> Exceptional<T> read(String content, Class<T> type);
    <T> Exceptional<T> read(Path path, Class<T> type);

    <T> Exceptional<T> read(String content, GenericType<T> type);
    <T> Exceptional<T> read(Path path, GenericType<T> type);

    <T> Exceptional<Boolean> write(Path path, T content);
    <T> Exceptional<String> write(T content);

    void setFileType(FileType fileType);
    FileType getFileType();

}
