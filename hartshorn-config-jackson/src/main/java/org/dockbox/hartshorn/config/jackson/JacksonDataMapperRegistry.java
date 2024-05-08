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
import org.dockbox.hartshorn.util.option.Option;

/**
 * TODO: #1063 Add documentation
 *
 * @since 0.6.0
 *
 * @author Guus Lieben
 */
public interface JacksonDataMapperRegistry {

    void register(JacksonDataMapper mapper);

    boolean isCompatible(FileFormat format);

    Option<JacksonDataMapper> resolve(FileFormat format);
}
