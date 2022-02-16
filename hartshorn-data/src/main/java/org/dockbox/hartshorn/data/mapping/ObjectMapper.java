/*
 * Copyright 2019-2022 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.dockbox.hartshorn.data.mapping;

import org.dockbox.hartshorn.core.GenericType;
import org.dockbox.hartshorn.core.context.element.TypeContext;
import org.dockbox.hartshorn.core.domain.Exceptional;
import org.dockbox.hartshorn.data.FileFormat;

import java.net.URI;
import java.net.URL;
import java.nio.file.Path;
import java.util.HashMap;
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
        return Exceptional.of(() -> this.flat(uri.toURL())).or(new HashMap<>());
    }

    <T> Exceptional<Boolean> write(Path path, T content);

    <T> Exceptional<String> write(T content);

    ObjectMapper fileType(FileFormat fileFormat);

    FileFormat fileType();

    ObjectMapper skipBehavior(JsonInclusionRule modifier);

}
