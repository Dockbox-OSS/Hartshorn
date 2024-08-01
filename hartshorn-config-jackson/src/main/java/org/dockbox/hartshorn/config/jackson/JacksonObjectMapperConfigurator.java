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

import com.fasterxml.jackson.databind.cfg.MapperBuilder;

import org.dockbox.hartshorn.config.FileFormat;
import org.dockbox.hartshorn.config.JsonInclusionRule;

/**
 * Configuration interface for {@link MapperBuilder}s. This allows for the configuration of the {@link MapperBuilder}
 * based on the {@link FileFormat} of the source and the {@link JsonInclusionRule} that is used.
 *
 * @see JacksonObjectMapper
 * @see MapperBuilder
 * @see FileFormat
 * @see JsonInclusionRule
 *
 * @since 0.4.12
 *
 * @author Guus Lieben
 */
public interface JacksonObjectMapperConfigurator {

    /**
     * Configures the provided {@link MapperBuilder} based on the provided {@link FileFormat} and {@link
     * JsonInclusionRule}.
     *
     * @param builder the builder to configure
     * @param format the format of the source
     * @param inclusionRule the inclusion rule to use
     * @return the configured builder
     */
    MapperBuilder<?, ?> configure(MapperBuilder<?, ?> builder, FileFormat format, JsonInclusionRule inclusionRule);

}
