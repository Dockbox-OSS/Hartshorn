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

package org.dockbox.hartshorn.data.mapping;

import org.dockbox.hartshorn.core.GenericType;
import org.dockbox.hartshorn.core.HartshornUtils;
import org.dockbox.hartshorn.core.context.element.TypeContext;
import org.dockbox.hartshorn.core.domain.Exceptional;
import org.dockbox.hartshorn.data.FileFormat;

import java.net.URI;
import java.net.URL;
import java.nio.file.Path;
import java.util.Map;

public interface ObjectMapper {

    default <T> Exceptional<T> read(final String content, final TypeContext<T> type) {
        return this.read(content, type.type());
    }

    default <T> Exceptional<T> read(final Path path, final TypeContext<T> type) {
        return this.read(path, type.type());
    }

    default <T> Exceptional<T> read(final URI uri, final TypeContext<T> type) {
        return this.read(uri, type.type());
    }

    default  <T> Exceptional<T> read(final URL url, final TypeContext<T> type) {
        return this.read(url, type.type());
    }

    <T> Exceptional<T> read(String content, Class<T> type);

    <T> Exceptional<T> read(Path path, Class<T> type);

    <T> Exceptional<T> read(URL url, Class<T> type);

    default <T> Exceptional<T> read(final URI uri, final Class<T> type) {
        return Exceptional.of(() -> this.read(uri.toURL(), type).orNull());
    }

    <T> Exceptional<T> read(String content, GenericType<T> type);

    <T> Exceptional<T> read(Path path, GenericType<T> type);

    <T> Exceptional<T> read(URL url, GenericType<T> type);

    default <T> Exceptional<T> read(final URI uri, final GenericType<T> type) {
        return Exceptional.of(() -> this.read(uri.toURL(), type).orNull());
    }

    Map<String, Object> flat(String content);

    Map<String, Object> flat(Path path);

    Map<String, Object> flat(URL url);

    default Map<String, Object> flat(final URI uri) {
        return Exceptional.of(() -> this.flat(uri.toURL())).or(HartshornUtils.emptyMap());
    }

    <T> Exceptional<Boolean> write(Path path, T content);

    <T> Exceptional<String> write(T content);

    ObjectMapper fileType(FileFormat fileFormat);

    FileFormat fileType();

    ObjectMapper skipBehavior(JsonInclusionRule modifier);

}
