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

import org.dockbox.hartshorn.data.FileFormat;
import org.dockbox.hartshorn.util.GenericType;
import org.dockbox.hartshorn.util.Result;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URL;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public interface ObjectMapper {

    <T> Result<T> read(String content, Class<T> type);

    <T> Result<T> read(Path path, Class<T> type);

    <T> Result<T> read(URL url, Class<T> type);

    <T> Result<T> read(InputStream stream, Class<T> type);

    default <T> Result<T> read(final URI uri, final Class<T> type) {
        return Result.of(() -> this.read(uri.toURL(), type).orNull());
    }

    <T> Result<T> read(String content, GenericType<T> type);

    <T> Result<T> read(Path path, GenericType<T> type);

    <T> Result<T> read(URL url, GenericType<T> type);

    <T> Result<T> read(InputStream stream, GenericType<T> type);

    default <T> Result<T> read(final URI uri, final GenericType<T> type) {
        return Result.of(() -> this.read(uri.toURL(), type).orNull());
    }

    <T> Result<T> update(T object, String content, Class<T> type);

    <T> Result<T> update(T object, Path path, Class<T> type);

    <T> Result<T> update(T object, URL url, Class<T> type);

    <T> Result<T> update(T object, InputStream stream, Class<T> type);

    default <T> Result<T> update(final T object, final URI uri, final Class<T> type) {
        return Result.of(() -> this.update(object, uri.toURL(), type).orNull());
    }

    Map<String, Object> flat(String content);

    Map<String, Object> flat(Path path);

    Map<String, Object> flat(URL url);

    Map<String, Object> flat(InputStream stream);

    default Map<String, Object> flat(final URI uri) {
        return Result.of(() -> this.flat(uri.toURL())).or(new HashMap<>());
    }

    <T> Result<Boolean> write(Path path, T content);

    <T> Result<Boolean> write(OutputStream outputStream, T content);

    <T> Result<String> write(T content);

    ObjectMapper fileType(FileFormat fileFormat);

    FileFormat fileType();

    ObjectMapper skipBehavior(JsonInclusionRule modifier);

}
