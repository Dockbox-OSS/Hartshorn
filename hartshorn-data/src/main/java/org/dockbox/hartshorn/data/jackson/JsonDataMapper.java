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

package org.dockbox.hartshorn.data.jackson;

import com.fasterxml.jackson.databind.cfg.MapperBuilder;
import com.fasterxml.jackson.databind.json.JsonMapper;

import org.dockbox.hartshorn.core.annotations.inject.ComponentBinding;
import org.dockbox.hartshorn.data.FileFormat;
import org.dockbox.hartshorn.data.FileFormats;

import javax.inject.Named;

@ComponentBinding(value = JacksonDataMapper.class, named = @Named("json"))
public class JsonDataMapper implements JacksonDataMapper{

    @Override
    public FileFormat fileFormat() {
        return FileFormats.JSON;
    }

    @Override
    public MapperBuilder<?, ?> get() {
        return JsonMapper.builder();
    }
}
