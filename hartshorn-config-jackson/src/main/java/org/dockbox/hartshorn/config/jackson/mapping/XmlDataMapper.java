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

package org.dockbox.hartshorn.config.jackson.mapping;

import com.fasterxml.jackson.databind.cfg.MapperBuilder;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import org.dockbox.hartshorn.config.FileFormat;
import org.dockbox.hartshorn.config.FileFormats;
import org.dockbox.hartshorn.config.jackson.JacksonDataMapper;

/**
 * A {@link JacksonDataMapper} that uses the XML format. This mapper uses Jackson's {@link XmlMapper} to
 * support {@link FileFormats#XML XML sources}.
 *
 * @since 0.4.9
 *
 * @author Guus Lieben
 */
public class XmlDataMapper implements JacksonDataMapper {

    @Override
    public FileFormat fileFormat() {
        return FileFormats.XML;
    }

    @Override
    public MapperBuilder<?, ?> get() {
        return XmlMapper.builder();
    }
}
