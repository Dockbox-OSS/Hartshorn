/*
 * Copyright 2019-2024 the original author or authors.
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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URL;
import java.nio.file.Path;
import java.util.Map;

import org.dockbox.hartshorn.util.GenericType;
import org.dockbox.hartshorn.util.option.Option;

/**
 * TODO: #1062 Add documentation
 *
 * @since 0.4.1
 *
 * @author Guus Lieben
 */
public interface ObjectMapper {

    <T> Option<T> read(String content, Class<T> type) throws ObjectMappingException;

    <T> Option<T> read(Path path, Class<T> type) throws ObjectMappingException;

    <T> Option<T> read(URL url, Class<T> type) throws ObjectMappingException;

    <T> Option<T> read(InputStream stream, Class<T> type) throws ObjectMappingException;

    default <T> Option<T> read(URI uri, Class<T> type) throws ObjectMappingException {
        try {
            return this.read(uri.toURL(), type);
        }
        catch (IOException e) {
            throw new ObjectMappingException("Failed to read object from URI", e);
        }
    }

    <T> Option<T> read(String content, GenericType<T> type) throws ObjectMappingException;

    <T> Option<T> read(Path path, GenericType<T> type) throws ObjectMappingException;

    <T> Option<T> read(URL url, GenericType<T> type) throws ObjectMappingException;

    <T> Option<T> read(InputStream stream, GenericType<T> type) throws ObjectMappingException;

    default <T> Option<T> read(URI uri, GenericType<T> type) throws ObjectMappingException {
        try {
            return this.read(uri.toURL(), type);
        }
        catch (IOException e) {
            throw new ObjectMappingException("Failed to read object from URI", e);
        }
    }

    <T> Option<T> update(T object, String content, Class<T> type) throws ObjectMappingException;

    <T> Option<T> update(T object, Path path, Class<T> type) throws ObjectMappingException;

    <T> Option<T> update(T object, URL url, Class<T> type) throws ObjectMappingException;

    <T> Option<T> update(T object, InputStream stream, Class<T> type) throws ObjectMappingException;

    default <T> Option<T> update(T object, URI uri, Class<T> type) throws ObjectMappingException {
        try {
            return this.update(object, uri.toURL(), type);
        }
        catch (IOException e) {
            throw new ObjectMappingException("Failed to read object from URI", e);
        }
    }

    Map<String, Object> flat(String content) throws ObjectMappingException;

    Map<String, Object> flat(Path path) throws ObjectMappingException;

    Map<String, Object> flat(URL url) throws ObjectMappingException;

    Map<String, Object> flat(InputStream stream) throws ObjectMappingException;

    default Map<String, Object> flat(URI uri) throws ObjectMappingException {
        try {
            return this.flat(uri.toURL());
        }
        catch (IOException e) {
            throw new ObjectMappingException("Failed to read object from URI", e);
        }
    }

    <T> boolean write(Path path, T content) throws ObjectMappingException;

    <T> boolean write(OutputStream outputStream, T content) throws ObjectMappingException;

    <T> String write(T content) throws ObjectMappingException;

    ObjectMapper fileType(FileFormat fileFormat);

    FileFormat fileType();

    ObjectMapper skipBehavior(JsonInclusionRule modifier);

}
