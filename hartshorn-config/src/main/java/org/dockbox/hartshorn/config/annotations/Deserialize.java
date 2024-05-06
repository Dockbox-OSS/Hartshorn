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

package org.dockbox.hartshorn.config.annotations;

import org.dockbox.hartshorn.config.FileFormats;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that the annotated method is able to deserialize from a given source.
 * If a {@link SerializationSource} is specified, the given source will be used to
 * deserialize. Otherwise, the first parameter of the method will be used to determine
 * the source.
 *
 * <p>If the parameter is a {@link String}, the string will be used as the source. If
 * the parameter is a {@link java.nio.file.Path} or {@link java.io.File}, the provided
 * path will be used as the source.
 *
 * <p>The method can return either a {@link org.dockbox.hartshorn.util.option.Option}, a
 * {@link java.util.Optional}, or the type it deserializes to. If it returns a
 * {@link org.dockbox.hartshorn.util.option.Option} or {@link java.util.Optional}, the
 * type is determined based on the type parameter.
 *
 * <p><pre>{@code
 * @Service
 * public interface PathPersistenceService {
 *    @Deserialize(path = @File("test"))
 *    PersistentElement readFromAnnotation();
 *
 *    @Deserialize
 *    Optional<PersistentElement> readFromString(String source);
 *
 *    @Deserialize
 *    Result<PersistentElement> readFromPath(Path source);
 * }
 * }</pre>
 *
 * <p>By default the file format is expected to be JSON. Different file formats can be
 * used through {@link #fileType()}.
 *
 * @author Guus Lieben
 * @since 0.4.1
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Deserialize {

    /**
     * The file format to use when deserializing.
     *
     * @return The file format to use when deserializing.
     */
    FileFormats fileType() default FileFormats.JSON;
}
