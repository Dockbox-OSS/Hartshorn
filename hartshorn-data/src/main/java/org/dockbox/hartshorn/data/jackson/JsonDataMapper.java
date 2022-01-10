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
