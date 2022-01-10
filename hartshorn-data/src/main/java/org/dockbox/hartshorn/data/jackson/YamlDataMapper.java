/*
 * Copyright (C) 2020 Guus Lieben
 *
 * This framework is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 2.1 of the
 * License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See
 * the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library. If not, see {@literal<http://www.gnu.org/licenses/>}.
 */

package org.dockbox.hartshorn.data.jackson;

import com.fasterxml.jackson.databind.cfg.MapperBuilder;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLParser;

import org.dockbox.hartshorn.core.annotations.inject.ComponentBinding;
import org.dockbox.hartshorn.data.FileFormat;
import org.dockbox.hartshorn.data.FileFormats;

import javax.inject.Named;

@ComponentBinding(value = JacksonDataMapper.class, named = @Named("yml"))
public class YamlDataMapper implements JacksonDataMapper {
    @Override
    public FileFormat fileFormat() {
        return FileFormats.YAML;
    }

    @Override
    public MapperBuilder<?, ?> get() {
        final YAMLFactory yamlFactory = new YAMLFactory();
        yamlFactory.enable(YAMLGenerator.Feature.MINIMIZE_QUOTES);
        yamlFactory.disable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER);
        yamlFactory.disable(YAMLParser.Feature.EMPTY_STRING_AS_NULL);
        return YAMLMapper.builder(yamlFactory);
    }
}
