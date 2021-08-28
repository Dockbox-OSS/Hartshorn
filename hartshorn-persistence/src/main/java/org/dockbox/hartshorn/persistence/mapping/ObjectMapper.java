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
import org.dockbox.hartshorn.di.GenericType;
import org.dockbox.hartshorn.di.context.element.TypeContext;
import org.dockbox.hartshorn.di.properties.AttributeHolder;
import org.dockbox.hartshorn.persistence.FileType;

import java.net.URL;
import java.nio.file.Path;

public interface ObjectMapper extends AttributeHolder {

    default <T> Exceptional<T> read(final String content, final TypeContext<T> type) {
        return this.read(content, type.type());
    }

    default  <T> Exceptional<T> read(final Path path, final TypeContext<T> type) {
        return this.read(path, type.type());
    }

    default  <T> Exceptional<T> read(final URL url, final TypeContext<T> type) {
        return this.read(url, type.type());
    }

    <T> Exceptional<T> read(String content, Class<T> type);

    <T> Exceptional<T> read(Path path, Class<T> type);

    <T> Exceptional<T> read(URL url, Class<T> type);

    <T> Exceptional<T> read(String content, GenericType<T> type);

    <T> Exceptional<T> read(Path path, GenericType<T> type);

    <T> Exceptional<T> read(URL url, GenericType<T> type);

    <T> Exceptional<Boolean> write(Path path, T content);

    <T> Exceptional<String> write(T content);

    ObjectMapper fileType(FileType fileType);

    FileType fileType();

}
