/*
 * Copyright 2019-2023 the original author or authors.
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

package org.dockbox.hartshorn.config;

import org.dockbox.hartshorn.util.GenericType;
import org.dockbox.hartshorn.util.option.Attempt;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URL;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public interface ObjectMapper {

    <T> Attempt<T, ObjectMappingException> read(String content, Class<T> type);

    <T> Attempt<T, ObjectMappingException> read(Path path, Class<T> type);

    <T> Attempt<T, ObjectMappingException> read(URL url, Class<T> type);

    <T> Attempt<T, ObjectMappingException> read(InputStream stream, Class<T> type);

    default <T> Attempt<T, ObjectMappingException> read(URI uri, Class<T> type) {
        return (Attempt<T, ObjectMappingException>) Attempt.of(() -> this.read(uri.toURL(), type), IOException.class)
                .mapError(ObjectMappingException::new)
                .flatMap(object -> object);
    }

    <T> Attempt<T, ObjectMappingException> read(String content, GenericType<T> type);

    <T> Attempt<T, ObjectMappingException> read(Path path, GenericType<T> type);

    <T> Attempt<T, ObjectMappingException> read(URL url, GenericType<T> type);

    <T> Attempt<T, ObjectMappingException> read(InputStream stream, GenericType<T> type);

    default <T> Attempt<T, ObjectMappingException> read(URI uri, GenericType<T> type) {
        return (Attempt<T, ObjectMappingException>) Attempt.of(() -> this.read(uri.toURL(), type), IOException.class)
                .mapError(ObjectMappingException::new)
                .flatMap(object -> object);
    }

    <T> Attempt<T, ObjectMappingException> update(T object, String content, Class<T> type);

    <T> Attempt<T, ObjectMappingException> update(T object, Path path, Class<T> type);

    <T> Attempt<T, ObjectMappingException> update(T object, URL url, Class<T> type);

    <T> Attempt<T, ObjectMappingException> update(T object, InputStream stream, Class<T> type);

    default <T> Attempt<T, ObjectMappingException> update(T object, URI uri, Class<T> type) {
        return (Attempt<T, ObjectMappingException>) Attempt.of(() -> this.update(object, uri.toURL(), type), IOException.class)
                .mapError(ObjectMappingException::new)
                .flatMap(updatedObject -> updatedObject);
    }

    Map<String, Object> flat(String content);

    Map<String, Object> flat(Path path);

    Map<String, Object> flat(URL url);

    Map<String, Object> flat(InputStream stream);

    default Map<String, Object> flat(URI uri) {
        return Attempt.of(() -> this.flat(uri.toURL()), IOException.class).orElseGet(HashMap::new);
    }

    <T> Attempt<Boolean, ObjectMappingException> write(Path path, T content);

    <T> Attempt<Boolean, ObjectMappingException> write(OutputStream outputStream, T content);

    <T> Attempt<String, ObjectMappingException> write(T content);

    ObjectMapper fileType(FileFormat fileFormat);

    FileFormat fileType();

    ObjectMapper skipBehavior(JsonInclusionRule modifier);

}
