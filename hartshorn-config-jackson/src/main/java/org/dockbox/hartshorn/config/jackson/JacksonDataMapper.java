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

package org.dockbox.hartshorn.config.jackson;

import org.dockbox.hartshorn.config.FileFormat;

import com.fasterxml.jackson.databind.cfg.MapperBuilder;

/**
 * A wrapper for a {@link MapperBuilder} that provides a {@link FileFormat} to indicate the format that the
 * {@link MapperBuilder} supports. This allows for a {@link org.dockbox.hartshorn.config.ObjectMapper} to
 * swap out the supported {@link MapperBuilder} based on the {@link FileFormat} of the source.
 *
 * @since 0.4.9
 *
 * @author Guus Lieben
 */
public interface JacksonDataMapper {

    /**
     * Returns the {@link FileFormat} that this {@link JacksonDataMapper} supports.
     *
     * @return The {@link FileFormat} that this {@link JacksonDataMapper} supports.
     */
    FileFormat fileFormat();

    /**
     * Returns the {@link MapperBuilder} that this {@link JacksonDataMapper} wraps.
     *
     * @return The {@link MapperBuilder} that this {@link JacksonDataMapper} wraps.
     */
    MapperBuilder<?, ?> get();
}
