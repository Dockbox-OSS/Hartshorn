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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.dockbox.hartshorn.config.FileFormat;
import org.dockbox.hartshorn.util.option.Option;

/**
 * TODO: #1063 Add documentation
 *
 * @since 0.6.0
 *
 * @author Guus Lieben
 */
public class SimpleJacksonDataMapperRegistry implements JacksonDataMapperRegistry {

    private final Map<FileFormat, JacksonDataMapper> dataMappers = new ConcurrentHashMap<>();

    @Override
    public void register(JacksonDataMapper mapper) {
        this.dataMappers.put(mapper.fileFormat(), mapper);
    }

    @Override
    public boolean isCompatible(FileFormat format) {
        return dataMappers.containsKey(format);
    }

    @Override
    public Option<JacksonDataMapper> resolve(FileFormat format) {
        return Option.of(this.dataMappers.get(format));
    }
}
